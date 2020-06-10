package knightminer.inspirations.recipes.block;

import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.recipes.entity.SmashingAnvilEntity;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class SmashingAnvilBlock extends AnvilBlock {

  public SmashingAnvilBlock(Block.Properties props) {
    super(props);
  }

  // Replace this to handle our different blocks.
  @Nullable
  public static BlockState damage(BlockState state) {
    Block block = state.getBlock();
    if (block == Blocks.ANVIL || block == InspirationsRecipes.fullAnvil) {
      return InspirationsRecipes.chippedAnvil.getDefaultState().with(FACING, state.get(FACING));
    } else {
      if (block == Blocks.CHIPPED_ANVIL || block == InspirationsRecipes.chippedAnvil)
        return InspirationsRecipes.damagedAnvil.getDefaultState().with(FACING, state.get(FACING));
      else return null;
    }
  }

  public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
    if (world.isAirBlock(pos.down()) || canFallThrough(world.getBlockState(pos.down())) && pos.getY() >= 0) {
      FallingBlockEntity fallingblockentity = new SmashingAnvilEntity(world, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, world.getBlockState(pos));
      world.addEntity(fallingblockentity);
    }
  }

}
