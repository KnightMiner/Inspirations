package knightminer.inspirations.plugins.waila;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import net.minecraft.util.ResourceLocation;

@mcp.mobius.waila.api.WailaPlugin(Inspirations.modID)
public class WailaPlugin implements IWailaPlugin {
	static final ResourceLocation CONFIG_CAULDRON = Util.getResource("cauldron");

	@Override
	public void register(IRegistrar registrar) {
		// config entries
		registrar.addConfig(CONFIG_CAULDRON, true);

		// cauldron info
		registrar.registerComponentProvider(new CauldronProvider(), TooltipPosition.BODY, TileCauldron.class);
	}
}
