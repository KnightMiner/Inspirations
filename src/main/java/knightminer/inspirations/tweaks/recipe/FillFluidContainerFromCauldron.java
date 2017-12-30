package knightminer.inspirations.tweaks.recipe;

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
		if(level != 3 || state.getFluid() == null) {
			return false;
		}

		IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(stack);
		if(fluidHandler == null) {
			return false;
		}

		return fluidHandler.fill(new FluidStack(state.getFluid(), 1000), false) == 1000;
	}

	@Override
	public ItemStack transformInput(ItemStack stack, boolean boiling, int level, CauldronState state) {
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

	@Override
	public float getVolume() {
		return 1f;
	}
}
