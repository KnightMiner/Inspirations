package knightminer.inspirations.utility.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;
import net.minecraft.state.properties.Half;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;

public class BlockCarpetedTrapdoor extends TrapDoorBlock {
	private DyeColor color;

   protected static final VoxelShape EAST_OPEN_CARP_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 4.0D, 16.0D, 16.0D);
   protected static final VoxelShape WEST_OPEN_CARP_AABB = Block.makeCuboidShape(12.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape SOUTH_OPEN_CARP_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 4.0D);
   protected static final VoxelShape NORTH_OPEN_CARP_AABB = Block.makeCuboidShape(0.0D, 0.0D, 12.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape BOTTOM_CARP_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);
   protected static final VoxelShape TOP_CARP_AABB = Block.makeCuboidShape(0.0D, 13.0D, 0.0D, 16.0D, 16.9D, 16.0D);

	public BlockCarpetedTrapdoor(DyeColor color) {
		super(Block.Properties
				.create(Material.WOOD)
				.hardnessAndResistance(3.0F)
				.sound(SoundType.CLOTH)
		);
		this.color = color;
	}

	@Nonnull
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
	  boolean isTop = state.get(HALF) == Half.TOP;
      if (!state.get(OPEN)) {
         return isTop ? TOP_CARP_AABB : BOTTOM_CARP_AABB;
      } else {
      	 // Topmost trapdoors open with carpet out, and are therefore thicker.
         switch(state.get(HORIZONTAL_FACING)) {
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
}
