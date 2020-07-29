package knightminer.inspirations.building.inventory;

import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.tileentity.BookshelfTileEntity;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import slimeknights.mantle.inventory.BaseContainer;

import javax.annotation.Nullable;

public class BookshelfContainer extends BaseContainer<BookshelfTileEntity> {
	/**
	 * Standard constructor
	 * @param id     Window ID
	 * @param inv    Player inventory instance
	 * @param shelf  Bookshelf tile entity
	 */
	public BookshelfContainer(int id, PlayerInventory inv, @Nullable BookshelfTileEntity shelf) {
		super(InspirationsBuilding.contBookshelf, id, inv, shelf);
		if (tile != null) {
			// two rows of slots
			for(int i = 0; i < 7; i++) {
				this.addSlot(new SlotBookshelf(tile, i, 26 + (i * 18), 18));
			}
			for(int i = 0; i < 7; i++) {
				this.addSlot(new SlotBookshelf(tile, i+7, 26 + (i*18), 44));
			}
		}
		addInventorySlots();
	}

	/**
	 * Factory constructor to get tile entity from the packet buffer
	 * @param id   Window ID
	 * @param inv  Player inventory
	 * @param buf  Packet buffer instance
	 */
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

	/** Slot that limits to just books */
	private static class SlotBookshelf extends Slot {
		private SlotBookshelf(BookshelfTileEntity bookshelf, int index, int x, int y) {
			super(bookshelf, index, x, y);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return InspirationsRegistry.isBook(stack);
		}
	}
}
