package knightminer.inspirations.tools;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.AbstractClientEvents;
import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.tools.client.BarometerPropertyGetter;
import knightminer.inspirations.tools.client.DimensionCompassPropertyGetter;
import knightminer.inspirations.tools.client.NorthCompassPropertyGetter;
import knightminer.inspirations.tools.client.PhotometerPropertyGetter;
import knightminer.inspirations.tools.client.RedstoneArrowRenderer;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = Inspirations.modID, value = Dist.CLIENT, bus = Bus.MOD)
public class ToolsClientEvents extends AbstractClientEvents {

  @SubscribeEvent
  static void clientSetup(FMLClientSetupEvent event) {
    // item model properties
    registerModelProperty(InspirationsTools.northCompass, "angle", new NorthCompassPropertyGetter());
    registerModelProperty(InspirationsTools.barometer, "height", new BarometerPropertyGetter());
    registerModelProperty(InspirationsTools.photometer, "light", new PhotometerPropertyGetter());
    registerModelProperty(InspirationsTools.dimensionCompass, "angle", new DimensionCompassPropertyGetter());
    // re-register shield blocking with registry sub shield, not strictly needed unless certain mods decide to register their properties before regsitry events
    if (InspirationsTools.shield != null) {
      ItemProperties.register(InspirationsTools.shield, new ResourceLocation("blocking"),
                           (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
    }
  }

  @SubscribeEvent
  static void registerEntityRenderer(EntityRenderersEvent.RegisterRenderers event) {
    event.registerEntityRenderer(InspirationsTools.entRSArrow, RedstoneArrowRenderer::new);
  }

  @SubscribeEvent
  static void registerItemColors(ColorHandlerEvent.Item event) {
    ItemColors itemColors = event.getItemColors();

    // coloring of books for normal bookshelf
    registerItemColors(itemColors, (stack, tintIndex) -> {
      if (tintIndex == 1 && MiscUtil.hasColor(stack)) {
        return MiscUtil.getColor(stack);
      }
      return tintIndex == 0 ? -1 : 0x2CCDB1;
    }, InspirationsTools.dimensionCompass);
  }
}
