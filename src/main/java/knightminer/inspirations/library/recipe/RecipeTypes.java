package knightminer.inspirations.library.recipe;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.cauldron.recipe.CauldronTransform;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;

/**
 * Contains all recipe types used in Inspirations
 */
public class RecipeTypes {
  /** Recipe type for cauldron recipes */
  public static final IRecipeType<ICauldronRecipe> CAULDRON = register("cauldron");
  public static final IRecipeType<CauldronTransform> CAULDRON_TRANSFORM = register("cauldron_transform");

  /**
   * Registers an Inspirations recipe type
   * @param name  Type name
   * @param <R>   Recipe base class
   * @return  Registered recipe type
   */
  private static <R extends IRecipe<?>> IRecipeType<R> register(String name) {
    return IRecipeType.register(Inspirations.resourceName(name));
  }
}
