package knightminer.inspirations.plugins.top;

import com.google.common.eventbus.Subscribe;

import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = "TheOneProbePlugin", modsRequired = TheOneProbePlugin.modid, defaultEnable = true)
public class TheOneProbePlugin {
	public static final String modid = "theoneprobe";

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		FMLInterModComms.sendFunctionMessage(modid, "getTheOneProbe", "knightminer.inspirations.plugins.top.TheOneProbeRegistrar");
	}
}
