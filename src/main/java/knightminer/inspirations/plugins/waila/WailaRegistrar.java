package knightminer.inspirations.plugins.waila;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import mcp.mobius.waila.api.IWailaRegistrar;

public class WailaRegistrar {

	static final String CONFIG_CAULDRON = Util.prefix("cauldron");
	public static void registerWaila(IWailaRegistrar registrar) {
		// config entries
		registrar.addConfig(Inspirations.modName, CONFIG_CAULDRON, true);

		// cauldron info
		registrar.registerBodyProvider(new CauldronProvider(), TileCauldron.class);
	}
}
