package knightminer.inspirations.tweaks.block;

import knightminer.inspirations.tweaks.InspirationsTweaks;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

// Hopper block with waterlogged=True.
public class WetHopperBlock extends DryHopperBlock {
  public WetHopperBlock(Properties props) {
    super(props);
  }

  @Deprecated
  @Override
  public FluidState getFluidState(BlockState state) {
    return Fluids.WATER.getStillFluidState(false);
  }

  // Duplicate IWaterLoggable's code, but don't use the property.
  @Override
  public boolean canContainFluid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid) {
    // No new fluid can be inserted.
    return false;
  }

  @Override
  public boolean receiveFluid(IWorld world, BlockPos pos, BlockState state, FluidState fluid) {
    return false;
  }

  @Override
  public Fluid pickupFluid(IWorld world, BlockPos pos, BlockState state) {
    // Swap the block but don't alter the properties itself.
    world.setBlockState(pos, InspirationsTweaks.dryHopper.getDefaultState()
                                                         .with(FACING, state.get(FACING))
                                                         .with(ENABLED, state.get(ENABLED))
        , 3);
    return Fluids.WATER;
  }
}
