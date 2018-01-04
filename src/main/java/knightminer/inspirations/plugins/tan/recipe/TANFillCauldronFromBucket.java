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

public enum TANFillCauldronFromBucket implements ICauldronRecipe {
	INSTANCE;

	private static CauldronState dirtyWater = CauldronState.fluid(ToughAsNailsPlugin.dirtyWater);

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		if(level == 3) {
			return false;
		}
		Fluid current = state.getFluid();
		if(current != FluidRegistry.WATER && current != ToughAsNailsPlugin.dirtyWater & current != ToughAsNailsPlugin.filteredWater) {
			return false;
		}

		IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(stack);
		if(fluidHandler == null) {
			return false;
		}

		FluidStack fluidStack = fluidHandler.drain(1000, false);
		return fluidStack != null && fluidStack.amount == 1000 && (level == 0 || fluidStack.getFluid() == FluidRegistry.WATER);
	}

	@Override
	public ItemStack transformInput(ItemStack stack, boolean boiling, int level, CauldronState state) {
		IFluidHandlerItem handler = FluidUtil.getFluidHandler(stack);
		handler.drain(1000, true);
		return handler.getContainer();
	}

	@Override
	public int getLevel(int level) {
		return 3;
	}

	@Override
	public CauldronState getState(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return dirtyWater;
	}

	@Override
	public SoundEvent getSound(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return FluidRegistry.WATER.getEmptySound();
	}
}
