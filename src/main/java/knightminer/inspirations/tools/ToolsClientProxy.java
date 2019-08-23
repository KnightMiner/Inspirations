package knightminer.inspirations.tools;

import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.tools.client.RenderModArrow;
import knightminer.inspirations.tools.entity.RedstoneArrow;
import knightminer.inspirations.tools.item.ItemWaypointCompass;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ToolsClientProxy extends ClientProxy {

	@SubscribeEvent
	public void clientSetup(FMLClientSetupEvent event) {
		super.preInit();
		RenderingRegistry.registerEntityRenderingHandler(RedstoneArrow.class, RenderModArrow::new);
	}

	@SubscribeEvent
	public void registerItemColors(ColorHandlerEvent.Item event) {
		ItemColors itemColors = event.getItemColors();

		// dyed waypoint compasses
		for(ItemWaypointCompass compass : InspirationsTools.waypointCompasses) {
			int needleColor = compass.getNeedleColor();
			int bodyColor = compass.getBodyColor();

			itemColors.register((stack, tintIndex) -> {
				switch(tintIndex) {
					case 0:
						return bodyColor;
					case 1:
						return needleColor;
					default:
						return -1;
				}
			}, compass);
		}
	}
}
