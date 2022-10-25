package knightminer.inspirations.building.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class ClimbablePaneBlock extends IronBarsBlock {
  public ClimbablePaneBlock(Properties builder) {
    super(builder);
  }

  @Override
  public boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
    return world.getBlockState(pos.below()).getBlock() instanceof RopeBlock;
  }
}
