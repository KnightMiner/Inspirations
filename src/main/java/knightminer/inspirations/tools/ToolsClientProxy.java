package knightminer.inspirations.tools;

import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.tools.client.BarometerPropertyGetter;
import knightminer.inspirations.tools.client.NorthCompassPropertyGetter;
import knightminer.inspirations.tools.client.PhotometerPropertyGetter;
import knightminer.inspirations.tools.client.RedstoneArrowRenderer;
import knightminer.inspirations.tools.client.WaypointCompassPropertyGetter;
import knightminer.inspirations.tools.item.WaypointCompassItem;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ToolsClientProxy extends ClientProxy {

	@SubscribeEvent
	void clientSetup(FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(InspirationsTools.entRSArrow, RedstoneArrowRenderer::new);

		// item model properties
		registerModelProperty(InspirationsTools.northCompass, "angle", new NorthCompassPropertyGetter());
		registerModelProperty(InspirationsTools.barometer, "height", new BarometerPropertyGetter());
		registerModelProperty(InspirationsTools.photometer, "light", new PhotometerPropertyGetter());
		for (WaypointCompassItem item : InspirationsTools.waypointCompasses) {
			registerModelProperty(item, "angle", new WaypointCompassPropertyGetter());
		}
	}

	@SubscribeEvent
	void registerItemColors(ColorHandlerEvent.Item event) {
		ItemColors itemColors = event.getItemColors();

		// Dyed waypoint compasses. This implements IItemColor itself.
		for(WaypointCompassItem compass : InspirationsTools.waypointCompasses) {
			registerItemColors(itemColors, compass::getColor, compass);
		}
	}
}
