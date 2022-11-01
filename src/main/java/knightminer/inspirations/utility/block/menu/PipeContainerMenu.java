package knightminer.inspirations.utility.block.menu;

import knightminer.inspirations.utility.InspirationsUtility;
import knightminer.inspirations.utility.block.entity.PipeBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import slimeknights.mantle.inventory.BaseContainerMenu;

import javax.annotation.Nullable;

public class PipeContainerMenu extends BaseContainerMenu<PipeBlockEntity> {
  public PipeContainerMenu(int winId, Inventory inventoryPlayer, @Nullable PipeBlockEntity tile) {
    super(InspirationsUtility.contPipe, winId, inventoryPlayer, tile);
    if (tile != null) {
      this.addSlot(new Slot(tile, 0, 80, 20));
    }
    addInventorySlots();
  }

  public PipeContainerMenu(int windowId, Inventory inv, FriendlyByteBuf data) {
    this(windowId, inv, getTileEntityFromBuf(data, PipeBlockEntity.class));
  }

  @Override
  protected int getInventoryXOffset() {
    return 8;
  }

  @Override
  protected int getInventoryYOffset() {
    return 51;
  }
}
