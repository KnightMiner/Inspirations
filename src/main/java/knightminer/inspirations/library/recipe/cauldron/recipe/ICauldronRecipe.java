package knightminer.inspirations.library.recipe.cauldron.recipe;

import knightminer.inspirations.library.recipe.RecipeTypes;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.inventory.IModifyableCauldronInventory;
import net.minecraft.item.crafting.IRecipeType;
import slimeknights.mantle.recipe.ICommonRecipe;

/**
 * Base interface for all cauldron recipes
 */
public interface ICauldronRecipe extends ICommonRecipe<ICauldronInventory> {
  /** Maximum fill level of the cauldron */
  static int MAX = 3;

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
