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
import net.minecraft.block.AbstractBlock.Properties;

public class WetHopperBlock extends DryHopperBlock {
  public WetHopperBlock(Properties props) {
    super(props);
  }

  @Deprecated
  @Override
  public FluidState getFluidState(BlockState state) {
    return Fluids.WATER.getSource(false);
  }

  // Duplicate IWaterLoggable's code, but don't use the property.
  @Override
  public boolean canPlaceLiquid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid) {
    // No new fluid can be inserted.
    return false;
  }

  @Override
  public boolean placeLiquid(IWorld world, BlockPos pos, BlockState state, FluidState fluid) {
    return false;
  }

  @Override
  public Fluid takeLiquid(IWorld world, BlockPos pos, BlockState state) {
    // Swap the block but don't alter the properties itself.
    world.setBlock(pos, InspirationsTweaks.dryHopper.defaultBlockState()
                                                         .setValue(FACING, state.getValue(FACING))
                                                         .setValue(ENABLED, state.getValue(ENABLED))
        , 3);
    return Fluids.WATER;
  }
}
