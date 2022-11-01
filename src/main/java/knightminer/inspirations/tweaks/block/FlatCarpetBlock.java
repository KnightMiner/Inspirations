package knightminer.inspirations.tweaks.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.tweaks.InspirationsTweaks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public class FlatCarpetBlock extends WoolCarpetBlock {
  protected static final BooleanProperty NORTHWEST = BooleanProperty.create("northwest");
  protected static final BooleanProperty NORTHEAST = BooleanProperty.create("northeast");
  protected static final BooleanProperty SOUTHWEST = BooleanProperty.create("southwest");
  protected static final BooleanProperty SOUTHEAST = BooleanProperty.create("southeast");

  // No bits set.
  private static final int SHAPE_FLAT = 0;

  public FlatCarpetBlock(DyeColor color, Block.Properties props) {
    super(color, props);
  }

  @Override
  public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
    if (!state.canSurvive(world, pos)) {
      return Blocks.AIR.defaultBlockState();
    }
    int shape = getStairShape(world.getBlockState(pos.below()));

    if (shape != SHAPE_FLAT) {
      return InspirationsTweaks.fitCarpets.get(getColor())
                                          .defaultBlockState()
                                          .setValue(NORTHWEST, (shape & 8) > 0)
                                          .setValue(NORTHEAST, (shape & 4) > 0)
                                          .setValue(SOUTHWEST, (shape & 2) > 0)
                                          .setValue(SOUTHEAST, (shape & 1) > 0);
    } else {
      return InspirationsTweaks.flatCarpets.get(getColor()).defaultBlockState();
    }
  }

  @Override
  public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
    world.setBlock(pos, updateShape(state, Direction.UP, state, world, pos, pos), 2);
  }

  @Override
  public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
    return new ItemStack(InspirationsTweaks.flatCarpets.get(getColor()));
  }

  /**
   * Given the potential stair block below, return the shape to use for the carpet.
   * @param stairs The state of the block underneath.
   * @return SHAPE_FLAT if flat, or a value to pass to withShape().
   */
  private static int getStairShape(BlockState stairs) {
    if (!Config.enableFittedCarpets.getAsBoolean()) {
      return SHAPE_FLAT;
      // } else if(stairs instanceof BlockSlab && !((BlockSlab)stairs).isDouble() && stairs.getValue(BlockSlab.HALF) == EnumBlockHalf.BOTTOM) {
      //	return 0b1111;
    } else if (!(stairs.getBlock() instanceof StairBlock) ||
               stairs.getValue(StairBlock.HALF) != Half.BOTTOM) {
      return SHAPE_FLAT;
    }

    StairsShape shape = stairs.getValue(StairBlock.SHAPE);
    // seemed like the simplest way, convert each shape to four bits
    // bits are NW NE SW SE
    return switch (stairs.getValue(StairBlock.FACING)) {
      case NORTH -> switch (shape) {
        case STRAIGHT -> 0b0011;
        case INNER_LEFT -> 0b0001;
        case INNER_RIGHT -> 0b0010;
        case OUTER_LEFT -> 0b0111;
        case OUTER_RIGHT -> 0b1011;
      };
      case SOUTH -> switch (shape) {
        case STRAIGHT -> 0b1100;
        case INNER_LEFT -> 0b1000;
        case INNER_RIGHT -> 0b0100;
        case OUTER_LEFT -> 0b1110;
        case OUTER_RIGHT -> 0b1101;
      };
      case WEST -> switch (shape) {
        case STRAIGHT -> 0b0101;
        case INNER_LEFT -> 0b0100;
        case INNER_RIGHT -> 0b0001;
        case OUTER_LEFT -> 0b1101;
        case OUTER_RIGHT -> 0b0111;
      };
      case EAST -> switch (shape) {
        case STRAIGHT -> 0b1010;
        case INNER_LEFT -> 0b0010;
        case INNER_RIGHT -> 0b1000;
        case OUTER_LEFT -> 0b1011;
        case OUTER_RIGHT -> 0b1110;
      };
      default -> SHAPE_FLAT;
    };
  }
}
