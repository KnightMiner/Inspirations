package knightminer.inspirations.utility.inventory;

import knightminer.inspirations.utility.tileentity.TileCollector;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import slimeknights.mantle.inventory.ContainerMultiModule;

public class ContainerCollector extends ContainerMultiModule<TileCollector> {

	public ContainerCollector(InventoryPlayer inventoryPlayer, TileCollector tile) {
		super(tile);
		for(int y = 0; y < 3; y++) {
			for(int x = 0; x < 3; x++) {
				this.addSlotToContainer(new Slot(tile, (x+y*3), 62+(x*18), 17+(y*18)));
			}
		}

		addPlayerInventory(inventoryPlayer, 8, 84);
	}
}
