package knightminer.inspirations.common.datagen;

import knightminer.inspirations.Inspirations;
import net.minecraftforge.common.crafting.conditions.ICondition;
import slimeknights.mantle.recipe.data.IRecipeBuilderUtils;

/**
 * Utilities to help in the creation of recipes
 */
public interface IInspirationsRecipeBuilder extends IRecipeBuilderUtils {
  @Override
  default String getModId() {
    return Inspirations.modID;
  }

  /**
   * Gets the base condition for the condition utility
   */
  @Override
  ICondition baseCondition();
}
