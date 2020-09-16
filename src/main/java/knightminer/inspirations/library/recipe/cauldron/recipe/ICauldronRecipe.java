package knightminer.inspirations.library.recipe.cauldron.recipe;

import knightminer.inspirations.library.recipe.RecipeTypes;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.inventory.IModifyableCauldronInventory;
import net.minecraft.item.crafting.IRecipeType;
import slimeknights.mantle.recipe.ICustomOutputRecipe;

/**
 * Base interface for all cauldron recipes based on direct interaction
 */
public interface ICauldronRecipe extends ICustomOutputRecipe<ICauldronInventory> {
  /** Maximum fill level of the cauldron */
  int MAX = 12;
  /** Levels for 1/2 of a cauldron */
  int HALF = MAX / 2;
  /** Levels for 1/3 of a cauldron */
  int THIRD = MAX / 3;
  /** Levels for 1/4 of a cauldron */
  int QUARTER = MAX / 4;
  /** Levels for 1/6 of a cauldron */
  int SIXTH = MAX / 6;

  /**
   * Updates the cauldron based on the results of the recipe
   * @param inventory  Inventory for modification
   */
  void handleRecipe(IModifyableCauldronInventory inventory);

  @Override
  default IRecipeType<?> getType() {
    return RecipeTypes.CAULDRON;
  }
}
