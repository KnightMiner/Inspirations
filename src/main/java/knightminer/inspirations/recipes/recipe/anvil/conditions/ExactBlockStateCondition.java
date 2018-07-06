package knightminer.inspirations.recipes.recipe.anvil.conditions;

import knightminer.inspirations.recipes.recipe.anvil.IBlockStateCondition;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * Condition that is true if the block state matches exactly.
 */
public class ExactBlockStateCondition implements IBlockStateCondition {
  private final IBlockState[] stateExpected;

  public ExactBlockStateCondition(@Nonnull IBlockState stateExpected) {
    this(new IBlockState[]{stateExpected});
  }

  public ExactBlockStateCondition(@Nonnull IBlockState[] stateExpected) {
    this.stateExpected = stateExpected;
  }

  public IBlockState[] getStateExpected() {
    return stateExpected;
  }

  @Override
  public boolean matches(World world, BlockPos pos, @Nonnull IBlockState state) {
    return Arrays.stream(stateExpected).anyMatch(state::equals);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ExactBlockStateCondition that = (ExactBlockStateCondition) o;
    return Arrays.equals(stateExpected, that.stateExpected);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(stateExpected);
  }
}
