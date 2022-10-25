package knightminer.inspirations.library.recipe;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronTransform;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * Contains all recipe types used in Inspirations
 */
public class RecipeTypes {
  /** Recipe type for cauldron recipes */
  public static final RecipeType<ICauldronRecipe> CAULDRON = register("cauldron");
  public static final RecipeType<ICauldronTransform> CAULDRON_TRANSFORM = register("cauldron_transform");

  /**
   * Registers an Inspirations recipe type
   * @param name  Type name
   * @param <R>   Recipe base class
   * @return  Registered recipe type
   */
  private static <R extends Recipe<?>> RecipeType<R> register(String name) {
    return RecipeType.register(Inspirations.resourceName(name));
  }
}
