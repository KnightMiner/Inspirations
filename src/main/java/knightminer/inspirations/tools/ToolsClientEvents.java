package knightminer.inspirations.tools;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.ClientEvents;
import knightminer.inspirations.tools.client.BarometerPropertyGetter;
import knightminer.inspirations.tools.client.NorthCompassPropertyGetter;
import knightminer.inspirations.tools.client.PhotometerPropertyGetter;
import knightminer.inspirations.tools.client.RedstoneArrowRenderer;
import knightminer.inspirations.tools.client.WaypointCompassPropertyGetter;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = Inspirations.modID, value = Dist.CLIENT, bus = Bus.MOD)
public class ToolsClientEvents extends ClientEvents {

  @SubscribeEvent
  static void clientSetup(FMLClientSetupEvent event) {
    RenderingRegistry.registerEntityRenderingHandler(InspirationsTools.entRSArrow, RedstoneArrowRenderer::new);

    // item model properties
    registerModelProperty(InspirationsTools.northCompass, "angle", new NorthCompassPropertyGetter());
    registerModelProperty(InspirationsTools.barometer, "height", new BarometerPropertyGetter());
    registerModelProperty(InspirationsTools.photometer, "light", new PhotometerPropertyGetter());
    IItemPropertyGetter waypointCompass = new WaypointCompassPropertyGetter();
    InspirationsTools.waypointCompasses.forEach(compass -> registerModelProperty(compass, "angle", waypointCompass));
  }

  @SubscribeEvent
  static void registerItemColors(ColorHandlerEvent.Item event) {
    ItemColors itemColors = event.getItemColors();
    InspirationsTools.waypointCompasses.forEach(compass -> itemColors.register(compass::getColor, compass));
  }
}
