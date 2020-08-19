package knightminer.inspirations.library.recipe.cauldron.ingredient;

import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;

import java.util.function.Predicate;

/**
 * Ingredient for matching cauldron contents
 */
public interface ICauldronIngredient extends Predicate<ICauldronContents> {
  /**
   * Gets the serializer for this ingredient
   * @return  Ingredient serializer
   */
  ICauldronIngredientSerializer<?> getSerializer();
}
