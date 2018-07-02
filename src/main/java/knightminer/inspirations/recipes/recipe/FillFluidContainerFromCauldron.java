package knightminer.inspirations.recipes.recipe;

import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public enum FillFluidContainerFromCauldron implements ICauldronRecipe {
	INSTANCE;

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		if(level != InspirationsRegistry.getCauldronMax() || state.getFluid() == null) {
			return false;
		}

		stack = stack.copy();
		stack.setCount(1); // stack size must be 1 or it fails
		IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(stack);
		if(fluidHandler == null) {
			return false;
		}

		return fluidHandler.fill(new FluidStack(state.getFluid(), 1000), false) == 1000;
	}

	@Override
	public ItemStack getResult(ItemStack stack, boolean boiling, int level, CauldronState state) {
		stack = stack.copy();
		stack.setCount(1);
		FluidUtil.getFluidHandler(stack).fill(new FluidStack(state.getFluid(), 1000), true);
		return FluidUtil.getFluidHandler(stack).getContainer();
	}

	@Override
	public int getLevel(int level) {
		return 0;
	}

	@Override
	public SoundEvent getSound(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return state.getFluid().getEmptySound();
	}
}
