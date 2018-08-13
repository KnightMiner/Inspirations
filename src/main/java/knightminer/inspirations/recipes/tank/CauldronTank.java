package knightminer.inspirations.recipes.tank;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe.CauldronState;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class CauldronTank implements IFluidHandler {

	private TileCauldron cauldron;
	private Properties[] properties;
	public CauldronTank(TileCauldron cauldron) {
		this.cauldron = cauldron;
		this.properties = new Properties[] {new Properties(cauldron)};
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return properties;
	}

	private static int getLevels(int amount) {
		// if bigger, we got between 0 and 4
		if(Config.enableBiggerCauldron) {
			return amount / 250;
		}
		// regular is just 3 or 0
		return amount >= 1000 ? 3 : 0;
	}

	private static int getAmount(int levels) {
		if(Config.enableBiggerCauldron) {
			return levels * 250;
		}
		return levels == 3 ? 1000 : 0;
	}

	@Override
	public int fill(FluidStack stack, boolean doFill) {
		// cannot fill with NBT stacks
		if(stack.tag != null) {
			return 0;
		}

		// if the fluid is different, its not allowed
		// note the fluid will be null if a non-fluid type, but will be water for an empty cauldron
		int level = cauldron.getFluidLevel();
		int max = InspirationsRegistry.getCauldronMax();
		if(level == max) {
			return 0;
		}

		// validate fluid
		CauldronState state = cauldron.getState();
		if(level == max || (level > 0 && state.getFluid() != stack.getFluid())) {
			return 0;
		}

		// determine how much fluid we can insert
		int toInsert = Math.min(getLevels(stack.amount), max - level);
		if(toInsert == 0) {
			return 0;
		}

		if(doFill) {
			cauldron.setState(CauldronState.fluid(stack.getFluid()), false);
			cauldron.setFluidLevel(toInsert + level);
		}

		return getAmount(toInsert);
	}

	@Override
	public FluidStack drain(FluidStack stack, boolean doDrain) {
		// cannot drain with NBT stacks
		if(stack.tag != null) {
			return null;
		}

		CauldronState state = cauldron.getState();
		if(state.getFluid() != stack.getFluid()) {
			return null;
		}

		return drain(stack.amount, doDrain);
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		CauldronState state = cauldron.getState();
		if(state.getFluid() == null) {
			return null;
		}

		// nothing to drain
		int level = cauldron.getFluidLevel();
		if(level == 0) {
			return null;
		}

		// nothing can drain
		int toDrain = Math.min(getLevels(maxDrain), level);
		if(toDrain == 0) {
			return null;
		}

		if(doDrain) {
			cauldron.setFluidLevel(level - toDrain);
		}

		return new FluidStack(state.getFluid(), getAmount(toDrain));
	}

	private static class Properties implements IFluidTankProperties {
		private TileCauldron cauldron;
		public Properties(TileCauldron cauldron) {
			this.cauldron = cauldron;
		}

		@Override
		public FluidStack getContents() {
			Fluid fluid = cauldron.getState().getFluid();
			if(fluid == null) {
				return null;
			}

			// determine fluid amount
			int amount = cauldron.getFluidLevel();
			if(amount == 0) {
				return null;
			}

			return new FluidStack(fluid, getAmount(amount));
		}

		@Override
		public int getCapacity() {
			return 1000;
		}

		@Override
		public boolean canFill() {
			return true;
		}

		@Override
		public boolean canDrain() {
			return true;
		}

		@Override
		public boolean canFillFluidType(FluidStack fluidStack) {
			return fluidStack.tag == null;
		}

		@Override
		public boolean canDrainFluidType(FluidStack fluidStack) {
			return fluidStack.tag == null;
		}
	}
}
