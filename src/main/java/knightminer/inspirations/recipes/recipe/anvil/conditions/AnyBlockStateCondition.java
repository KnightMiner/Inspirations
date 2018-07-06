package knightminer.inspirations.recipes.recipe.anvil.conditions;

import knightminer.inspirations.recipes.recipe.anvil.IBlockStateCondition;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Matches any blockstate.
 */
public class AnyBlockStateCondition implements IBlockStateCondition {
  private static AnyBlockStateCondition instance = new AnyBlockStateCondition();

  public static IBlockStateCondition get() {
    return instance;
  }

  @Override
  public boolean matches(World world, BlockPos pos, @Nonnull IBlockState state) {
    return true;
  }
}
