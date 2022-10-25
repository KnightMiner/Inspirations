package knightminer.inspirations.recipes.recipe.inventory;

import knightminer.inspirations.library.recipe.cauldron.inventory.IModifyableCauldronInventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.function.Consumer;

/**
 * Base logic for handling items in the cauldron, as it almost never actually contains the items
 */
public abstract class CauldronItemInventory implements IModifyableCauldronInventory {
  protected static final Consumer<ItemStack> EMPTY_CONSUMER = s -> {};
  protected ItemStack stack = ItemStack.EMPTY;
  protected Consumer<ItemStack> itemSetter = EMPTY_CONSUMER;
  protected Consumer<ItemStack> itemAdder = EMPTY_CONSUMER;

  public CauldronItemInventory(ItemStack stack, Consumer<ItemStack> itemSetter, Consumer<ItemStack> itemAdder) {
    this.stack = stack;
    this.itemSetter = itemSetter;
    this.itemAdder = itemAdder;
  }

  public CauldronItemInventory() {}

  @Override
  public ItemStack getStack() {
    return stack;
  }

  @Override
  public void setStack(ItemStack stack) {
    this.stack = stack;
    itemSetter.accept(stack);
  }

  @Override
  public void giveStack(ItemStack stack) {
    itemAdder.accept(stack);
  }

  /**
   * Gets the consumer for adding items to a player. Mostly here because the logic is a bit more complex than the setter
   * @param player  Player to give items to
   * @return  Consumer for item stacks
   */
  public static Consumer<ItemStack> getPlayerAdder(Player player) {
    return stack -> ItemHandlerHelper.giveItemToPlayer(player, stack, player.getInventory().selected);
  }
}
