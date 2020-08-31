package knightminer.inspirations.library.recipe.cauldron.inventory;

import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import slimeknights.mantle.recipe.inventory.IEmptyInventory;

/**
 * Interface representing the current cauldron state with no outside item context.
 * Use {@link ICauldronInventory} for item context.
 */
public interface ICauldronState extends IEmptyInventory {
  /**
   * Gets the contents of the cauldron.
   * Should return {@link knightminer.inspirations.library.recipe.cauldron.contents.EmptyCauldronContents#INSTANCE} if {@link #getLevel()} returns 0. Should never return empty otherwise.
   * @return  Cauldron contents
   */
  ICauldronContents getContents();

  /**
   * Gets the level of the cauldron, between 0 and 3
   * @return  Cauldron level between 0 and 3
   */
  int getLevel();

  /**
   * If true, the cauldron was placed above blocks causing it to boil
   * @return  True if the cauldron is boiling
   */
  boolean isBoiling();
}
