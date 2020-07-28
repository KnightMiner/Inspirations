package knightminer.inspirations.utility.inventory;

import knightminer.inspirations.utility.InspirationsUtility;
import knightminer.inspirations.utility.tileentity.CollectorTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import slimeknights.mantle.inventory.MultiModuleContainer;

public class CollectorContainer extends MultiModuleContainer<CollectorTileEntity> {
	public CollectorContainer(int winId, PlayerInventory inventoryPlayer, CollectorTileEntity tile) {
		super(InspirationsUtility.contCollector, winId, inventoryPlayer, tile);
		for(int y = 0; y < 3; y++) {
			for(int x = 0; x < 3; x++) {
				this.addSlot(new Slot(tile, (x+y*3), 62+(x*18), 17+(y*18)));
			}
		}
		addInventorySlots();
	}

	public CollectorContainer(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, getTileEntityFromBuf(data, CollectorTileEntity.class));
	}
}
