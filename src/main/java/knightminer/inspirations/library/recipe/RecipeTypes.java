package knightminer.inspirations.library.recipe;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.anvil.AnvilRecipe;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronTransform;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;

/**
 * Contains all recipe types used in Inspirations
 */
public class RecipeTypes {
  /** Recipe type for cauldron recipes */
  public static final IRecipeType<ICauldronRecipe> CAULDRON = register("cauldron");
  public static final IRecipeType<ICauldronTransform> CAULDRON_TRANSFORM = register("cauldron_transform");
  public static final IRecipeType<AnvilRecipe> ANVIL = register("anvil");

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
