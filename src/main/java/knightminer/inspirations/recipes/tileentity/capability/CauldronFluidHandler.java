package knightminer.inspirations.recipes.tileentity.capability;

import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import knightminer.inspirations.recipes.tileentity.CauldronTileEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import static knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe.MAX;
import static knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe.QUARTER;

import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

/**
 * Logic to treat the cauldron as a fluid tank
 */
public class CauldronFluidHandler implements IFluidHandler {
  private final CauldronTileEntity cauldron;
  /** Cache of current contents. Null when not yet calculated */
  private FluidStack currentContents;

  /**
   * Creates a new tank instance
   * @param cauldron  Cauldron instance
   */
  public CauldronFluidHandler(CauldronTileEntity cauldron) {
    this.cauldron = cauldron;
  }



  /* Filling and draining */

  @Override
  public int fill(FluidStack stack, FluidAction action) {
    // cannot fill with NBT stacks
    if (stack.hasTag()) {
      return 0;
    }

    // if more than 3 quarters, block
    int level = cauldron.getFluidLevel();
    if (level < QUARTER * 3) {
      // if the fluid is different, prevent insertion
      Fluid fluid = stack.getFluid();
      if (level > 0 || getFluidStack().getFluid() == fluid) {
        // determine how much fluid we can insert
        int toInsert = Math.min(getLevels(stack.getAmount()), MAX - level);
        if (toInsert != 0) {
          // update on execute
          if (action == FluidAction.EXECUTE) {
            cauldron.updateStateAndBlock(CauldronContentTypes.FLUID.of(fluid), toInsert + level);
          }
          return getAmount(toInsert);
        }
      }
    }

    // failed to insert
    return 0;
  }

  /**
   * Shared logic for fluid draining
   * @param fluid     Fluid to drain
   * @param maxDrain  Amount to drain
   * @param action    Whether to execute the drain
   * @return  Drained fluid stack
   */
  private FluidStack drain(Fluid fluid, int maxDrain, FluidAction action) {
    // minimum of a quarter to drain
    int level = cauldron.getFluidLevel();
    if (level >= QUARTER) {
      // check if they are draining enough
      int toDrain = Math.min(getLevels(maxDrain), level);
      if (toDrain > 0) {
        // update on execute
        if (action == FluidAction.EXECUTE) {
          cauldron.updateStateAndBlock(null, level - toDrain);
        }
        // return fluid
        return new FluidStack(fluid, getAmount(toDrain));
      }
    }
    return FluidStack.EMPTY;
  }

  @Override
  public FluidStack drain(FluidStack stack, FluidAction action) {
    // cannot drain with NBT stacks or wrong fluid
    FluidStack current = getFluidStack();
    if (!current.isEmpty() && !stack.hasTag() && current.getFluid() == stack.getFluid()) {
      return drain(stack.getFluid(), stack.getAmount(), action);
    }

    // cannot drain
    return FluidStack.EMPTY;
  }

  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    FluidStack current = getFluidStack();
    if (!current.isEmpty()) {
      return drain(current.getFluid(), maxDrain, action);
    }

    // cannot drain
    return FluidStack.EMPTY;
  }

  /* Properties */

  @Override
  public int getTanks() {
    return 1;
  }

  @Override
  public int getTankCapacity(int tank) {
    return FluidAttributes.BUCKET_VOLUME;
  }

  @Override
  public boolean isFluidValid(int tank, FluidStack stack) {
    return !stack.hasTag();
  }

  @Override
  public FluidStack getFluidInTank(int tank) {
    return getFluidStack();
  }


  /* Helpers */

  /**
   * Clears the contents cache
   */
  public void clearCache() {
    currentContents = null;
  }

  /**
   * Gets the current fluid stack in the cauldron
   * @return  Current fluid stack
   */
  private FluidStack getFluidStack() {
    if (currentContents == null) {
      currentContents = cauldron.getContents()
                                .get(CauldronContentTypes.FLUID)
                                .map(fluid -> new FluidStack(fluid, getAmount(cauldron.getFluidLevel())))
                                .orElse(FluidStack.EMPTY);
    }
    return currentContents;
  }

  /**
   * Converts a fluid amount to cauldron levels
   * @param amount  Amount to convert
   * @return  Cauldron levels amount
   */
  private static int getLevels(int amount) {
    return MathHelper.clamp(amount * 4 / FluidAttributes.BUCKET_VOLUME, 0, 4) * ICauldronRecipe.QUARTER;
  }

  /**
   * Converts a level amount to a fluid amount
   * @param levels  Levels amount
   * @return  Fluid amount
   */
  private static int getAmount(int levels) {
    return MathHelper.clamp((levels / ICauldronRecipe.QUARTER) * FluidAttributes.BUCKET_VOLUME / 4, 0, FluidAttributes.BUCKET_VOLUME);
  }
}
