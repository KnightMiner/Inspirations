package knightminer.inspirations.utility.inventory;

import knightminer.inspirations.utility.InspirationsUtility;
import knightminer.inspirations.utility.tileentity.PipeTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.IContainerFactory;
import slimeknights.mantle.inventory.MultiModuleContainer;

public class PipeContainer extends MultiModuleContainer<PipeTileEntity> {
	public PipeContainer(int winId, PlayerInventory inventoryPlayer, PipeTileEntity tile) {
		super(InspirationsUtility.contPipe, winId, inventoryPlayer, tile);
		this.addSlot(new Slot(tile, 0, 80, 20));
		addInventorySlots();
	}

	@Override
	protected int getInventoryXOffset() {
		return 8;
	}

	@Override
	protected int getInventoryYOffset() {
		return 51;
	}

	public static class Factory implements IContainerFactory<PipeContainer> {
		@Override
		public PipeContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
			// Create the container on the clientside.
			BlockPos pos = data.readBlockPos();
			TileEntity te = inv.player.world.getTileEntity(pos);
			if (te instanceof PipeTileEntity) {
				return new PipeContainer(windowId, inv, (PipeTileEntity) te);
			}
			throw new AssertionError(String.format("No pipe at %s!", pos));
		}
	}
}
