package knightminer.inspirations.library.recipe.cauldron.ingredient;

import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;

import java.util.List;
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

  /**
   * Gets a list of cauldron contents matching this ingredient
   * @return  Matching contents
   */
  List<ICauldronContents> getMatchingContents();
}
