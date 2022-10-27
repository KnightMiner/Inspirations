package knightminer.inspirations.library.recipe.cauldron.recipe;

import knightminer.inspirations.library.recipe.RecipeTypes;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.inventory.IModifyableCauldronInventory;
import net.minecraft.world.item.crafting.RecipeType;
import slimeknights.mantle.recipe.ICustomOutputRecipe;

import javax.annotation.Nonnull;

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
  /** Levels for 1/12 of a cauldron. Technically, this is 1, but using this constant over 1 will allow changing the cauldron to hold more in the future */
  int TWELFTH = MAX / 12;

  /**
   * Updates the cauldron based on the results of the recipe
   * @param inventory  Inventory for modification
   */
  void handleRecipe(IModifyableCauldronInventory inventory);

  @Nonnull
  @Override
  default RecipeType<?> getType() {
    return RecipeTypes.CAULDRON.get();
  }
}
