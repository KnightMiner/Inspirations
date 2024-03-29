package knightminer.inspirations.building.block.menu;

import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.block.entity.ShelfBlockEntity;
import knightminer.inspirations.building.block.entity.ShelfInventory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.inventory.BaseContainerMenu;
import slimeknights.mantle.inventory.SmartItemHandlerSlot;

import javax.annotation.Nullable;

public class ShelfContainerMenu extends BaseContainerMenu<ShelfBlockEntity> {
  /**
   * Standard constructor
   * @param id    Window ID
   * @param inv   Player inventory instance
   * @param shelf Bookshelf tile entity
   */
  public ShelfContainerMenu(int id, Inventory inv, @Nullable ShelfBlockEntity shelf) {
    super(InspirationsBuilding.shelfContainer, id, inv, shelf);
    if (tile != null) {
      // two rows of slots
      ShelfInventory inventory = tile.getInventory();
      for (int i = 0; i < 8; i++) {
        this.addSlot(new ShelfSlot(inventory, i,     17 + (i * 18), 18));
      }
      for (int i = 0; i < 8; i++) {
        this.addSlot(new ShelfSlot(inventory, i + 8, 17 + (i * 18), 44));
      }
    }
    addInventorySlots();
  }

  /**
   * Factory constructor to get tile entity from the packet buffer
   * @param id  Window ID
   * @param inv Player inventory
   * @param buf Packet buffer instance
   */
  public ShelfContainerMenu(int id, Inventory inv, FriendlyByteBuf buf) {
    this(id, inv, getTileEntityFromBuf(buf, ShelfBlockEntity.class));
  }

  @Override
  protected int getInventoryXOffset() {
    return 8;
  }

  @Override
  protected int getInventoryYOffset() {
    return 74;
  }

  /**
   * Slot that limits to just books
   */
  private static class ShelfSlot extends SmartItemHandlerSlot {
    private final ShelfInventory shelf;
    private ShelfSlot(ShelfInventory inventory, int index, int x, int y) {
      super(inventory, index, x, y);
      this.shelf = inventory;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
      return shelf.canHoldItem(getSlotIndex(), stack);
    }
  }
}
