package knightminer.inspirations.utility;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.ClientEvents;
import knightminer.inspirations.shared.client.BackgroundContainerScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = Inspirations.modID, value = Dist.CLIENT, bus = Bus.MOD)
public class UtilityClientEvents extends ClientEvents {

	@SubscribeEvent
	static void clientSetup(FMLClientSetupEvent event) {
		RenderType cutout = RenderType.getCutout();
		RenderTypeLookup.setRenderLayer(InspirationsUtility.torchLeverFloor, cutout);
		RenderTypeLookup.setRenderLayer(InspirationsUtility.torchLeverWall, cutout);
	}

	@SubscribeEvent
	static void commonSetup(FMLCommonSetupEvent event) {
		// Register GUIs.
		ScreenManager.registerFactory(InspirationsUtility.contCollector, new BackgroundContainerScreen.Factory<>(new ResourceLocation("textures/gui/container/dispenser.png")));
		ScreenManager.registerFactory(InspirationsUtility.contPipe, new BackgroundContainerScreen.Factory<>("pipe"));
	}
}
