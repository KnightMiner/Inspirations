package knightminer.inspirations.tweaks.recipe;

import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public enum FillCauldronFromFluidContainer implements ICauldronRecipe {
	INSTANCE;

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		if(level == 3 || state.getFluid() == null) {
			return false;
		}

		IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(stack);
		if(fluidHandler == null) {
			return false;
		}

		FluidStack fluidStack = fluidHandler.drain(1000, false);
		return fluidStack != null && fluidStack.amount == 1000 && (level == 0 || fluidStack.getFluid() == state.getFluid());
	}

	@Override
	public ItemStack transformInput(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return FluidUtil.getFluidHandler(stack).getContainer();
	}

	@Override
	public int getLevel(int level) {
		return 3;
	}

	@Override
	public CauldronState getState(ItemStack stack, boolean boiling, int level, CauldronState state) {
		Fluid fluid = FluidUtil.getFluidHandler(stack).drain(1000, true).getFluid();
		if(fluid == state.getFluid()) {
			return state;
		}

		return CauldronState.fluid(fluid);
	}

	@Override
	public SoundEvent getSound(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return FluidUtil.getFluidHandler(stack).drain(1000, false).getFluid().getFillSound();
	}

	@Override
	public float getVolume() {
		return 1f;
	}
}
