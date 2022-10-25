package knightminer.inspirations.building.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import knightminer.inspirations.library.Util;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock.Properties;

public class GlassDoorBlock extends DoorBlock implements IHidable {

  public GlassDoorBlock(Properties props) {
    super(props);
  }

  @Nullable
  @Override
  public PathNodeType getAiPathNodeType(BlockState state, IBlockReader world, BlockPos pos, @Nullable MobEntity entity) {
    return state.getValue(OPEN) ? PathNodeType.DOOR_WOOD_CLOSED : PathNodeType.DOOR_WOOD_CLOSED;
  }

  @Override
  public boolean isEnabled() {
    return Config.enableGlassDoor.get();
  }

  @Override
  public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
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
        VoxelShape door = Util.makeRotatedShape(yaw, 0, 0, 0, 16, 16, 3);
        int z1 = onTop ? 0 : 6;
        int z2 = onTop ? 9 : 16;
        VoxelShape handleL = Util.makeRotatedShape(yaw, 4, z1, -2, 5, z2, 5);
        VoxelShape handleR = Util.makeRotatedShape(yaw, 11, z1, -2, 12, z2, 5);
        SHAPES[ind] = VoxelShapes.or(door, handleL);
        SHAPES[ind | 4] = VoxelShapes.or(door, handleR);
      }
    }
  }

  @Deprecated
  @Override
  public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return super.getShape(state, worldIn, pos, context);
  }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
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
