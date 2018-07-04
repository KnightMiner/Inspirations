package knightminer.inspirations.plugins.top;

import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe.CauldronState;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.NumberFormat;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class CauldronInfoProvider implements IProbeInfoProvider {

	@Override
	public String getID() {
		return Util.resource("cauldron");
	}

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState state, IProbeHitData data) {
		// first, ensure it is a cauldron
		if(state.getBlock() instanceof BlockEnhancedCauldron) {
			int level = ((BlockEnhancedCauldron)state.getBlock()).getLevel(state);
			int color = 0x0000DD;
			String colorString = null;

			// if we have a TE and the levels are at least 1, try fancy string
			if(level > 0) {
				TileEntity te = world.getTileEntity(data.getPos());
				if(te instanceof TileCauldron) {
					CauldronState cauldron = ((TileCauldron) te).getState();

					// fluids are pretty simple, unlocalized name
					if(cauldron.getFluid() != null) {
						Fluid fluid = cauldron.getFluid();
						probeInfo.horizontal().text(fluid.getLocalizedName(new FluidStack(fluid, 1000)));
						// if we got a color, use that, looks nicer than always blue
						if(fluid.getColor() != -1) {
							color = fluid.getColor();
						}

						// likewise potions are simple
					} else if(cauldron.getPotion() != null) {
						PotionType potion = cauldron.getPotion();
						probeInfo.horizontal().text(Util.translate(potion.getNamePrefixed("potion.effect.")));
						color = PotionUtils.getPotionColor(potion);

						// dyes are a bit more work
					} else if(cauldron.getColor() != -1) {
						color = cauldron.getColor();
						colorString = "#" + Integer.toHexString(color).toUpperCase();

						// first, try a dye color
						EnumDyeColor dyeColor = Util.getDyeForColor(color);
						if(dyeColor != null) {
							probeInfo.horizontal().text(Util.translateFormatted("gui.jei.cauldron.color", Util.translate("item.fireworksCharge.%s", dyeColor.getUnlocalizedName())));
						} else {
							// if not a dye color, just display as is and display the color on the next line
							probeInfo.horizontal().text(Util.translate("gui.inspirations.cauldron.dye"));
						}
					}
				}
			}

			// add the bottles
			probeInfo.horizontal().progress(level, InspirationsRegistry.getCauldronMax(), probeInfo.defaultProgressStyle()
					.filledColor(color | 0xFF000000)
					.suffix(Util.translate("gui.top.inspirations.cauldron.bottles"))
					.alternateFilledColor(0xff000043)
					.borderColor(0xff555555)
					.numberFormat(NumberFormat.COMPACT));

			// and add the color string if given one
			if(colorString != null && mode == ProbeMode.EXTENDED) {
				probeInfo.horizontal().text(Util.translateFormatted("gui.inspirations.cauldron.color", colorString));
			}
		}
	}

}
