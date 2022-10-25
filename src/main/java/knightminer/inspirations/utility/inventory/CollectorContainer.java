package knightminer.inspirations.utility.inventory;

import knightminer.inspirations.utility.InspirationsUtility;
import knightminer.inspirations.utility.tileentity.CollectorTileEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import slimeknights.mantle.inventory.BaseContainerMenu;

import javax.annotation.Nullable;

public class CollectorContainer extends BaseContainerMenu<CollectorTileEntity> {
  public CollectorContainer(int winId, Inventory inventoryPlayer, @Nullable CollectorTileEntity tile) {
    super(InspirationsUtility.contCollector, winId, inventoryPlayer, tile);
    if (tile != null) {
      for (int y = 0; y < 3; y++) {
        for (int x = 0; x < 3; x++) {
          this.addSlot(new Slot(tile, (x + y * 3), 62 + (x * 18), 17 + (y * 18)));
        }
      }
    }
    addInventorySlots();
  }

  public CollectorContainer(int windowId, Inventory inv, FriendlyByteBuf data) {
    this(windowId, inv, getTileEntityFromBuf(data, CollectorTileEntity.class));
  }
}
