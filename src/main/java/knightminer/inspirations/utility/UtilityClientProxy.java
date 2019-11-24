package knightminer.inspirations.utility;

import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.utility.client.CollectorScreen;
import knightminer.inspirations.utility.client.PipeScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class UtilityClientProxy extends ClientProxy {

	@SubscribeEvent
	public void commonSetup(FMLCommonSetupEvent event) {

		// Register GUIs.
		ScreenManager.registerFactory(InspirationsUtility.contCollector, CollectorScreen::new);
		ScreenManager.registerFactory(InspirationsUtility.contPipe, PipeScreen::new);
	}
}
