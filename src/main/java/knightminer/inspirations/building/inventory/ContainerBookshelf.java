package knightminer.inspirations.building.inventory;

import knightminer.inspirations.building.tileentity.TileBookshelf;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import slimeknights.mantle.inventory.ContainerMultiModule;

public class ContainerBookshelf extends ContainerMultiModule<TileBookshelf> {

	public ContainerBookshelf(InventoryPlayer inventoryPlayer, TileBookshelf tile) {
		super(tile);
		for(int i = 0; i < 7; i++) {
			this.addSlotToContainer(new SlotBookshelf(tile, i, 26 + (i*18), 18));
		}
		for(int i = 0; i < 7; i++) {
			this.addSlotToContainer(new SlotBookshelf(tile, i+7, 26 + (i*18), 44));
		}

		addPlayerInventory(inventoryPlayer, 8, 74);
	}

	public static class SlotBookshelf extends Slot {

		public SlotBookshelf(TileBookshelf bookshelf, int index, int x, int y) {
			super(bookshelf, index, x, y);
		}

		/**
		 * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
		 */
		@Override
		public boolean isItemValid(ItemStack stack) {
			return InspirationsRegistry.isBook(stack);
		}
	}
}
