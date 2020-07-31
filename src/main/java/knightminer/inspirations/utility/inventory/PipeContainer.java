package knightminer.inspirations.utility.inventory;

import knightminer.inspirations.utility.InspirationsUtility;
import knightminer.inspirations.utility.tileentity.PipeTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import slimeknights.mantle.inventory.BaseContainer;

import javax.annotation.Nullable;

public class PipeContainer extends BaseContainer<PipeTileEntity> {
  public PipeContainer(int winId, PlayerInventory inventoryPlayer, @Nullable PipeTileEntity tile) {
    super(InspirationsUtility.contPipe, winId, inventoryPlayer, tile);
    if (tile != null) {
      this.addSlot(new Slot(tile, 0, 80, 20));
    }
    addInventorySlots();
  }

  public PipeContainer(int windowId, PlayerInventory inv, PacketBuffer data) {
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
