package knightminer.inspirations.tweaks;

import com.google.common.eventbus.Subscribe;

import knightminer.inspirations.common.PulseBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import slimeknights.mantle.item.ItemMetaDynamic;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = InspirationsTweaks.pulseID, description = "Various vanilla tweaks")
public class InspirationsTweaks extends PulseBase {
	public static final String pulseID = "InspirationsTweaks";

	// items
	public static ItemMetaDynamic materials;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(TweaksEvents.class);
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
	}

	@Subscribe
	public void postInit(FMLPostInitializationEvent event) {
	}
}
