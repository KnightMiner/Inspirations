package knightminer.inspirations.plugins.waila;

import java.util.List;

import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe.CauldronState;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class CauldronProvider implements IWailaDataProvider {
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> current, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		if(!config.getConfig(WailaRegistrar.CONFIG_CAULDRON)) {
			return current;
		}

		// first, ensure it is a cauldron
		IBlockState state = accessor.getBlockState();
		if(state.getBlock() instanceof BlockEnhancedCauldron) {
			int level = ((BlockEnhancedCauldron)state.getBlock()).getLevel(state);
			String colorString = null;

			// if we have a TE and the levels are at least 1, try fancy string
			if(level > 0 && accessor.getTileEntity() instanceof TileCauldron) {
				CauldronState cauldron = ((TileCauldron) accessor.getTileEntity()).getState();

				// fluids are pretty simple, unlocalized name
				if(cauldron.getFluid() != null) {
					Fluid fluid = cauldron.getFluid();
					current.add(fluid.getLocalizedName(new FluidStack(fluid, 1000)));

					// likewise potions are simple
				} else if(cauldron.getPotion() != null) {
					current.add(Util.translate(cauldron.getPotion().getNamePrefixed("potion.effect.")));

					// dyes are a bit more work
				} else if(cauldron.getColor() != -1) {
					int color = cauldron.getColor();
					colorString = "#" + Integer.toHexString(color).toUpperCase();

					// first, try a dye color
					EnumDyeColor dyeColor = Util.getDyeForColor(color);
					if(dyeColor != null) {
						current.add(Util.translateFormatted("gui.jei.cauldron.color", Util.translate("item.fireworksCharge.%s", dyeColor.getUnlocalizedName())));
					} else {
						// if not a dye color, just display as is and display the color on the next line
						current.add(Util.translate("gui.inspirations.cauldron.dye"));
					}
				}
			}

			// add bottles
			current.add(Util.translateFormatted("gui.waila.inspirations.cauldron.bottles", level, InspirationsRegistry.getCauldronMax()));

			// if we were given a color string, show it
			if(colorString != null && accessor.getPlayer().isSneaking()) {
				current.add(Util.translateFormatted("gui.inspirations.cauldron.color", colorString));
			}
		}
		return current;
	}
}
