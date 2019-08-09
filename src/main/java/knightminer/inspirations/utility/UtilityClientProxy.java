package knightminer.inspirations.utility;

import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.utility.block.BlockRedstoneBarrel;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class UtilityClientProxy extends ClientProxy {


	@SubscribeEvent
	public void registerBlockColors(ColorHandlerEvent.Block event) {
		BlockColors blockColors = event.getBlockColors();

		// coloring redstone inside the barrel
		blockColors.register((state, world, pos, tintIndex) -> {
			if(tintIndex == 1) {
				int level = state.get(BlockRedstoneBarrel.LEVEL);
				if(level > 0) {
					return RedstoneWireBlock.colorMultiplier(level);
				}
			}

			return -1;
		}, InspirationsUtility.redstoneBarrel);
	}

	@SubscribeEvent
	public void registerItemColors(ColorHandlerEvent.Item event) {
		ItemColors itemColors = event.getItemColors();

		// Hardcode a specific level for the inventory item which looks nice.
		itemColors.register((stack, tintIndex) -> {
			if (tintIndex == 1) {
				return RedstoneWireBlock.colorMultiplier(7);
			}
			return -1;
		}, InspirationsUtility.redstoneBarrel);
	}
}
