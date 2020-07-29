package knightminer.inspirations.tweaks.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.tweaks.InspirationsTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public class FlatCarpetBlock extends CarpetBlock {
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
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
		if (!state.isValidPosition(world, pos)) {
			return Blocks.AIR.getDefaultState();
		}
		int shape = getStairShape(world.getBlockState(pos.down()));

		if (shape != SHAPE_FLAT) {
			return InspirationsTweaks.fitCarpets.get(getColor())
										.getDefaultState()
										.with(NORTHWEST, (shape & 8) > 0)
										.with(NORTHEAST, (shape & 4) > 0)
										.with(SOUTHWEST, (shape & 2) > 0)
										.with(SOUTHEAST, (shape & 1) > 0);
		} else {
			return InspirationsTweaks.flatCarpets.get(getColor()).getDefaultState();
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		world.setBlockState(pos, updatePostPlacement(state, null, null, world, pos, null), 2);
	}

	/**
	 * Always produce the original carpet item, not the altered carpet blocks.
	 */
	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		return new ItemStack(InspirationsTweaks.flatCarpets.get(getColor()));
	}

	/**
	 * Given the potential stair block below, return the shape to use for the carpet.
	 * @param stairs The state of the block underneath.
	 * @return SHAPE_FLAT if flat, or a value to pass to withShape().
	 */
	private static int getStairShape(BlockState stairs) {
		if (!Config.enableFittedCarpets.get()) {
			return SHAPE_FLAT;
		// } else if(stairs instanceof BlockSlab && !((BlockSlab)stairs).isDouble() && stairs.getValue(BlockSlab.HALF) == EnumBlockHalf.BOTTOM) {
		//	return 0b1111;
		} else if (!(stairs.getBlock() instanceof StairsBlock) ||
				stairs.get(StairsBlock.HALF) != Half.BOTTOM) {
			return SHAPE_FLAT;
		}

		StairsShape shape = stairs.get(StairsBlock.SHAPE);
		// seemed like the simplest way, convert each shape to four bits
		// bits are NW NE SW SE
		switch(stairs.get(StairsBlock.FACING)) {
			case NORTH:
				switch(shape) {
					case STRAIGHT:    return 0b0011;
					case INNER_LEFT:  return 0b0001;
					case INNER_RIGHT: return 0b0010;
					case OUTER_LEFT:  return 0b0111;
					case OUTER_RIGHT: return 0b1011;
				}
			case SOUTH:
				switch(shape) {
					case STRAIGHT:    return 0b1100;
					case INNER_LEFT:  return 0b1000;
					case INNER_RIGHT: return 0b0100;
					case OUTER_LEFT:  return 0b1110;
					case OUTER_RIGHT: return 0b1101;
				}
			case WEST:
				switch(shape) {
					case STRAIGHT:    return 0b0101;
					case INNER_LEFT:  return 0b0100;
					case INNER_RIGHT: return 0b0001;
					case OUTER_LEFT:  return 0b1101;
					case OUTER_RIGHT: return 0b0111;
				}
			case EAST:
				switch(shape) {
					case STRAIGHT:    return 0b1010;
					case INNER_LEFT:  return 0b0010;
					case INNER_RIGHT: return 0b1000;
					case OUTER_LEFT:  return 0b1011;
					case OUTER_RIGHT: return 0b1110;
				}
		}
		return SHAPE_FLAT;
	}
}
