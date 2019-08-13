package knightminer.inspirations.plugins.waila;

import com.google.common.eventbus.Subscribe;

import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = "WailaPlugin", modsRequired = WailaPlugin.modid, defaultEnable = true)
public class WailaPlugin {
	public static final String modid = "waila";

	@Subscribe
	public void preInit(InterModEnqueueEvent event) {
		InterModComms.sendTo(modid, "register", WailaRegistrar::registerWaila);
	}
}
