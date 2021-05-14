package knightminer.inspirations.building.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import knightminer.inspirations.library.Util;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class GlassDoorBlock extends DoorBlock implements IHidable {

  public GlassDoorBlock(Properties props) {
    super(props);
  }

  @Override
  public boolean isEnabled() {
    return Config.enableGlassDoor.get();
  }

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    if (shouldAddtoItemGroup(group)) {
      super.fillItemGroup(group, items);
    }
  }

  // Custom shape to include the protruding handle.
  // Packing:
  // 4 sides, 2 top/bottom, 2 left/right = 16 total.
  private static final VoxelShape[] SHAPES = new VoxelShape[16];

  static {
    for (boolean onTop : new boolean[]{false, true}) {
      for (Direction yaw : Direction.Plane.HORIZONTAL) {
        int ind = (onTop ? 8 : 0) | yaw.getHorizontalIndex();
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

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    Direction direction = state.get(FACING);
    boolean flipped = state.get(HINGE) == DoorHingeSide.RIGHT;
    // If open, rotate and flip to replicate the hinge swap.
    if (state.get(OPEN)) {
      direction = flipped ? direction.rotateYCCW() : direction.rotateY();
      flipped = !flipped;
    }
    int half = state.get(HALF) == DoubleBlockHalf.UPPER ? 8 : 0;
    return SHAPES[direction.getHorizontalIndex() | half | (flipped ? 4 : 0)];
  }
}
