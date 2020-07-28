package knightminer.inspirations.building.inventory;

import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.tileentity.BookshelfTileEntity;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import slimeknights.mantle.inventory.MultiModuleContainer;

public class BookshelfContainer extends MultiModuleContainer<BookshelfTileEntity> {
	public BookshelfContainer(int winId, PlayerInventory inventoryPlayer, BookshelfTileEntity shelf) {
		super(InspirationsBuilding.contBookshelf, winId, inventoryPlayer, shelf);
		for(int i = 0; i < 7; i++) {
			this.addSlot(new SlotBookshelf(tile, i, 26 + (i*18), 18));
		}
		for(int i = 0; i < 7; i++) {
			this.addSlot(new SlotBookshelf(tile, i+7, 26 + (i*18), 44));
		}
		addInventorySlots();
	}

	public BookshelfContainer(int id, PlayerInventory inv, PacketBuffer buf) {
		this(id, inv, getTileEntityFromBuf(buf, BookshelfTileEntity.class));
	}

	@Override
	protected int getInventoryXOffset() {
		return 8;
	}

	@Override
	protected int getInventoryYOffset() {
		return 74;
	}

	public static class SlotBookshelf extends Slot {

		public SlotBookshelf(BookshelfTileEntity bookshelf, int index, int x, int y) {
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
