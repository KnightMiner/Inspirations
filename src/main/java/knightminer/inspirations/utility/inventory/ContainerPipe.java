package knightminer.inspirations.utility.inventory;

import knightminer.inspirations.utility.InspirationsUtility;
import knightminer.inspirations.utility.tileentity.TileCollector;
import knightminer.inspirations.utility.tileentity.TilePipe;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.IContainerFactory;
import slimeknights.mantle.inventory.MultiModuleContainer;

public class ContainerPipe extends MultiModuleContainer<TilePipe> {
	public static class Factory implements IContainerFactory<ContainerPipe> {
		@Override
		public ContainerPipe create(int windowId, PlayerInventory inv, PacketBuffer data) {
			// Create the container on the clientside.
			BlockPos pos = data.readBlockPos();
			TileEntity te = inv.player.world.getTileEntity(pos);
			if (te instanceof TilePipe) {
				return new ContainerPipe(windowId, inv, (TilePipe) te);
			}
			throw new AssertionError(String.format("No pipe at %s!", pos));
		}
	}


	public ContainerPipe(int winId, PlayerInventory inventoryPlayer, TilePipe tile) {
		super(InspirationsUtility.contPipe, winId, tile);
		this.addSlot(new Slot(tile, 0, 80, 20));

		addPlayerInventory(inventoryPlayer, 8, 51);
	}
}
