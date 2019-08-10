package knightminer.inspirations.utility.inventory;

import knightminer.inspirations.utility.InspirationsUtility;
import knightminer.inspirations.utility.tileentity.TileCollector;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.IContainerFactory;
import slimeknights.mantle.inventory.MultiModuleContainer;

public class ContainerCollector extends MultiModuleContainer<TileCollector> {
	public static class Factory implements IContainerFactory<ContainerCollector> {
		@Override
		public ContainerCollector create(int windowId, PlayerInventory inv, PacketBuffer data) {
			// Create the container on the clientside.
			BlockPos pos = data.readBlockPos();
			TileEntity te = inv.player.world.getTileEntity(pos);
			if (te instanceof TileCollector) {
				return new ContainerCollector(windowId, inv, (TileCollector) te);
			}
			throw new AssertionError(String.format("No collector at %s!", pos));
		}
	}

	public ContainerCollector(int winId, PlayerInventory inventoryPlayer, TileCollector tile) {
		super(InspirationsUtility.contCollector, winId, tile);
		for(int y = 0; y < 3; y++) {
			for(int x = 0; x < 3; x++) {
				this.addSlot(new Slot(tile, (x+y*3), 62+(x*18), 17+(y*18)));
			}
		}

		addPlayerInventory(inventoryPlayer, 8, 84);
	}
}
