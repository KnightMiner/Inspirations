package knightminer.inspirations.library.recipe.cauldron;

import java.util.List;

import com.google.common.collect.ImmutableList;

import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

/**
 * Recipe to combine two fluids into an item output
 */
public class CauldronMixRecipe implements ISimpleCauldronRecipe {

	private CauldronState input1, input2;
	private ItemStack result;

	/**
	 * Combines two inputs into an item output. Input order does not matter, both can be either the container or in the cauldron
	 * @param result  Item result
	 * @param input1  First fluid for either container or cauldron, will show in JEI as the cauldron contents
	 * @param input2  Second fluid for either container or cauldron, will show in JEI as a bucket
	 */
	public CauldronMixRecipe(Fluid input1, Fluid input2, ItemStack result) {
		this.result = result;
		this.input1 = CauldronState.fluid(input1);
		this.input2 = CauldronState.fluid(input2);
	}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		// cauldron must be full of a fluid
		if(level != InspirationsRegistry.getCauldronMax() || state.getFluid() == null) {
			return false;
		}

		// stack must be a fluid container
		LazyOptional<IFluidHandlerItem> maybefluidHandler = FluidUtil.getFluidHandler(stack);
		if(!maybefluidHandler.isPresent()) {
			return false;
		}
		IFluidHandlerItem fluidHandler = maybefluidHandler.orElseThrow(() -> new RuntimeException("Not present?"));

		// fluid in stack must be a cauldron fluid
		FluidStack fluidStack = fluidHandler.drain(1000, false);
		if(!CauldronState.fluidValid(fluidStack)) {
			return false;
		}

		// either input must be in the cauldron
		Fluid fluid = fluidStack.getFluid();
		return input1.matches(state) && input2.getFluid() == fluid
				|| input2.matches(state) && input1.getFluid() == fluid;
	}

	@Override
	public int getLevel(int level) {
		return 0;
	}

	/* JEI */
	@Override
	public Fluid getInputState() {
		return input1.getFluid();
	}

	@Override
	public List<ItemStack> getInput() {
		return ImmutableList.of(FluidUtil.getFilledBucket(input2.getFluidStack()));
	}

	@Override
	public ItemStack getResult() {
		return result;
	}

	@Override
	public String toString() {
		return String.format("CauldronMixRecipe: %s from %s and %s", getResult().toString(), input1.getFluid().getName(), input2.getFluid().getName());
	}
}
