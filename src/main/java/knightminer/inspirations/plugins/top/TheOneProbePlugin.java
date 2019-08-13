package knightminer.inspirations.plugins.top;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = "TheOneProbePlugin", modsRequired = TheOneProbePlugin.modid, defaultEnable = true)
public class TheOneProbePlugin {
	public static final String modid = "theoneprobe";

	@SubscribeEvent
	public void preInit(InterModEnqueueEvent event) {
		InterModComms.sendTo(modid, "getTheOneProbe", TheOneProbeRegistrar::new);

	}
}
