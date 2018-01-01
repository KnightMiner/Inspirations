package knightminer.inspirations.plugins.tan.recipe;

import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.plugins.tan.ToughAsNailsPlugin;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public enum TANFillBucketFromCauldron implements ICauldronRecipe {
	INSTANCE;

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		if(level != 3) {
			return false;
		}

		Fluid current = state.getFluid();
		if(current != ToughAsNailsPlugin.dirtyWater && current != ToughAsNailsPlugin.filteredWater) {
			return false;
		}

		IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(stack);
		if(fluidHandler == null) {
			return false;
		}

		return fluidHandler.fill(new FluidStack(FluidRegistry.WATER, 1000), false) == 1000;
	}

	@Override
	public ItemStack transformInput(ItemStack stack, boolean boiling, int level, CauldronState state) {
		IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(stack);
		fluidHandler.fill(new FluidStack(FluidRegistry.WATER, 1000), true);
		return fluidHandler.getContainer();
	}

	@Override
	public int getLevel(int level) {
		return 0;
	}

	@Override
	public SoundEvent getSound(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return FluidRegistry.WATER.getEmptySound();
	}
}
