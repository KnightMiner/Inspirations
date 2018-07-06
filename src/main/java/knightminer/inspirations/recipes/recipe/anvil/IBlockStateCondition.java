package knightminer.inspirations.recipes.recipe.anvil;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Test if the block matches a condition.
 */
public interface IBlockStateCondition {
  boolean matches(World world, BlockPos pos, @Nonnull IBlockState state);

  default String getTooltip() {
    return null;
  }
}
