package knightminer.inspirations.recipes.recipe;

import knightminer.inspirations.library.InspirationsRegistry;
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
		if(level == InspirationsRegistry.getCauldronMax() || (level > 0 && state.getFluid() == null)) {
			return false;
		}

		IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(stack);
		if(fluidHandler == null) {
			return false;
		}

		FluidStack fluidStack = fluidHandler.drain(1000, false);
		return fluidStack != null && fluidStack.amount == 1000 && (level == 0 || fluidStack.getFluid() == state.getFluid()) && fluidStack.tag == null;
	}

	@Override
	public ItemStack getResult(ItemStack stack, boolean boiling, int level, CauldronState state) {
		IFluidHandlerItem handler = FluidUtil.getFluidHandler(stack.copy());
		handler.drain(1000, true);
		return handler.getContainer();
	}

	@Override
	public int getLevel(int level) {
		return InspirationsRegistry.getCauldronMax();
	}

	@Override
	public CauldronState getState(ItemStack stack, boolean boiling, int level, CauldronState state) {
		Fluid fluid = FluidUtil.getFluidHandler(stack).drain(1000, false).getFluid();
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
	public ItemStack getContainer(ItemStack stack) {
		return ItemStack.EMPTY;
	}
}
