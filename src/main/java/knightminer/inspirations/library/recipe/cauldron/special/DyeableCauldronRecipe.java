package knightminer.inspirations.library.recipe.cauldron.special;

import com.google.gson.JsonObject;
import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.library.recipe.DynamicFinishedRecipe;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.inventory.IModifyableCauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.util.DisplayCauldronRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.AbstractRecipeSerializer;
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
  public boolean matches(ICauldronInventory inv, Level worldIn) {
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
    inventory.playSound(SoundEvents.GENERIC_SPLASH);
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
      displayRecipes = Arrays.stream(ingredient.getItems()).flatMap(this::getDisplayRecipes).collect(Collectors.toList());
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
      return contents.get(CauldronContentTypes.COLOR).filter(color -> !MiscUtil.hasColor(stack) || MiscUtil.getColor(stack) != color).isPresent();
    }

    @Override
    protected ItemStack updateColor(ICauldronContents contents, ItemStack stack) {
      int color = contents.get(CauldronContentTypes.COLOR).orElse(-1);
      return MiscUtil.setColor(stack, color);
    }

    @Override
    protected Stream<DisplayCauldronRecipe> getDisplayRecipes(ItemStack stack) {
      List<ItemStack> inputs = Collections.singletonList(stack);
      return Arrays.stream(DyeColor.values())
                   .map(color -> DisplayCauldronRecipe.builder(THIRD, 0)
                                                      .setItemInputs(inputs)
                                                      .setContentInputs(CauldronContentTypes.DYE.of(color))
                                                      .setItemOutput(MiscUtil.setColor(stack.copy(), MiscUtil.getColor(color)))
                                                      .build());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
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
      return contents.contains(CauldronContentTypes.FLUID, Fluids.WATER) && MiscUtil.hasColor(stack);
    }

    @Override
    protected ItemStack updateColor(ICauldronContents contents, ItemStack stack) {
      return MiscUtil.clearColor(stack);
    }

    @Override
    protected Stream<DisplayCauldronRecipe> getDisplayRecipes(ItemStack stack) {
      List<ItemStack> inputs = Arrays.stream(DyeColor.values()).map(color -> MiscUtil.setColor(stack.copy(), MiscUtil.getColor(color))).collect(Collectors.toList());
      return Stream.of(DisplayCauldronRecipe.builder(THIRD, 0)
                                            .setItemInputs(inputs)
                                            .setContentInputs(DisplayCauldronRecipe.WATER_CONTENTS.get())
                                            .setItemOutput(MiscUtil.clearColor(stack.copy()))
                                            .build());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CAULDRON_CLEAR_DYEABLE;
    }
  }

  /**
   * Serializer for the recipe
   */
  public static class Serializer extends AbstractRecipeSerializer<DyeableCauldronRecipe> {
    private final BiFunction<ResourceLocation, Ingredient, DyeableCauldronRecipe> factory;
    public Serializer(BiFunction<ResourceLocation, Ingredient, DyeableCauldronRecipe> factory) {
      this.factory = factory;
    }

    @Override
    public DyeableCauldronRecipe fromJson(ResourceLocation id, JsonObject json) {
      return factory.apply(id, Ingredient.fromJson(JsonHelper.getElement(json, "ingredient")));
    }

    @Nullable
    @Override
    public DyeableCauldronRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
      return factory.apply(id, Ingredient.fromNetwork(buffer));
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, DyeableCauldronRecipe recipe) {
      recipe.ingredient.toNetwork(buffer);
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
    public void serializeRecipeData(JsonObject json) {
      json.add("ingredient", ingredient.toJson());
    }
  }
}
