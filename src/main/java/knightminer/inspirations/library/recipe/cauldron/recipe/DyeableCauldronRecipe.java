package knightminer.inspirations.library.recipe.cauldron.recipe;

import com.google.gson.JsonObject;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.RecipeSerializer;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.inventory.IModifyableCauldronInventory;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

/**
 * Shared logic between dyeing and clearing dye
 */
public abstract class DyeableCauldronRecipe implements ICauldronRecipe {
  private final ResourceLocation id;
  private final Ingredient ingredient;

  public DyeableCauldronRecipe(ResourceLocation id, Ingredient ingredient) {
    this.id = id;
    this.ingredient = ingredient;
  }

  @Override
  public boolean matches(ICauldronInventory inv, World worldIn) {
    ItemStack stack = inv.getStack();
    return inv.getLevel() > 0 && ingredient.test(stack) && matches(inv.getContents(), stack);
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
    // if the stack contains multiple items, update just one
    ICauldronContents contents = inventory.getContents();
    ItemStack stack = inventory.getStack();
    if (stack.getCount() > 1) {
      stack = stack.split(1);
      inventory.giveStack(updateColor(contents, stack));
    } else {
      // if one, update the one
      inventory.setStack(updateColor(contents, stack));
    }

    // remove a level of dye
    inventory.addLevel(-1);

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

  /**
   * @deprecated  Use {@link #getCraftingResult(IInventory)}
   */
  @Deprecated
  @Override
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }

  @Override
  public ItemStack getCraftingResult(ICauldronInventory inv) {
    return updateColor(inv.getContents(), inv.getStack());
  }

  @Override
  public ResourceLocation getId() {
    return id;
  }

  /**
   * Recipe to dye a dyeable
   */
  public static class Dye extends DyeableCauldronRecipe {
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
    public IRecipeSerializer<?> getSerializer() {
      return InspirationsRecipes.dyeDyeableSerializer;
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
    public IRecipeSerializer<?> getSerializer() {
      return InspirationsRecipes.clearDyeableSerializer;
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
  public static class FinishedRecipe implements IFinishedRecipe {
    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final Serializer serializer;
    private FinishedRecipe(ResourceLocation id, Ingredient ingredient, Serializer serializer) {
      this.id = id;
      this.ingredient = ingredient;
      this.serializer = serializer;
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
      return new FinishedRecipe(id, ingredient, InspirationsRecipes.dyeDyeableSerializer);
    }

    /**
     * Creates a dye clearing recipe
     * @param id          Recipe ID
     * @param ingredient  Recipe ingredient
     * @return  Recipe
     */
    public static FinishedRecipe clear(ResourceLocation id, Ingredient ingredient) {
      return new FinishedRecipe(id, ingredient, InspirationsRecipes.clearDyeableSerializer);
    }

    @Override
    public void serialize(JsonObject json) {
      json.add("ingredient", ingredient.serialize());
    }

    @Override
    public ResourceLocation getID() {
      return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return serializer;
    }

    @Nullable
    @Override
    public JsonObject getAdvancementJson() {
      return null;
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementID() {
      return null;
    }
  }
}
