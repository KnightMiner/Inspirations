package knightminer.inspirations.tweaks.block;

import knightminer.inspirations.tweaks.InspirationsTweaks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class DryHopperBlock extends HopperBlock implements SimpleWaterloggedBlock {
  public DryHopperBlock(Properties props) {
    super(props);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    Direction side = context.getClickedFace().getOpposite();
    Block block = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER
                  ? InspirationsTweaks.wetHopper : InspirationsTweaks.dryHopper;
    return block.defaultBlockState()
                .setValue(FACING, side.getAxis() == Axis.Y ? Direction.DOWN : side)
                .setValue(ENABLED, true);
  }

  @Override
  public void onRemove(BlockState state1, Level world, BlockPos pos, BlockState state2, boolean moving) {
    if (state2.getBlock() != state1.getBlock() &&
        state2.getBlock() != InspirationsTweaks.dryHopper &&
        state2.getBlock() != InspirationsTweaks.wetHopper
    ) {
      BlockEntity te = world.getBlockEntity(pos);
      if (te instanceof HopperBlockEntity) {
        Containers.dropContents(world, pos, (HopperBlockEntity)te);
        world.updateNeighbourForOutputSignal(pos, this);
      }

      super.onRemove(state1, world, pos, state2, moving);
    }
  }

  // Duplicate IWaterLoggable's code, but don't use the property.
  @Override
  public boolean canPlaceLiquid(BlockGetter world, BlockPos pos, BlockState state, Fluid fluid) {
    return fluid == Fluids.WATER;
  }

  @Override
  public boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluid) {
    if (fluid.getType() == Fluids.WATER) {
      if (!world.isClientSide()) {
        // Swap the block but don't alter the properties itself.
        world.setBlock(pos, InspirationsTweaks.wetHopper.defaultBlockState()
                                                             .setValue(FACING, state.getValue(FACING))
                                                             .setValue(ENABLED, state.getValue(ENABLED))
            , 3);
        world.scheduleTick(pos, fluid.getType(), fluid.getType().getTickDelay(world));
      }
      return true;
    } else {
      return false;
    }
  }

  @Override
  public ItemStack pickupBlock(LevelAccessor world, BlockPos pos, BlockState state) {
    return ItemStack.EMPTY;
  }
}
