package knightminer.inspirations.utility.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.state.properties.Half;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class CarpetedTrapdoorBlock extends TrapDoorBlock implements IHidable {
  private static final VoxelShape EAST_OPEN_CARP_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 4.0D, 16.0D, 16.0D);
  private static final VoxelShape WEST_OPEN_CARP_AABB = Block.makeCuboidShape(12.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
  private static final VoxelShape SOUTH_OPEN_CARP_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 4.0D);
  private static final VoxelShape NORTH_OPEN_CARP_AABB = Block.makeCuboidShape(0.0D, 0.0D, 12.0D, 16.0D, 16.0D, 16.0D);
  private static final VoxelShape BOTTOM_CARP_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);
  private static final VoxelShape TOP_CARP_AABB = Block.makeCuboidShape(0.0D, 13.0D, 0.0D, 16.0D, 16.9D, 16.0D);

  public CarpetedTrapdoorBlock() {
    super(Block.Properties
              .create(Material.WOOD)
              .hardnessAndResistance(3.0F)
              .sound(SoundType.CLOTH)
         );
  }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    boolean isTop = state.get(HALF) == Half.TOP;
    if (!state.get(OPEN)) {
      return isTop ? TOP_CARP_AABB : BOTTOM_CARP_AABB;
    } else {
      // Topmost trapdoors open with carpet out, and are therefore thicker.
      switch (state.get(HORIZONTAL_FACING)) {
        case NORTH:
        default:
          return isTop ? NORTH_OPEN_CARP_AABB : NORTH_OPEN_AABB;
        case SOUTH:
          return isTop ? SOUTH_OPEN_CARP_AABB : SOUTH_OPEN_AABB;
        case WEST:
          return isTop ? WEST_OPEN_CARP_AABB : WEST_OPEN_AABB;
        case EAST:
          return isTop ? EAST_OPEN_CARP_AABB : EAST_OPEN_AABB;
      }
    }
  }

  @Override
  public boolean isEnabled() {
    return Config.enableCarpetedTrapdoor.get();
  }
}
