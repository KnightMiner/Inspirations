package knightminer.inspirations.recipes.recipe.cauldron.fill;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

@Deprecated
public enum FluidContainerFillCauldron implements ICauldronRecipe {
  INSTANCE;

  @Override
  public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
    if (level == Config.getCauldronMax() || (level > 0 && state.getFluid() == Fluids.EMPTY)) {
      return false;
    }

    return FluidUtil.getFluidHandler(stack).map(handler -> {
      FluidStack fluidStack = handler.drain(1000, FluidAction.SIMULATE);
      return CauldronState.fluidValid(fluidStack) && (level == 0 || fluidStack.getFluid() == state.getFluid());
    }).orElse(false);
  }

  @Override
  public ItemStack getResult(ItemStack stack, boolean boiling, int level, CauldronState state) {
    return FluidUtil.getFluidHandler(stack.copy()).map(handler -> {
      handler.drain(1000, FluidAction.EXECUTE);
      return handler.getContainer();
    }).orElse(ItemStack.EMPTY);
  }

  @Override
  public int getLevel(int level) {
    return Config.getCauldronMax();
  }

  @Override
  public CauldronState getState(ItemStack stack, boolean boiling, int level, CauldronState state) {
    Fluid fluid = FluidUtil.getFluidHandler(stack).map(h -> h.drain(1000, FluidAction.SIMULATE).getFluid()).orElse(Fluids.EMPTY);
    if (fluid == Fluids.EMPTY || fluid == state.getFluid()) {
      return state;
    }

    return CauldronState.fluid(fluid);
  }

  @Override
  public SoundEvent getSound(ItemStack stack, boolean boiling, int level, CauldronState state) {
    return FluidUtil.getFluidHandler(stack).map(h -> {
      FluidStack fluid = h.drain(1000, FluidAction.SIMULATE);
      return fluid.getFluid().getAttributes().getFillSound(fluid);
    }).orElse(SoundEvents.ITEM_BUCKET_FILL);
  }

  @Override
  public ItemStack getContainer(ItemStack stack) {
    return ItemStack.EMPTY;
  }
}
