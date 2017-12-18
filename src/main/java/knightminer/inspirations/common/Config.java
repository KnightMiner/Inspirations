package knightminer.inspirations.common;

import java.util.function.BooleanSupplier;

import com.google.gson.JsonObject;

import knightminer.inspirations.Inspirations;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import slimeknights.mantle.pulsar.config.ForgeCFG;

public class Config {

	public static ForgeCFG pulseConfig = new ForgeCFG("inspirationsModules", "Modules");

	private static Configuration configFile;

	public static boolean showAllVariants = true;
	public static void load(FMLPreInitializationEvent event) {
		configFile = new Configuration(event.getSuggestedConfigurationFile(), "0.1", false);

		if(configFile.hasChanged()) {
			configFile.save();
		}
	}


	public static class PulseLoaded implements IConditionFactory {
		@Override
		public BooleanSupplier parse(JsonContext context, JsonObject json) {
			String pulse = JsonUtils.getString(json, "pulse");
			return () -> Inspirations.pulseManager.isPulseLoaded(pulse);
		}
	}
}
