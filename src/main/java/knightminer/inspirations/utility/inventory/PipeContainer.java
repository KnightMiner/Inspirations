package knightminer.inspirations.utility.inventory;

import knightminer.inspirations.utility.InspirationsUtility;
import knightminer.inspirations.utility.tileentity.PipeTileEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import slimeknights.mantle.inventory.BaseContainerMenu;

import javax.annotation.Nullable;

public class PipeContainer extends BaseContainerMenu<PipeTileEntity> {
  public PipeContainer(int winId, Inventory inventoryPlayer, @Nullable PipeTileEntity tile) {
    super(InspirationsUtility.contPipe, winId, inventoryPlayer, tile);
    if (tile != null) {
      this.addSlot(new Slot(tile, 0, 80, 20));
    }
    addInventorySlots();
  }

  public PipeContainer(int windowId, Inventory inv, FriendlyByteBuf data) {
    this(windowId, inv, getTileEntityFromBuf(data, PipeTileEntity.class));
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
