package knightminer.inspirations.plugins.waila;

import com.google.common.eventbus.Subscribe;

import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = "WailaPlugin", modsRequired = WailaPlugin.modid, defaultEnable = true)
public class WailaPlugin {
	public static final String modid = "waila";

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		FMLInterModComms.sendMessage(modid, "register", "knightminer.inspirations.plugins.waila.WailaRegistrar.registerWaila");
	}
}
