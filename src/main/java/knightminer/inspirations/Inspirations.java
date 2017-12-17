package knightminer.inspirations;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.redstone.InspirationsRedstone;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import slimeknights.mantle.common.GuiHandler;
import slimeknights.mantle.pulsar.control.PulseManager;

@Mod(
		modid = Inspirations.modID,
		name = Inspirations.modName,
		version = Inspirations.modVersion,
		dependencies = "required-after:forge;"
				+ "required-after:mantle",
				acceptedMinecraftVersions = "[1.12, 1.13)")
public class Inspirations {
	public static final String modID = "inspirations";
	public static final String modVersion = "${version}";
	public static final String modName = "Inspirations";

	public static final Logger log = LogManager.getLogger(modID);

	@Mod.Instance(modID)
	public static Inspirations instance;

	public static PulseManager pulseManager = new PulseManager(Config.pulseConfig);
	public static GuiHandler guiHandler = new GuiHandler();

	static {
		pulseManager.registerPulse(new InspirationsRedstone());
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Config.load(event);
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);
	}
}
