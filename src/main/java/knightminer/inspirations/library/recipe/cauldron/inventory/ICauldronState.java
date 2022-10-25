package knightminer.inspirations.library.recipe.cauldron.inventory;

import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.util.CauldronTemperature;
import slimeknights.mantle.recipe.container.IEmptyContainer;

/**
 * Interface representing the current cauldron state with no outside item context.
 * Use {@link ICauldronInventory} for item context.
 */
public interface ICauldronState extends IEmptyContainer {
  /**
   * Gets the contents of the cauldron.
   * If {@link #getLevel()} returns 0, value is indeterminate.
   * @return  Cauldron contents
   */
  ICauldronContents getContents();

  /**
   * Gets the level of the cauldron, between 0 and 3
   * @return  Cauldron level between 0 and 3
   */
  int getLevel();

  /**
   * Gets the temperature of this cauldron
   * @return  Cauldron temperature
   */
  CauldronTemperature getTemperature();
}
