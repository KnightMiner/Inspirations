package knightminer.inspirations.library.recipe.cauldron.inventory;

import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

import java.util.function.IntUnaryOperator;

/**
 * Inventory for handling the results of a cauldron recipe
 * @deprecated Will be recreated once recipe format is decided upon
 */
@Deprecated
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

  /**
   * Plays the given sound at the cauldron location
   * @param sound  Sound to play
   */
  void playSound(SoundEvent sound);


  /* Helper methods */

  /**
   * Decreases the size of the held stack
   * @param amount  Amount to shrink by
   * @return true if the stack is now empty, false otherwise
   */
  default boolean shrinkStack(int amount) {
    ItemStack stack = getStack();
    stack.shrink(amount);
    if (stack.isEmpty()) {
      stack = ItemStack.EMPTY;
    }
    setStack(stack);
    return stack.isEmpty();
  }

  /**
   * Splits off a stack with the given amount
   * @param amount  Amount to split into
   * @return Split stack instance
   */
  default ItemStack splitStack(int amount) {
    ItemStack stack = getStack();
    if (stack.isEmpty()) {
      return ItemStack.EMPTY;
    }
    return stack.split(amount);
  }

  /**
   * Sets the given stack if the stack is empty, gives otherwise
   * @param stack  New stack to give
   */
  default void setOrGiveStack(ItemStack stack) {
    if (!stack.isEmpty()) {
      if (getStack().isEmpty()) {
        setStack(stack);
      } else {
        giveStack(stack);
      }
    }
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

  /**
   * Adds the given amount to the cauldron level
   * @param amount Amount to add, can be negative
   * @return  True if the cauldron is now empty, false otherwise
   */
  default boolean addLevel(int amount) {
    int newLevel = getLevel() + amount;
    setLevel(newLevel);
    return newLevel == 0;
  }
}
