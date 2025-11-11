package knightminer.inspirations.tweaks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FittedCarpetBlock extends FlatCarpetBlock {
  public FittedCarpetBlock(DyeColor color, Block.Properties props) {
    super(color, props);
    this.registerDefaultState(this.getStateDefinition().any()
                             .setValue(NORTHWEST, false)
                             .setValue(NORTHEAST, false)
                             .setValue(SOUTHWEST, false)
                             .setValue(SOUTHEAST, false));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
    builder.add(NORTHWEST, NORTHEAST, SOUTHWEST, SOUTHEAST);
  }


  /* Bounds */

  private static final VoxelShape[] BOUNDS = new VoxelShape[16];
  static {
    // Compute all the different possible shapes.
    int HIGH = 0;
    int LOW = -8;
    for (int i = 0; i < 16; i++) {
      boolean NW = (i & 0b1000) == 0; // -X -Z
      boolean NE = (i & 0b0100) == 0; // +X -Z
      boolean SW = (i & 0b0010) == 0; // -X +Z
      boolean SE = (i & 0b0001) == 0; // +X +Z

      if (!NW && !NE && !SW && !SE) {
        // Fully lowered, bit of a special case, but should never happen in world.
        BOUNDS[i] = Shapes.or(
            box( 0,  -8,  0, 17, -7, 17),
            box(-1, -16, -1,  0, -7, 17),
            box(-1, -16, -1, 16, -7,  0),
            box(16, -16, -1, 17, -7, 17),
            box(-1, -16, 16, 16, -7, 17));
        continue;
      }

      // First each flat segment, high or low.
      VoxelShape shape = Shapes.or(
          box(0, NW ? HIGH : LOW, 0, 8, (NW ? HIGH : LOW) + 1, 8),
          box(8, NE ? HIGH : LOW, 0, 16, (NE ? HIGH : LOW) + 1, 8),
          box(0, SW ? HIGH : LOW, 8, 8, (SW ? HIGH : LOW) + 1, 16),
          box(8, SE ? HIGH : LOW, 8, 16, (SE ? HIGH : LOW) + 1, 16));

      // Add the lowermost shapes around the base.
      if (!NE && !NW) shape = Shapes.or(shape, box(0, -16, -1, 16, -7, 0));
      if (!SE && !SW) shape = Shapes.or(shape, box(0, -16, 16, 16, -7, 17));
      if (!NW && !SW) shape = Shapes.or(shape, box(-1, -16, 0, 0, -7, 16));
      if (!NE && !SE) shape = Shapes.or(shape, box(16, -16, 0, 17, -7, 16));

      // Then, for each of the spaces between generate verticals if they're different.
      if (NW && !NE) shape = Shapes.or(shape, box(8, LOW, 0, 9, 1, 8));
      if (!NW && NE) shape = Shapes.or(shape, box(7, LOW, 0, 8, 1, 8));

      if (SW && !SE) shape = Shapes.or(shape, box(8, LOW, 8, 9, 1, 16));
      if (!SW && SE) shape = Shapes.or(shape, box(7, LOW, 8, 8, 1, 16));

      if (NW && !SW) shape = Shapes.or(shape, box(0, LOW, 8, 8, 1, 9));
      if (!NW && SW) shape = Shapes.or(shape, box(0, LOW, 7, 8, 1, 8));

      if (NE && !SE) shape = Shapes.or(shape, box(8, LOW, 8, 16, 1, 9));
      if (!NE && SE) shape = Shapes.or(shape, box(8, LOW, 7, 16, 1, 8));

      // Last, a the missing 1x1 post for both heights in outer corners.
      if (!NW && !SE && !SW) shape = Shapes.or(shape, box(7, -8, 8, 8, 1, 9), box(-1, -16, 16,  0, -7, 17)); // NE
      if (!NE && !SE && !SW) shape = Shapes.or(shape, box(8, -8, 8, 9, 1, 9), box(16, -16, 16, 17, -7, 17)); // NW
      if (!NE && !NW && !SW) shape = Shapes.or(shape, box(7, -8, 7, 8, 1, 8), box(-1, -16, -1,  0, -7,  0)); // SE
      if (!NE && !NW && !SE) shape = Shapes.or(shape, box(8, -8, 7, 9, 1, 8), box(16, -16, -1, 17, -7,  0)); // SW

      BOUNDS[i] = shape;
    }
  }

  /**
   * Gets the int key for bounds from the given block state
   * @param state  State
   * @return  Bounds key
   */
  private static int getBoundsKey(BlockState state) {
    return (state.getValue(NORTHWEST) ? 8 : 0) | (state.getValue(NORTHEAST) ? 4 : 0) |
           (state.getValue(SOUTHWEST) ? 2 : 0) | (state.getValue(SOUTHEAST) ? 1 : 0);
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
    return BOUNDS[getBoundsKey(state)];
  }

  @Override
  public BlockState rotate(BlockState state, Rotation rotation) {
    return switch (rotation) {
      case CLOCKWISE_90 -> defaultBlockState()
              .setValue(NORTHEAST, state.getValue(NORTHWEST))
              .setValue(SOUTHEAST, state.getValue(NORTHEAST))
              .setValue(SOUTHWEST, state.getValue(SOUTHEAST))
              .setValue(NORTHWEST, state.getValue(SOUTHWEST));

      case CLOCKWISE_180 -> defaultBlockState()
              .setValue(SOUTHEAST, state.getValue(NORTHWEST))
              .setValue(SOUTHWEST, state.getValue(NORTHEAST))
              .setValue(NORTHWEST, state.getValue(SOUTHEAST))
              .setValue(NORTHEAST, state.getValue(SOUTHWEST));

      case COUNTERCLOCKWISE_90 -> defaultBlockState()
              .setValue(SOUTHWEST, state.getValue(NORTHWEST))
              .setValue(NORTHWEST, state.getValue(NORTHEAST))
              .setValue(NORTHEAST, state.getValue(SOUTHEAST))
              .setValue(SOUTHEAST, state.getValue(SOUTHWEST));

      case NONE -> state; //identity
    };
  }

  @Override
  public BlockState mirror(BlockState state, Mirror mirror) {
    return switch (mirror.rotation()) {
      case INVERT_X -> defaultBlockState()
              .setValue(SOUTHEAST, state.getValue(SOUTHWEST))
              .setValue(SOUTHWEST, state.getValue(SOUTHEAST))
              .setValue(NORTHEAST, state.getValue(NORTHWEST))
              .setValue(NORTHWEST, state.getValue(NORTHEAST));

      case INVERT_Z -> defaultBlockState()
              .setValue(NORTHWEST, state.getValue(SOUTHWEST))
              .setValue(NORTHEAST, state.getValue(SOUTHEAST))
              .setValue(SOUTHWEST, state.getValue(NORTHWEST))
              .setValue(SOUTHEAST, state.getValue(NORTHEAST));

      default -> state; //identity or unsupported operation
    };
  }

}
