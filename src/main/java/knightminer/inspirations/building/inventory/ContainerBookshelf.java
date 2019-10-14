package knightminer.inspirations.building.inventory;

import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.tileentity.TileBookshelf;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.IContainerFactory;
import slimeknights.mantle.inventory.MultiModuleContainer;

public class ContainerBookshelf extends MultiModuleContainer<TileBookshelf> {
	public static class Factory implements IContainerFactory<ContainerBookshelf> {
		@Override
		public ContainerBookshelf create(int windowId, PlayerInventory inv, PacketBuffer data) {
			// Create the container on the clientside.
			BlockPos pos = data.readBlockPos();
			TileEntity te = inv.player.world.getTileEntity(pos);
			if (te instanceof TileBookshelf) {
				return new ContainerBookshelf(windowId, inv, (TileBookshelf) te);
			}
			throw new AssertionError(String.format("No bookshelf at %s!", pos));
		}
	}

	public ContainerBookshelf(int winId, PlayerInventory inventoryPlayer, TileBookshelf shelf) {
		super(InspirationsBuilding.contBookshelf, winId, shelf);
		for(int i = 0; i < 7; i++) {
			this.addSlot(new SlotBookshelf(tile, i, 26 + (i*18), 18));
		}
		for(int i = 0; i < 7; i++) {
			this.addSlot(new SlotBookshelf(tile, i+7, 26 + (i*18), 44));
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
