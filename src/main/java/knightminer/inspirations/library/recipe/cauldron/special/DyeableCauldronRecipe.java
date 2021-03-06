package knightminer.inspirations.library.recipe.cauldron.special;

import com.google.gson.JsonObject;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.DynamicFinishedRecipe;
import knightminer.inspirations.library.recipe.RecipeSerializer;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.inventory.IModifyableCauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.util.DisplayCauldronRecipe;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Shared logic between dyeing and clearing dye
 */
public abstract class DyeableCauldronRecipe implements ICauldronRecipe, IMultiRecipe<DisplayCauldronRecipe> {
  private final ResourceLocation id;
  private final Ingredient ingredient;
  private List<DisplayCauldronRecipe> displayRecipes;

  /**
   * Recipe to remove dye from an item
   * @param id          Recipe ID
   * @param ingredient  Ingredient for input
   */
  public DyeableCauldronRecipe(ResourceLocation id, Ingredient ingredient) {
    this.id = id;
    this.ingredient = ingredient;
  }

  @Override
  public boolean matches(ICauldronInventory inv, World worldIn) {
    ItemStack stack = inv.getStack();
    return inv.getLevel() >= THIRD && ingredient.test(stack) && matches(inv.getContents(), stack);
  }

  /**
   * Logic to check that the given contents match after ingredient and level check
   * @param contents  Contents to check
   * @param stack     Item stack to check
   * @return  True if the recipe matches
   */
  protected abstract boolean matches(ICauldronContents contents, ItemStack stack);

  @Override
  public void handleRecipe(IModifyableCauldronInventory inventory) {
    // update a single item from the stack
    inventory.setOrGiveStack(updateColor(inventory.getContents(), inventory.splitStack(1)));

    // remove a level of dye
    inventory.addLevel(-THIRD);

    // play sound
    inventory.playSound(SoundEvents.ENTITY_GENERIC_SPLASH);
  }

  /**
   * Logic to update the item stack color
   * @param contents  Cauldron contents
   * @param stack     Stack to update
   * @return  Updated stack
   */
  protected abstract ItemStack updateColor(ICauldronContents contents, ItemStack stack);

  @Override
  public ResourceLocation getId() {
    return id;
  }

  /* Display */

  /**
   * Gets a stream of recipes for the given input stack
   * @param stack  Input stack
   * @return  Stream of recipes for display
   */
  protected abstract Stream<DisplayCauldronRecipe> getDisplayRecipes(ItemStack stack);

  @Override
  public List<DisplayCauldronRecipe> getRecipes() {
    if (displayRecipes == null) {
      displayRecipes = Arrays.stream(ingredient.getMatchingStacks()).flatMap(this::getDisplayRecipes).collect(Collectors.toList());
    }
    return displayRecipes;
  }

  /**
   * Recipe to dye a dyeable
   */
  public static class Dye extends DyeableCauldronRecipe {
    private List<DisplayCauldronRecipe> displayRecipes;
    public Dye(ResourceLocation id, Ingredient ingredient) {
      super(id, ingredient);
    }

    @Override
    protected boolean matches(ICauldronContents contents, ItemStack stack) {
      return contents.get(CauldronContentTypes.COLOR).filter(color -> !Util.hasColor(stack) || Util.getColor(stack) != color).isPresent();
    }

    @Override
    protected ItemStack updateColor(ICauldronContents contents, ItemStack stack) {
      int color = contents.get(CauldronContentTypes.COLOR).orElse(-1);
      return Util.setColor(stack, color);
    }

    @Override
    protected Stream<DisplayCauldronRecipe> getDisplayRecipes(ItemStack stack) {
      List<ItemStack> inputs = Collections.singletonList(stack);
      return Arrays.stream(DyeColor.values())
                   .map(color -> DisplayCauldronRecipe.builder(THIRD, 0)
                                                      .setItemInputs(inputs)
                                                      .setContentInputs(CauldronContentTypes.DYE.of(color))
                                                      .setItemOutput(Util.setColor(stack.copy(), color.getColorValue()))
                                                      .build());
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CAULDRON_DYE_DYEABLE;
    }
  }

  /**
   * Recipe to clear a dyeable
   */
  public static class Clear extends DyeableCauldronRecipe {
    public Clear(ResourceLocation id, Ingredient ingredient) {
      super(id, ingredient);
    }

    @Override
    protected boolean matches(ICauldronContents contents, ItemStack stack) {
      return contents.contains(CauldronContentTypes.FLUID, Fluids.WATER) && Util.hasColor(stack);
    }

    @Override
    protected ItemStack updateColor(ICauldronContents contents, ItemStack stack) {
      return Util.clearColor(stack);
    }

    @Override
    protected Stream<DisplayCauldronRecipe> getDisplayRecipes(ItemStack stack) {
      List<ItemStack> inputs = Arrays.stream(DyeColor.values()).map(color -> Util.setColor(stack.copy(), color.getColorValue())).collect(Collectors.toList());
      return Stream.of(DisplayCauldronRecipe.builder(THIRD, 0)
                                            .setItemInputs(inputs)
                                            .setContentInputs(DisplayCauldronRecipe.WATER_CONTENTS.get())
                                            .setItemOutput(Util.clearColor(stack.copy()))
                                            .build());
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CAULDRON_CLEAR_DYEABLE;
    }
  }

  /**
   * Serializer for the recipe
   */
  public static class Serializer extends RecipeSerializer<DyeableCauldronRecipe> {
    private final BiFunction<ResourceLocation, Ingredient, DyeableCauldronRecipe> factory;
    public Serializer(BiFunction<ResourceLocation, Ingredient, DyeableCauldronRecipe> factory) {
      this.factory = factory;
    }

    @Override
    public DyeableCauldronRecipe read(ResourceLocation id, JsonObject json) {
      return factory.apply(id, Ingredient.deserialize(JsonHelper.getElement(json, "ingredient")));
    }

    @Nullable
    @Override
    public DyeableCauldronRecipe read(ResourceLocation id, PacketBuffer buffer) {
      return factory.apply(id, Ingredient.read(buffer));
    }

    @Override
    public void write(PacketBuffer buffer, DyeableCauldronRecipe recipe) {
      recipe.ingredient.write(buffer);
    }
  }

  /**
   * Finished recipe for data gen
   */
  public static class FinishedRecipe extends DynamicFinishedRecipe {
    private final Ingredient ingredient;
    private FinishedRecipe(ResourceLocation id, Ingredient ingredient, Serializer serializer) {
      super(id, serializer);
      this.ingredient = ingredient;
    }

    /**
     * Creates a recipe for a generic serializer
     * @param id          Recipe ID
     * @param ingredient  Recipe ingredient
     * @param serializer  Recipe serializer
     * @return  Recipe
     */
    public static FinishedRecipe recipe(ResourceLocation id, Ingredient ingredient, Serializer serializer) {
      return new FinishedRecipe(id, ingredient, serializer);
    }

    /**
     * Creates a dyeing recipe
     * @param id          Recipe ID
     * @param ingredient  Recipe ingredient
     * @return  Recipe
     */
    public static FinishedRecipe dye(ResourceLocation id, Ingredient ingredient) {
      return new FinishedRecipe(id, ingredient, RecipeSerializers.CAULDRON_DYE_DYEABLE);
    }

    /**
     * Creates a dye clearing recipe
     * @param id          Recipe ID
     * @param ingredient  Recipe ingredient
     * @return  Recipe
     */
    public static FinishedRecipe clear(ResourceLocation id, Ingredient ingredient) {
      return new FinishedRecipe(id, ingredient, RecipeSerializers.CAULDRON_CLEAR_DYEABLE);
    }

    @Override
    public void serialize(JsonObject json) {
      json.add("ingredient", ingredient.serialize());
    }
  }
}
