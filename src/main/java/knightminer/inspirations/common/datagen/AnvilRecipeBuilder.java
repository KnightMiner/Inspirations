package knightminer.inspirations.common.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.BlockIngredient;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.library.recipe.anvil.AnvilRecipe;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.state.Property;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class AnvilRecipeBuilder {
  private final List<Ingredient> ingredients;
  private final List<Pair<String, String>> properties;
  private String group = "";
  private final AnvilRecipe.ConvertType convertType;
  private final Block result;
  private final List<LootPool> pools;

  private AnvilRecipeBuilder(AnvilRecipe.ConvertType convType, Block res) {
    convertType = convType;
    result = res;
    ingredients = new ArrayList<>();
    properties = new ArrayList<>();
    pools = new ArrayList<>();
  }

  public static AnvilRecipeBuilder copiesInput() {
    return new AnvilRecipeBuilder(AnvilRecipe.ConvertType.KEEP, Blocks.AIR);
  }

  public static AnvilRecipeBuilder places(@Nonnull Block block) {
    if (block == Blocks.AIR || block == Blocks.CAVE_AIR || block == Blocks.VOID_AIR) {
      throw new IllegalArgumentException("Use smashes() instead, to be more clear.");
    }
    return new AnvilRecipeBuilder(AnvilRecipe.ConvertType.TRANSFORM, block);
  }

  /** Mines block */
  public static AnvilRecipeBuilder smashes() {
    return new AnvilRecipeBuilder(AnvilRecipe.ConvertType.SMASH, Blocks.AIR);
  }

  public AnvilRecipeBuilder group(String name) {
    group = name;
    return this;
  }

  public AnvilRecipeBuilder addIngredient(Ingredient ing) {
    ingredients.add(ing);
    return this;
  }

  public AnvilRecipeBuilder addIngredient(Block block) {
    ingredients.add(new BlockIngredient.BlockIngredientList(Collections.singletonList(block), StatePropertiesPredicate.EMPTY));
    return this;
  }

  public AnvilRecipeBuilder addIngredient(Block block, StatePropertiesPredicate pred) {
    ingredients.add(new BlockIngredient.BlockIngredientList(Collections.singletonList(block), pred));
    return this;
  }

  public AnvilRecipeBuilder addIngredient(ITag.INamedTag<Block> blockTag) {
    ingredients.add(new BlockIngredient.TaggedBlockIngredient(blockTag, StatePropertiesPredicate.EMPTY));
    return this;
  }

  public AnvilRecipeBuilder addIngredient(ITag.INamedTag<Block> blockTag, StatePropertiesPredicate pred) {
    ingredients.add(new BlockIngredient.TaggedBlockIngredient(blockTag, pred));
    return this;
  }

  public AnvilRecipeBuilder addIngredient(IItemProvider item) {
    ingredients.add(Ingredient.fromItems(item));
    return this;
  }

  public AnvilRecipeBuilder addLoot(LootPool pool) {
    pools.add(pool);
    return this;
  }

  public AnvilRecipeBuilder addLoot(LootPool.Builder pool) {
    pools.add(pool.build());
    return this;
  }

  public AnvilRecipeBuilder addLoot(LootEntry.Builder<?> entry) {
    pools.add(LootPool.builder().addEntry(entry).build());
    return this;
  }

  public AnvilRecipeBuilder addLoot(IItemProvider item, int quantity) {
    return addLoot(ItemLootEntry.builder(item)
            .acceptFunction(SetCount.builder(ConstantRange.of(quantity)))
    );
  }

  public AnvilRecipeBuilder addLoot(IItemProvider item) {
    return addLoot(ItemLootEntry.builder(item));
  }

  public AnvilRecipeBuilder addTaggedItem(ITag<Item> itemTag) {
    ingredients.add(Ingredient.fromTag(itemTag));
    return this;
  }

  public AnvilRecipeBuilder addIngredient(IItemProvider item, int quantity) {
    for(int i = 0; i < quantity; i++) {
      ingredients.add(Ingredient.fromItems(item));
    }
    return this;
  }

  public AnvilRecipeBuilder copiesProperty(Property<?> prop) {
    properties.add(Pair.of(prop.getName(), AnvilRecipe.FROM_INPUT));
    return this;
  }

  public <T extends Comparable<T>> AnvilRecipeBuilder setsProp(Property<T> prop, T value) {
    properties.add(Pair.of(prop.getName(), value.toString()));
    return this;
  }

  /**
   * Use IProperty versions if possible to ensure validity!
   */
  public AnvilRecipeBuilder copiesPropertyUnsafe(String prop) {
    properties.add(Pair.of(prop, AnvilRecipe.FROM_INPUT));
    return this;
  }

  /**
   * Use IProperty versions if possible to ensure validity!
   */
  public AnvilRecipeBuilder setsPropUnsafe(String prop) {
    properties.add(Pair.of(prop, AnvilRecipe.FROM_INPUT));
    return this;
  }

  // Shortcuts for common property setups.
  public AnvilRecipeBuilder copiesStandardSlab() {
    return this
        .copiesProperty(BlockStateProperties.HALF)
        .copiesProperty(BlockStateProperties.WATERLOGGED);
  }

  // Shortcuts for common property setups.
  public AnvilRecipeBuilder copiesStandardStair() {
    return this
        .copiesProperty(BlockStateProperties.HALF)
        .copiesProperty(BlockStateProperties.STAIRS_SHAPE)
        .copiesProperty(BlockStateProperties.HORIZONTAL_FACING)
        .copiesProperty(BlockStateProperties.WATERLOGGED);
  }

  public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
    if (ingredients.size() == 0) {
      throw new IllegalStateException("Recipe must have at least one ingredient!");
    }
    if (convertType == AnvilRecipe.ConvertType.KEEP && properties.size() == 0) {
      throw new IllegalStateException("If recipe keeps input block, properties should be set to change it!");
    }
    if (convertType == AnvilRecipe.ConvertType.SMASH) {
      id = new ResourceLocation(id.getNamespace(), "anvil_smash_" + id.getPath());
    } else if (!id.getPath().contains("anvil")) {
      id = new ResourceLocation(id.getNamespace(), id.getPath() + "_from_anvil_smashing");
    }
    consumer.accept(new Finished(
        id,
        ingredients,
        convertType,
        result,
        pools,
        properties,
        group
    ));
  }

  public void build(Consumer<IFinishedRecipe> consumer, String id) {
    build(consumer, Inspirations.getResource(id));
  }

  // Same name as the item.
  public void build(Consumer<IFinishedRecipe> consumer) {
    ResourceLocation id;
    if (convertType == AnvilRecipe.ConvertType.KEEP) {
      throw new IllegalStateException("Save location required for recipe which copies input block!");
    } else if (convertType == AnvilRecipe.ConvertType.TRANSFORM) {
      id = result.getRegistryName();
    } else {
      // Copy the first block input's ID.
      id = null;
      for(Ingredient ing: ingredients) {
        if (ing instanceof BlockIngredient.BlockIngredientList) {
          List<Block> blocks = ((BlockIngredient.BlockIngredientList) ing).blocks;
          if (blocks.size() == 1) {
            id = blocks.get(0).getRegistryName();
          }
          break;
        } else if (ing instanceof BlockIngredient.TaggedBlockIngredient) {
          ITag<Block> tag = ((BlockIngredient.TaggedBlockIngredient) ing).tag;
          if(tag instanceof ITag.INamedTag) {
            id = ((ITag.INamedTag<Block>) tag).getName();
            break;
          }
        }
      }
    }
    if (id == null) {
      throw new IllegalStateException("Could not infer save location for smashing recipe!");
    }
    // Anvil smashing is always in our namespace.
    build(consumer, Inspirations.getResource(id.getPath()));
  }

  private static class Finished implements IFinishedRecipe {
    private final ResourceLocation id;
    private final List<Ingredient> ingredients;
    private final String group;
    private final AnvilRecipe.ConvertType convertType;
    private final Block result;
    private final List<LootPool> pools;
    // Properties to assign to the result
    private final List<Pair<String, String>> properties;

    private Finished(
        ResourceLocation id,
        List<Ingredient> ingredients,
        AnvilRecipe.ConvertType convertType,
        Block result,
        List<LootPool> pools,
        List<Pair<String, String>> properties,
        String group
    ) {
      this.id = id;
      this.ingredients = ingredients;
      this.group = group;
      this.convertType = convertType;
      this.result = result;
      this.pools = pools;
      this.properties = properties;
    }

    @Override
    public void serialize(@Nonnull JsonObject json) {
      if (!group.isEmpty()) {
        json.addProperty("group", group);
      }
      JsonObject result = new JsonObject();
      json.add("result", result);
      switch(convertType) {
        case SMASH:
          result.addProperty("block", Blocks.AIR.getRegistryName().toString());
          break;
        case TRANSFORM:
          result.addProperty("block", this.result.getRegistryName().toString());
          break;
      }
      if (properties.size() > 0) {
        JsonObject props = new JsonObject();
        properties.forEach(prop -> props.addProperty(prop.getFirst(), prop.getSecond()));
        result.add("properties", props);
      }
      if (pools.size() > 0) {
        JsonArray poolArray = new JsonArray();
        for(LootPool pool: pools) {
          poolArray.add(AnvilRecipe.GSON_LOOT.toJsonTree(pool));
        }
        result.add("pools", poolArray);
      }

      json.add("ingredients", ingredients.stream()
            .map(Ingredient::serialize)
            .collect(JsonArray::new, JsonArray::add, JsonArray::addAll));
    }

    @Nonnull
    @Override
    public ResourceLocation getID() {
      return id;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.ANVIL_SMASHING;
    }

    // We can't add a recipe book to the anvil, so there's no advancement.
    @Override
    public JsonObject getAdvancementJson() {
      return null;
    }

    @Override
    public ResourceLocation getAdvancementID() {
      return null;
    }
  }
}
