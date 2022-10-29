package knightminer.inspirations.recipes.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class SmashingAnvilBlock extends AnvilBlock {

  public SmashingAnvilBlock(Block.Properties props) {
    super(props);
  }

  // Replace this to handle our different blocks.
  @Nullable
  public static BlockState damage(BlockState state) {
    Block block = state.getBlock();
    if (block == Blocks.ANVIL /*|| block == InspirationsRecipes.fullAnvil*/) {
      return /*InspirationsRecipes.chippedAnvil*/Blocks.CHIPPED_ANVIL.defaultBlockState().setValue(FACING, state.getValue(FACING));
    } else {
      if (block == Blocks.CHIPPED_ANVIL /*|| block == InspirationsRecipes.chippedAnvil*/)
        return /*InspirationsRecipes.damagedAnvil*/Blocks.DAMAGED_ANVIL.defaultBlockState().setValue(FACING, state.getValue(FACING));
      else return null;
    }
  }

  @Override
  public void onLand(Level world, BlockPos pos, BlockState anvil, BlockState target, FallingBlockEntity entity) {
    BlockPos down = pos.below();
    if (!smashBlock(world, down, world.getBlockState(down))) {
      super.onLand(world, pos, anvil, target, entity);
    }
  }

  /**
   * Base logic to smash a block
   * @param world World instance
   * @param pos   Position target
   * @param state State being smashed
   * @return True if somethign was smashed
   */
  @SuppressWarnings("WeakerAccess")
  public static boolean smashBlock(Level world, BlockPos pos, BlockState state) {
    // if we started on air, just return true
    if (state.getBlock() == Blocks.AIR) {
      return true;
    }
    // if the block is unbreakable, leave it
    if (state.getDestroySpeed(world, pos) == -1) {
      return false;
    }

    BlockState transformation = null;//InspirationsRegistry.getAnvilSmashResult(state);
    if (transformation == null) {
      return false;
    }

    // if the result is air, break the block
    if (transformation.getBlock() == Blocks.AIR) {
      world.destroyBlock(pos, true);
    } else {
      // breaking particles
      world.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
      world.setBlockAndUpdate(pos, transformation);
    }
    return true;
  }
}
