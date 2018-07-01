package knightminer.inspirations.utility.inventory;

import knightminer.inspirations.utility.tileentity.TilePipe;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import slimeknights.mantle.inventory.ContainerMultiModule;

public class ContainerPipe extends ContainerMultiModule<TilePipe> {

	public ContainerPipe(InventoryPlayer inventoryPlayer, TilePipe tile) {
		super(tile);
		this.addSlotToContainer(new Slot(tile, 0, 80, 20));

		addPlayerInventory(inventoryPlayer, 8, 51);
	}
}
