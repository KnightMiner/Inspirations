package knightminer.inspirations.common.datagen;

import knightminer.inspirations.Inspirations;
import slimeknights.mantle.recipe.data.ICommonRecipeHelper;
import slimeknights.mantle.recipe.data.IRecipeHelper;

/**
 * Utilities to help in the creation of recipes
 */
public interface IInspirationsRecipeBuilder extends IRecipeHelper, ICommonRecipeHelper {
  @Override
  default String getModId() {
    return Inspirations.modID;
  }
}
