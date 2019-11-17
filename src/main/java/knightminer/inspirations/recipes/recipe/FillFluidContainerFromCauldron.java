package knightminer.inspirations.recipes.recipe;

import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public enum FillFluidContainerFromCauldron implements ICauldronRecipe {
	INSTANCE;

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, final CauldronState state) {
		if(level != InspirationsRegistry.getCauldronMax() || state.getFluid() == null) {
			return false;
		}

		stack = stack.copy();
		stack.setCount(1); // stack size must be 1 or it fails
		return FluidUtil.getFluidHandler(stack).map(handler -> handler.fill(new FluidStack(state.getFluid(), 1000), FluidAction.SIMULATE) == 1000).orElse(false);
	}

	@Override
	public ItemStack getResult(ItemStack stack, boolean boiling, int level, CauldronState state) {
		stack = stack.copy();
		stack.setCount(1);
		return FluidUtil.getFluidHandler(stack).map(handler -> {
			handler.fill(state.getFluidStack(), FluidAction.EXECUTE);
			return handler.getContainer();
		}).orElse(ItemStack.EMPTY);
	}

	@Override
	public int getLevel(int level) {
		return 0;
	}

	@Override
	public SoundEvent getSound(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return state.getFluid().getAttributes().getFillSound();
	}

	@Override
	public ItemStack getContainer(ItemStack stack) {
		return ItemStack.EMPTY;
	}
}
