package knightminer.inspirations.shared;

import knightminer.inspirations.common.PulseBase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = InspirationsShared.pulseID, description = "Blocks and items used by all modules", forced = true)
public class InspirationsShared extends PulseBase {
	public static final String pulseID = "InspirationsShared";

	@SuppressWarnings("Convert2MethodRef")
	public static Object proxy = DistExecutor.callWhenOn(Dist.CLIENT, ()->()->new SharedClientProxy());

	@SubscribeEvent
	public void setup(FMLCommonSetupEvent event) {
		MinecraftForge.EVENT_BUS.addListener(SharedEvents::updateMilkCooldown);
	}
}
