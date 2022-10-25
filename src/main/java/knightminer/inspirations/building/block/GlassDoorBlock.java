package knightminer.inspirations.building.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import knightminer.inspirations.library.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class GlassDoorBlock extends DoorBlock implements IHidable {

  public GlassDoorBlock(Properties props) {
    super(props);
  }

  @Nullable
  @Override
  public BlockPathTypes getAiPathNodeType(BlockState state, BlockGetter world, BlockPos pos, @Nullable Mob entity) {
    return state.getValue(OPEN) ? BlockPathTypes.DOOR_OPEN : BlockPathTypes.DOOR_WOOD_CLOSED;
  }

  @Override
  public boolean isEnabled() {
    return Config.enableGlassDoor.getAsBoolean();
  }

  @Override
  public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
    if (shouldAddtoItemGroup(group)) {
      super.fillItemCategory(group, items);
    }
  }

  // Custom shape to include the protruding handle.
  // Packing:
  // 4 sides, 2 top/bottom, 2 left/right = 16 total.
  private static final VoxelShape[] SHAPES = new VoxelShape[16];

  static {
    for (boolean onTop : new boolean[]{false, true}) {
      for (Direction yaw : Direction.Plane.HORIZONTAL) {
        int ind = (onTop ? 8 : 0) | yaw.get2DDataValue();
        VoxelShape door = MiscUtil.makeRotatedShape(yaw, 0, 0, 0, 16, 16, 3);
        int z1 = onTop ? 0 : 6;
        int z2 = onTop ? 9 : 16;
        VoxelShape handleL = MiscUtil.makeRotatedShape(yaw, 4, z1, -2, 5, z2, 5);
        VoxelShape handleR = MiscUtil.makeRotatedShape(yaw, 11, z1, -2, 12, z2, 5);
        SHAPES[ind] = Shapes.or(door, handleL);
        SHAPES[ind | 4] = Shapes.or(door, handleR);
      }
    }
  }

  @Deprecated
  @Override
  public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return super.getShape(state, worldIn, pos, context);
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    Direction direction = state.getValue(FACING);
    boolean flipped = state.getValue(HINGE) == DoorHingeSide.RIGHT;
    // If open, rotate and flip to replicate the hinge swap.
    if (state.getValue(OPEN)) {
      direction = flipped ? direction.getCounterClockWise() : direction.getClockWise();
      flipped = !flipped;
    }
    int half = state.getValue(HALF) == DoubleBlockHalf.UPPER ? 8 : 0;
    return SHAPES[direction.get2DDataValue() | half | (flipped ? 4 : 0)];
  }
}
