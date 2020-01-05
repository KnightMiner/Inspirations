package knightminer.inspirations.building.inventory;

import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.tileentity.BookshelfTileEntity;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.IContainerFactory;
import slimeknights.mantle.inventory.MultiModuleContainer;

public class BookshelfContainer extends MultiModuleContainer<BookshelfTileEntity> {

	public static class Factory implements IContainerFactory<BookshelfContainer> {
		@Override
		public BookshelfContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
			// Create the container on the clientside.
			BlockPos pos = data.readBlockPos();
			boolean secHalf = data.readBoolean();
			TileEntity te = inv.player.world.getTileEntity(pos);
			if (te instanceof BookshelfTileEntity) {
				return new BookshelfContainer(windowId, inv, secHalf, (BookshelfTileEntity) te);
			}
			throw new AssertionError(String.format("No bookshelf at %s!", pos));
		}
	}

	/**
	 * @param winId Internal window ID for vanilla's logic.
	 * @param inventoryPlayer The player's inventory.
	 * @param secHalf If true, access the second half for a dual-sided shelf.
	 * @param shelf The shelf itself.
	 */
	public BookshelfContainer(int winId, PlayerInventory inventoryPlayer, boolean secHalf, BookshelfTileEntity shelf) {
		super(InspirationsBuilding.contBookshelf, winId, shelf);
		int offset = secHalf ? 14 : 0;
		for(int i = 0; i < 7; i++) {
			this.addSlot(new SlotBookshelf(tile, i + offset, 26 + (i*18), 18));
		}
		for(int i = 0; i < 7; i++) {
			this.addSlot(new SlotBookshelf(tile, i + offset + 7, 26 + (i*18), 44));
		}

		addPlayerInventory(inventoryPlayer, 8, 74);
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
