package knightminer.inspirations.building.inventory;

import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.tileentity.ShelfInventory;
import knightminer.inspirations.building.tileentity.ShelfTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.mantle.inventory.ItemHandlerSlot;

import javax.annotation.Nullable;

public class ShelfContainer extends BaseContainer<ShelfTileEntity> {
  /**
   * Standard constructor
   * @param id    Window ID
   * @param inv   Player inventory instance
   * @param shelf Bookshelf tile entity
   */
  public ShelfContainer(int id, PlayerInventory inv, @Nullable ShelfTileEntity shelf) {
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
  public ShelfContainer(int id, PlayerInventory inv, PacketBuffer buf) {
    this(id, inv, getTileEntityFromBuf(buf, ShelfTileEntity.class));
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
  private static class ShelfSlot extends ItemHandlerSlot {
    private final ShelfInventory shelf;
    private ShelfSlot(ShelfInventory inventory, int index, int x, int y) {
      super(inventory, index, x, y);
      this.shelf = inventory;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
      return shelf.canHoldItem(getSlotIndex(), stack);
    }
  }
}
