package knightminer.inspirations.utility.dispenser;

import knightminer.inspirations.library.InspirationsTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import java.util.Optional;

public class DispenseFluidTank extends DefaultDispenseItemBehavior {
  private static final DefaultDispenseItemBehavior DEFAULT = new DefaultDispenseItemBehavior();
  private final DispenseItemBehavior fallback;

  public DispenseFluidTank(DispenseItemBehavior fallback) {
    this.fallback = fallback;
  }

  @Override
  protected ItemStack execute(BlockSource source, ItemStack stack) {
    if (!stack.is(InspirationsTags.Items.DISP_FLUID_TANKS)) {
      return fallback.dispense(source, stack);
    }

    Direction side = source.getBlockState().getValue(DispenserBlock.FACING);
    BlockPos pos = source.getPos().relative(side);
    Level world = source.getLevel();
    return FluidUtil.getFluidHandler(world, pos, side.getOpposite()).map((handler) -> {
      FluidActionResult result;
      Optional<FluidStack> optFluid = FluidUtil.getFluidContained(stack);
      if (optFluid.isPresent()) {
        result = FluidUtil.tryEmptyContainer(stack, handler, Integer.MAX_VALUE, null, true);
      } else {
        result = FluidUtil.tryFillContainer(stack, handler, Integer.MAX_VALUE, null, true);
      }

      if (result.isSuccess()) {
        ItemStack resultStack = result.getResult();
        // play sound
        SoundEvent sound = optFluid.map(
            (fluid) -> fluid.getFluid().getAttributes().getEmptySound(fluid)
                                       ).orElseGet(() -> {
          FluidStack resultFluid = FluidUtil.getFluidContained(resultStack).orElseThrow(AssertionError::new);
          return resultFluid.getFluid().getAttributes().getFillSound(resultFluid);
        });

        world.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);

        if (stack.getCount() == 1) {
          return resultStack;
        }

        if (!resultStack.isEmpty() && ((DispenserBlockEntity)source.getEntity()).addItem(resultStack) < 0) {
          DEFAULT.dispense(source, resultStack);
        }

        ItemStack shrink = stack.copy();
        shrink.shrink(1);
        return shrink;
      }
      // TODO: fallback?
      return DEFAULT.dispense(source, stack);
    }).orElseGet(() -> fallback.dispense(source, stack));
  }
}
