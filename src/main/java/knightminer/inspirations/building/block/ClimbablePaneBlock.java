package knightminer.inspirations.building.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.PaneBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class ClimbablePaneBlock extends PaneBlock {
  public ClimbablePaneBlock(Properties builder) {
    super(builder);
  }

  @Override
  public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity) {
    return world.getBlockState(pos.down()).getBlock() instanceof RopeBlock;
  }
}
