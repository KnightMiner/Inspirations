package knightminer.inspirations.tweaks.block;

import knightminer.inspirations.tweaks.InspirationsTweaks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

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
  public boolean canPlaceLiquid(BlockGetter world, BlockPos pos, BlockState state, Fluid fluid) {
    // No new fluid can be inserted.
    return false;
  }

  @Override
  public boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluid) {
    return false;
  }

  @Override
  public ItemStack pickupBlock(LevelAccessor world, BlockPos pos, BlockState state) {
      world.setBlock(pos, InspirationsTweaks.dryHopper.defaultBlockState()
                                                      .setValue(FACING, state.getValue(FACING))
                                                      .setValue(ENABLED, state.getValue(ENABLED)), 3);
      return new ItemStack(Items.WATER_BUCKET);
  }
}
