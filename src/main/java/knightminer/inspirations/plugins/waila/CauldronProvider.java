package knightminer.inspirations.plugins.waila;

import java.util.List;

import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe.CauldronState;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.block.BlockState;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class CauldronProvider implements IComponentProvider {

	@Override
	public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
		if(!config.get(WailaPlugin.CONFIG_CAULDRON)) {
			return;
		}
		World world = accessor.getWorld();
		BlockPos pos = accessor.getPosition();

		// first, ensure it is a cauldron
		BlockState state = accessor.getBlockState();
		if(state.getBlock() instanceof BlockEnhancedCauldron) {
			int level = ((BlockEnhancedCauldron)state.getBlock()).getLevel(state, world, pos);
			String colorString = null;

			// if we have a TE and the levels are at least 1, try fancy string
			if(level > 0 && accessor.getTileEntity() instanceof TileCauldron) {
				CauldronState cauldron = ((TileCauldron) accessor.getTileEntity()).getState();

				// fluids are pretty simple, unlocalized name
				if(cauldron.getFluid() != null) {
					Fluid fluid = cauldron.getFluid();
					tooltip.add(new TranslationTextComponent(fluid.getUnlocalizedName(new FluidStack(fluid, 1000))));

					// likewise potions are simple
				} else if(cauldron.getPotion() != null) {
					tooltip.add(new TranslationTextComponent(cauldron.getPotion().getNamePrefixed("potion.effect.")));

					// dyes are a bit more work
				} else if(cauldron.getColor() != -1) {
					int color = cauldron.getColor();
					colorString = "#" + Integer.toHexString(color).toUpperCase();

					// first, try a dye color
					DyeColor dyeColor = Util.getDyeForColor(color);
					if(dyeColor != null) {
						tooltip.add(new TranslationTextComponent("gui.jei.cauldron.color",
								new TranslationTextComponent("item.fireworksCharge.%s", dyeColor.getTranslationKey())
						));
					} else {
						// if not a dye color, just display as is and display the color on the next line
						tooltip.add(new TranslationTextComponent("gui.inspirations.cauldron.dye"));
					}
				}
			}

			// add bottles
			tooltip.add(new TranslationTextComponent("gui.waila.inspirations.cauldron.bottles", level, InspirationsRegistry.getCauldronMax()));

			// if we were given a color string, show it
			if(colorString != null && accessor.getPlayer().isSneaking()) {
				tooltip.add(new TranslationTextComponent("gui.inspirations.cauldron.color", colorString));
			}
		}
	}
}
