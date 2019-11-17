package knightminer.inspirations.recipes.tank;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe.CauldronState;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class CauldronTank implements IFluidHandler {

	private TileCauldron cauldron;
	public CauldronTank(TileCauldron cauldron) {
		this.cauldron = cauldron;
	}

	/* Properties */
  @Override
  public int getTanks() {
    return 1;
  }

  @Override
  public int getTankCapacity(int tank) {
    return 1000;
  }

  @Override
  public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
    return !stack.hasTag();
  }

  @Nonnull
  @Override
  public FluidStack getFluidInTank(int tank) {
    Fluid fluid = cauldron.getState().getFluid();
    return fluid == null ? null : new FluidStack(fluid, 1000);
  }


	/* Filling and draining */

	@Override
	public int fill(FluidStack stack, FluidAction action) {
		// cannot fill with NBT stacks
		if(stack.hasTag()) {
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
		int toInsert = Math.min(getLevels(stack.getAmount()), max - level);
		if(toInsert == 0) {
			return 0;
		}

		if(action == FluidAction.EXECUTE) {
			cauldron.setState(CauldronState.fluid(stack.getFluid()), false);
			cauldron.setFluidLevel(toInsert + level);
		}

		return getAmount(toInsert);
	}

	@Override
	public FluidStack drain(FluidStack stack, FluidAction action) {
		// cannot drain with NBT stacks
		if(stack.hasTag()) {
			return null;
		}

		CauldronState state = cauldron.getState();
		if(state.getFluid() != stack.getFluid()) {
			return null;
		}

		return drain(stack.getAmount(), action);
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
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

		if(action == FluidAction.EXECUTE) {
			cauldron.setFluidLevel(level - toDrain);
		}

		return new FluidStack(state.getFluid(), getAmount(toDrain));
	}


  /* Helpers */

  private static int getLevels(int amount) {
    // if bigger, we got between 0 and 4
    if(Config.enableBiggerCauldron()) {
      return amount / 250;
    }
    // regular is just 3 or 0
    return amount >= 1000 ? 3 : 0;
  }

  private static int getAmount(int levels) {
    if(Config.enableBiggerCauldron()) {
      return levels * 250;
    }
    return levels == 3 ? 1000 : 0;
  }
}
