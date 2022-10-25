package knightminer.inspirations.utility.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CarpetedTrapdoorBlock extends TrapDoorBlock implements IHidable {
  private static final VoxelShape EAST_OPEN_CARP_AABB = Block.box(0.0D, 0.0D, 0.0D, 4.0D, 16.0D, 16.0D);
  private static final VoxelShape WEST_OPEN_CARP_AABB = Block.box(12.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
  private static final VoxelShape SOUTH_OPEN_CARP_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 4.0D);
  private static final VoxelShape NORTH_OPEN_CARP_AABB = Block.box(0.0D, 0.0D, 12.0D, 16.0D, 16.0D, 16.0D);
  private static final VoxelShape BOTTOM_CARP_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);
  private static final VoxelShape TOP_CARP_AABB = Block.box(0.0D, 13.0D, 0.0D, 16.0D, 16.9D, 16.0D);

  public CarpetedTrapdoorBlock() {
    super(Block.Properties
              .of(Material.WOOD)
              .strength(3.0F)
              .sound(SoundType.WOOL)
         );
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    boolean isTop = state.getValue(HALF) == Half.TOP;
    if (!state.getValue(OPEN)) {
      return isTop ? TOP_CARP_AABB : BOTTOM_CARP_AABB;
    } else {
      // Topmost trapdoors open with carpet out, and are therefore thicker.
      return switch (state.getValue(FACING)) {
        default -> isTop ? NORTH_OPEN_CARP_AABB : NORTH_OPEN_AABB;
        case SOUTH -> isTop ? SOUTH_OPEN_CARP_AABB : SOUTH_OPEN_AABB;
        case WEST -> isTop ? WEST_OPEN_CARP_AABB : WEST_OPEN_AABB;
        case EAST -> isTop ? EAST_OPEN_CARP_AABB : EAST_OPEN_AABB;
      };
    }
  }

  @Override
  public boolean isEnabled() {
    return Config.enableCarpetedTrapdoor.getAsBoolean();
  }
}
