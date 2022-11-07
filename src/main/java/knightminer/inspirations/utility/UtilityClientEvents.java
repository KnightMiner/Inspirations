package knightminer.inspirations.utility;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.AbstractClientEvents;
import knightminer.inspirations.common.client.BackgroundContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = Inspirations.modID, value = Dist.CLIENT, bus = Bus.MOD)
public class UtilityClientEvents extends AbstractClientEvents {

  @SubscribeEvent
  static void clientSetup(FMLClientSetupEvent event) {
    RenderType cutout = RenderType.cutout();
    setRenderLayer(InspirationsUtility.torchLeverFloor, cutout);
    setRenderLayer(InspirationsUtility.torchLeverWall, cutout);
    setRenderLayer(InspirationsUtility.soulLeverFloor, cutout);
    setRenderLayer(InspirationsUtility.soulLeverWall, cutout);
  }

  @SubscribeEvent
  static void commonSetup(FMLCommonSetupEvent event) {
    // Register GUIs.
    registerScreenFactory(InspirationsUtility.contCollector, new BackgroundContainerScreen.Factory<>(166, new ResourceLocation("textures/gui/container/dispenser.png")));
    registerScreenFactory(InspirationsUtility.contPipe, new BackgroundContainerScreen.Factory<>(133, "pipe"));
  }
}
