package knightminer.inspirations.library.recipe.anvil;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.BlockIngredient;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.library.recipe.RecipeTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootSerializers;
import net.minecraft.loot.RandomRanges;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.Property;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.state.StateContainer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AnvilRecipe implements IRecipe<AnvilInventory> {
  // These are uppercase, which isn't allowed for namespaces so they won't conflict.
  /** Used for property values or block to indicate it should be copied from the existing state. */
  public static final String FROM_INPUT = "INPUT";

  /** The kind of block transform to apply. */
  public enum ConvertType {
    /** Mine the block and drop contents. */
    SMASH,
    /** Convert to the specified blockstate. */
    TRANSFORM,
    /** Don't change the block, only change state. */
    KEEP,
  }

  // GSON instance setup to handle loot tables.
  public static final Gson GSON_LOOT = LootSerializers.func_237387_b_()
          .registerTypeAdapter(LootPool.class, new LootPoolSerializer())
          .create();

  /**
   * A list of all recipes, sorted by ingredient count.
   * That ensures recipes with more ingredients are preferred.
   * */
  @Nullable
  private static List<AnvilRecipe> sortedRecipes = null;

  private final ResourceLocation id;
  private final NonNullList<Ingredient> ingredients;
  private final String group;
  // Count of the number of non-block ingredients, for JEI.
  private final int itemIngredientCount;

  /** The block to produce. */
  private final ConvertType blockConvert;
  /** The state to produce, if blockConvert==TRANSFORM. */
  private final Block transformResult;
  /**
   * Loot table pools used for item generation.
   */
  private final List<LootPool> lootPools;
  // For JEI as well, cached list of items the pools generate.
  @Nullable
  private List<LootResult>lootResult;

  /**
   * Properties to assign to the result, unparsed.
   * If value == FROM_INPUT, copy over.
   */
  private final List<Pair<String, String>> properties;

  /** After matching, holds the number of times each item was used. */
  @Nullable
  private int[] used = null;

  public AnvilRecipe(
          ResourceLocation id,
          String group,
          NonNullList<Ingredient> ingredients,
          ConvertType conversion,
          Block transformResult,
          List<LootPool> lootPools,
          List<Pair<String, String>> properties
  ) {
    this.id = id;
    this.group = group;
    this.ingredients = ingredients;
    this.lootPools = lootPools;
    this.blockConvert = conversion;
    this.transformResult = transformResult;
    this.properties = properties;
    this.itemIngredientCount = (int)ingredients.stream().filter(obj -> !(obj instanceof BlockIngredient)).count();
    this.lootResult = null;
  }

  public static List<AnvilRecipe> getSortedRecipes(@Nonnull World world) {
    if (sortedRecipes == null) {
      // On first call, or after datapack reload sort all the recipes.
      sortedRecipes = world.getRecipeManager()
              .getRecipes(RecipeTypes.ANVIL)
              .values()
              .stream()
              .map(AnvilRecipe.class::cast)
              .sorted(Comparator.comparingInt(
                      (AnvilRecipe rec) -> rec.getIngredients().size()
              ).reversed())
              .collect(Collectors.toList());
    }
    return Collections.unmodifiableList(sortedRecipes);
  }

  /**
   * Return the appropriate recipe for this input.
   * This is equivalent to the RecipeManager call, but prefers recipes with more ingredients.
   */
  public static Optional<AnvilRecipe> matchRecipe(@Nonnull AnvilInventory inv, @Nonnull World world) {
    for(AnvilRecipe recipe: getSortedRecipes(world)) {
      if (recipe.matches(inv, world)) {
        return Optional.of(recipe);
      }
    }
    return Optional.empty();
  }

  /**
   * Register a reload listener which clears the sortedRecipes cache.
   * We can't do the cache in the listener since the recipe manager may run after us.
   */
  public static void onServerStart(FMLServerStartingEvent event) {
    IReloadableResourceManager resman = (IReloadableResourceManager) event.getServer().getDataPackRegistries().getResourceManager();
    resman.addReloadListener(
            (stage, resMan, prepProp, reloadProf, bgExec, gameExec) -> CompletableFuture
                    .runAsync(() -> sortedRecipes = null, gameExec)
                    .thenCompose(stage::markCompleteAwaitingOthers)
    );
  }

  /**
   * Test if the given recipe matches the input.
   * When successful, inv.used is modified to reflect the used items.
   */
  @Override
  public boolean matches(@Nonnull AnvilInventory inv, @Nonnull World worldIn) {
    // Used is set to true if that item was used in this recipe. First reset it.
    used = new int[inv.getItems().size()];
    Arrays.fill(used, 0);
    boolean result = ingredients.stream().allMatch(ing -> checkIngredient(inv, ing));
    if (!result) {
      // Clear this, since it's not useful.
      used = null;
    }
    return result;
  }

  private boolean checkIngredient(AnvilInventory inv, Ingredient ing) {
    assert this.used != null;
    int[] used = this.used.clone();

    if (ing instanceof BlockIngredient) {
      // It's a block, just test the state.
      return ((BlockIngredient) ing).testBlock(inv.getState());
    } else if (ing instanceof CompoundIngredient) {
      for(Ingredient subIng: ((CompoundIngredient) ing).getChildren()) {
        if (checkIngredient(inv, subIng)) {
          // Keep the state for this one.
          return true;
        }
        // Restore the state, since the compound didn't match.
        this.used = used.clone();
      }
      return false;
    } else {
      // It's an item. We want to see if any item matches,
      // but not reuse items twice - since they're consumed.
      boolean found = false;
      for(int i = 0; i < this.used.length; i++) {
        ItemStack item = inv.getItems().get(i);
        if (this.used[i] < item.getCount() && ing.test(item)) {
          this.used[i]++;
          found = true;
          break;
        }
      }
      if (!found) {
        // Restore the state, since the ingredient didn't match.
        this.used = used.clone();
      }
      return found;
    }
  }

  /** If a loot table is defined, suppress the drops from the block.
   * The user can do those themselves if they want them.
   */
  public boolean allowRegularDrops() {
    return lootPools.size() == 0;
  }

  /** Return the type of conversion this does. */
  @Nonnull
  public ConvertType getConversion() {
    return blockConvert;
  }

  /** Return the block produced, if a TRANSFORM recipe. */
  @Nonnull
  public Block getTransformResult() {
    return blockConvert == ConvertType.TRANSFORM ? transformResult : Blocks.AIR;
  }

  /**
   *  Equivalent to getCraftingResult, but for blocks.
   * @param inv The inventory that was matched.
   * @return The block which should replace the existing one.
   */
  @Nonnull
  public BlockState getBlockResult(@Nonnull AnvilInventory inv) {
    BlockState state;
    switch(blockConvert) {
      case TRANSFORM:
        state = transformResult.getDefaultState();
        break;
      case KEEP:
        state = inv.getState();
        break;
      case SMASH:
        return Blocks.AIR.getDefaultState();
      default:
        throw new IllegalStateException(String.format("Unexpected blockConvert value %s for recipe %s", blockConvert, id));
    }

    StateContainer<Block, BlockState> cont = state.getBlock().getStateContainer();
    StateContainer<Block, BlockState> inpContainer = inv.getState().getBlock().getStateContainer();

    for(Pair<String, String> prop: properties) {
      String key = prop.getFirst();
      String value = prop.getSecond();
      if (value.equals(FROM_INPUT)) {
        Property<?> inpProp = inpContainer.getProperty(key);
        if (inpProp == null) {
          InspirationsRegistry.log.warn(
                  "No property \"{}\" to copy from block {} in Anvil recipe {}!",
                  key, inv.getState().getBlock().getRegistryName(), id
          );
          continue;
        }
        // Convert to a string, so differing types and identical but distinct IProperty objects
        // still work.
        value = getProperty(inv.getState(), inpProp);
      }
      Property<?> targProp = cont.getProperty(key);
      if(targProp == null) {
        InspirationsRegistry.log.warn(
                "Property \"{}\" is not valid for block {} in Anvil recipe {}!",
                key, state.getBlock().getRegistryName(), id
        );
        continue;
      }
      state = setProperty(state, targProp, value);
    }
    return state;
  }

  /**
   * Consume the items used by the recipe, killing empty items.
   * @param items Item entities involved.
   */
  public void consumeItemEnts(List<ItemEntity> items) {
    if (used == null || items.size() != used.length) {
      return;
    }
    for(int i = 0; i < items.size(); i++) {
      if(used[i] > 0) {
        ItemEntity item = items.get(i);
        ItemStack newStack = item.getItem().copy();
        newStack.shrink(used[i]);
        if(newStack.isEmpty()) {
          item.remove();
        } else {
          item.setItem(newStack);
        }
      }
    }
  }
  /**
   * Consume the items used by the recipe.
   * @param items ItemStacks in the same order as passed to match().
   */
  public void consumeItemStacks(List<ItemStack> items) {
    if (used == null || items.size() != used.length) {
      return;
    }
    for(int i = 0; i < items.size(); i++) {
      items.get(i).shrink(used[i]);
    }
  }

  /**
   * Generate the items produced by this recipe.
   */
  public void generateItems(ServerWorld world, BlockPos pos, Consumer<ItemStack> itemConsumer) {
    LootContext context = new LootContext.Builder(world)
            .withParameter(LootParameters.field_237457_g_, Vector3d.copy(pos).add(0.5, 0.5, 0.5))
            .withParameter(LootParameters.BLOCK_STATE, world.getBlockState(pos))
            // Not a tool, but the thing that makes the most sense...
            .withParameter(LootParameters.TOOL, new ItemStack(Items.ANVIL))
            .withNullableParameter(LootParameters.BLOCK_ENTITY, world.getTileEntity(pos))
            .build(LootParameterSets.BLOCK);
    for (LootPool pool: lootPools) {
      pool.generate(itemConsumer, context);
    }
  }

  /**
   * Setting the property needs a generic arg, so the parsed value can have the same type as the property.
   */
  private <T extends Comparable<T>> BlockState setProperty(BlockState state, Property<T>prop, String value) {
    Optional<T> parsedValue = prop.parseValue(value);
    if (parsedValue.isPresent()) {
      return state.with(prop, parsedValue.get());
    } else {
      InspirationsRegistry.log.warn(
              "Invalid value \"{}\" for block property {} of {} in anvil recipe {}!",
              value, prop.getName(), state.getBlock().getRegistryName(), id);
      return state;
    }
  }

  /**
   * Getting the property needs a generic arg, so the parsed value can have the same type as the property.
   */
  private <T extends Comparable<T>> String getProperty(BlockState state, Property<T> prop) {
    return state.get(prop).toString();
  }

  /**
   * Not used, call getBlockResult.
   * @param inv The inventory that was matched.
   * @deprecated Use getBlockResult
   */
  @Nonnull
  @Override
  public ItemStack getCraftingResult(@Nonnull AnvilInventory inv) {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean canFit(int width, int height) {
    return true;
  }

  @Nonnull
  @Override
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }

  @Nonnull
  @Override
  public NonNullList<ItemStack> getRemainingItems(@Nonnull AnvilInventory inv) {
    return NonNullList.create();
  }

  @Nonnull
  @Override
  public NonNullList<Ingredient> getIngredients() {
    return ingredients;
  }

  public int getItemIngredientCount() {
    return itemIngredientCount;
  }

  /**
   * Navigate through the loot pools, collecting potential output items.
   */
  public List<LootResult> getRepresentativeLoot() {
    if (lootResult == null) {
      lootResult = LootResult.computePoolItems(lootPools);
    }
    return lootResult;
  }

  /**
   * The recipe book cannot accept this.
   */
  @Override
  public boolean isDynamic() {
    return true;
  }

  @Nonnull
  @Override
  public String getGroup() {
    return group;
  }

  @Nonnull
  @Override
  public ItemStack getIcon() {
    return new ItemStack(Items.ANVIL);
  }

  @Nonnull
  @Override
  public ResourceLocation getId() {
    return id;
  }

  @Nonnull
  @Override
  public IRecipeSerializer<?> getSerializer() {
    return RecipeSerializers.ANVIL_SMASHING;
  }

  @Nonnull
  @Override
  public IRecipeType<?> getType() {
    return RecipeTypes.ANVIL;
  }

  /**
   * Forge hooks into the LootPool constructor to add a name, whcih breaks if you try deserializing by itself.
   * So use the builder instead.
   */
  private static class LootPoolSerializer extends LootPool.Serializer {
    @Nonnull
    public LootPool deserialize(@Nonnull JsonElement jsonElement, @Nonnull Type type, @Nonnull JsonDeserializationContext ctx) throws JsonParseException {
      LootPool.Builder builder = LootPool.builder();
      JsonObject json = JSONUtils.getJsonObject(jsonElement, "loot pool");
      builder.entries = Lists.newArrayList(JSONUtils.deserializeClass(json, "entries", ctx, LootEntry[].class));
      builder.conditions = Lists.newArrayList(JSONUtils.deserializeClass(json, "conditions", new ILootCondition[0], ctx, ILootCondition[].class));
      builder.functions = Lists.newArrayList(JSONUtils.deserializeClass(json, "functions", new ILootFunction[0], ctx, ILootFunction[].class));
      builder.rolls(RandomRanges.deserialize(json.get("rolls"), ctx));
      if (json.has("bonus_rolls")) {
        RandomValueRange bonus_rolls = JSONUtils.deserializeClass(json, "bonus_rolls", new RandomValueRange(0.0F, 0.0F), ctx, RandomValueRange.class);
        builder.bonusRolls(bonus_rolls.getMin(), bonus_rolls.getMax());
      }
      return builder.build();
    }
  }

  public static class Serializer
          extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>>
          implements IRecipeSerializer<AnvilRecipe>
  {
    @Nonnull
    @Override
    public AnvilRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
      String group = JSONUtils.getString(json, "group", "");
      NonNullList<Ingredient> inputs = NonNullList.create();
      JsonArray inputJSON = JSONUtils.getJsonArray(json, "ingredients");
      for(int i = 0; i < inputJSON.size(); i++) {
        Ingredient ingredient = Ingredient.deserialize(inputJSON.get(i));
        if (!ingredient.hasNoMatchingItems()) {
          inputs.add(ingredient);
        }
      }

      // Generate the output blockstate.
      JsonObject result = JSONUtils.getJsonObject(json, "result");

      Block block = Blocks.AIR;
      ConvertType convertType;
      if (result.has("block")) {
        convertType = ConvertType.TRANSFORM;
        String blockName = JSONUtils.getString(result, "block");
        block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
        if(block == null) {
          throw new JsonParseException("Unknown block \"" + blockName + "\"");
        }
        // Only treat vanilla air specially, users might want to spawn modded "air" blocks
        // specifically for other reasons.
        if(block == Blocks.AIR || block == Blocks.CAVE_AIR || block == Blocks.VOID_AIR) {
          convertType = ConvertType.SMASH;
        }
      } else {
        convertType = ConvertType.KEEP;
      }

      JsonObject props = JSONUtils.getJsonObject(result, "properties", new JsonObject());
      List<Pair<String, String>> propsMap = new ArrayList<>();
      for(Map.Entry<String, JsonElement> entry: props.entrySet()) {
        if (!entry.getValue().isJsonPrimitive()) {
          throw new JsonParseException("Expected simple value for property \"" + entry.getKey() + "\", but got a " + entry.getValue().getClass().getSimpleName());
        }
        propsMap.add(Pair.of(entry.getKey(), entry.getValue().getAsString()));
      }

      if (convertType == ConvertType.KEEP && props.size() == 0) {
        throw new JsonParseException("Block result must either change block type or alter properties!");
      }

      List<LootPool> lootPools = Collections.emptyList();
      if (result.has("pools")) {
        lootPools = new ArrayList<>();
        JsonArray pools = JSONUtils.getJsonArray(result, "pools");
        for (JsonElement pool: pools) {
          lootPools.add(GSON_LOOT.fromJson(pool, LootPool.class));
        }
      }

      return new AnvilRecipe(recipeId, group, inputs, convertType, block, lootPools, propsMap);
    }

    @Nullable
    @Override
    public AnvilRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
      ConvertType convertType = buffer.readEnumValue(ConvertType.class);
      int ingredientCount = buffer.readVarInt();
      int propsCount = buffer.readVarInt();
      int poolsCount = buffer.readVarInt();
      String group = buffer.readString();

      Block blockResult = Blocks.AIR;
      if (convertType == ConvertType.TRANSFORM) {
        // Should never be missing, since we've already validated it.
        blockResult = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(buffer.readString()));
      }

      NonNullList<Ingredient> inputs = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
      for(int i = 0; i < ingredientCount; i++) {
        inputs.set(i, Ingredient.read(buffer));
      }
      List<Pair<String, String>> props = Collections.emptyList();
      if (propsCount > 0) {
        props = new ArrayList<>(propsCount);
        for(int i = 0; i < propsCount; i++) {
          props.add(Pair.of(buffer.readString(), buffer.readString()));
        }
      }
      List<LootPool> lootPools = Collections.emptyList();
      if (poolsCount > 0) {
        lootPools = new ArrayList<>(poolsCount);
        for(int i = 0; i < poolsCount; i++) {
          CompoundNBT nbt = buffer.readCompoundTag();
          JsonElement json = Dynamic.convert(NBTDynamicOps.INSTANCE, JsonOps.INSTANCE, nbt);
          lootPools.add(GSON_LOOT.fromJson(json, LootPool.class));
        }
      }
      return new AnvilRecipe(recipeId, group, inputs, convertType, blockResult, lootPools, props);
    }

    @Override
    public void write(PacketBuffer buffer, AnvilRecipe recipe) {
      buffer.writeEnumValue(recipe.blockConvert);
      buffer.writeVarInt(recipe.ingredients.size());
      buffer.writeVarInt(recipe.properties.size());
      buffer.writeVarInt(recipe.lootPools.size());
      buffer.writeString(recipe.group);

      // We only need block type when transforming, so don't bother writing otherwise.
      if (recipe.blockConvert == ConvertType.TRANSFORM) {
        buffer.writeString(recipe.transformResult.getRegistryName().toString());
      }
      for(Ingredient ingredient: recipe.ingredients) {
        ingredient.write(buffer);
      }
      for(Pair<String, String> prop: recipe.properties) {
        buffer.writeString(prop.getFirst());
        buffer.writeString(prop.getSecond());
      }
      // This is a bit expensive, but loot pools should be fairly rare.
      for (LootPool pool: recipe.lootPools) {
        JsonElement json = GSON_LOOT.toJsonTree(pool);
        INBT nbt = Dynamic.convert(JsonOps.INSTANCE, NBTDynamicOps.INSTANCE, json);
        buffer.writeCompoundTag((CompoundNBT) nbt);
      }
    }
  }
}
