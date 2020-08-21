package knightminer.inspirations.library.recipe.cauldron.inventory;

import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import net.minecraft.item.ItemStack;

import java.util.function.IntUnaryOperator;

/**
 * Inventory for handling the results of a cauldron recipe
 */
public interface IModifyableCauldronInventory extends ICauldronInventory {
  /**
   * Updates the held item stack used to interact with the cauldron
   * @param stack  New stack to hold
   */
  void setStack(ItemStack stack);

  /**
   * Gives an item to the player. Does not effect the existing stack
   * @param stack  New player stack
   */
  void giveStack(ItemStack stack);

  /**
   * Sets the contents of the cauldron
   * @param contents  New cauldron contents
   */
  void setContents(ICauldronContents contents);

  /**
   * Sets the cauldron level to a new value
   * @param level  New cauldron level
   */
  void setLevel(int level);


  /* Helper methods */

  /**
   * Decreases the size of the held stack
   * @param amount  Amount to shrink by
   * @return updated stack
   */
  default ItemStack shrinkStack(int amount) {
    ItemStack stack = getStack();
    stack.shrink(amount);
    if (stack.isEmpty()) {
      stack = ItemStack.EMPTY;
    }
    setStack(stack);
    return stack;
  }

  /**
   * Updates the cauldron level based on the given function
   * @param updater  Int to int function
   * @return  True if the cauldron is now empty, false otherwise
   */
  default boolean updateLevel(IntUnaryOperator updater) {
    int newLevel = updater.applyAsInt(getLevel());
    setLevel(newLevel);
    return newLevel == 0;
  }
}
