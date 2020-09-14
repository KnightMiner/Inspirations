package knightminer.inspirations.recipes.tank;

import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import knightminer.inspirations.recipes.tileentity.CauldronTileEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * Logic to treat the cauldron as a fluid tank
 */
public class CauldronTank implements IFluidHandler {
  private final CauldronTileEntity cauldron;
  public CauldronTank(CauldronTileEntity cauldron) {
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
  public boolean isFluidValid(int tank, FluidStack stack) {
    return !stack.hasTag();
  }

  @Override
  public FluidStack getFluidInTank(int tank) {
    return cauldron.getContents()
                   .get(CauldronContentTypes.FLUID)
                   .map(fluid -> new FluidStack(fluid, 1000))
                   .orElse(FluidStack.EMPTY);
  }


  /* Filling and draining */

  @Override
  public int fill(FluidStack stack, FluidAction action) {
    // cannot fill with NBT stacks
    if (stack.hasTag()) {
      return 0;
    }

    // if full, block
    int level = cauldron.getLevel();
    if (level == ICauldronRecipe.MAX) {
      return 0;
    }

    // if the fluid is different, block
    Fluid fluid = stack.getFluid();
    if (level > 0 && !cauldron.getContents().matches(CauldronContentTypes.FLUID, fluid)) {
      return 0;
    }

    // determine how much fluid we can insert
    int toInsert = Math.min(getLevels(stack.getAmount()), ICauldronRecipe.MAX - level);
    if (toInsert == 0) {
      return 0;
    }

    // update on execute
    if (action == FluidAction.EXECUTE) {
      cauldron.updateStateAndBlock(CauldronContentTypes.FLUID.of(fluid), toInsert + level);
    }

    return getAmount(toInsert);
  }

  /**
   * Shared logic for fluid draining
   * @param fluid     Fluid to drain
   * @param maxDrain  Amount to drain
   * @param action    Whether to execute the drain
   * @return  Drained fluid stack
   */
  private FluidStack drain(Fluid fluid, int maxDrain, FluidAction action) {
    // nothing to drain
    int level = cauldron.getLevel();
    if (level == 0) {
      return FluidStack.EMPTY;
    }

    // nothing can drain
    int toDrain = Math.min(getLevels(maxDrain), level);
    if (toDrain == 0) {
      return FluidStack.EMPTY;
    }

    // update on execute
    if (action == FluidAction.EXECUTE) {
      cauldron.updateStateAndBlock(null, level - toDrain);
    }

    // return fluid
    return new FluidStack(fluid, getAmount(toDrain));
  }

  @Override
  public FluidStack drain(FluidStack stack, FluidAction action) {
    // cannot drain with NBT stacks
    if (stack.hasTag()) {
      return FluidStack.EMPTY;
    }

    // need contents as a fluid type matching the given fluid
    return cauldron.getContents()
                   .get(CauldronContentTypes.FLUID)
                   .filter(stack.getFluid()::equals)
                   .map(fluid -> drain(fluid, stack.getAmount(), action))
                   .orElse(FluidStack.EMPTY);
  }

  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    // simply need contents to be a fluid type
    return cauldron.getContents()
                   .get(CauldronContentTypes.FLUID)
                   .map(fluid -> drain(fluid, maxDrain, action))
                   .orElse(FluidStack.EMPTY);
  }


  /* Helpers */

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
