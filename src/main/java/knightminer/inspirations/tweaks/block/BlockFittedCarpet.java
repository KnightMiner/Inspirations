package knightminer.inspirations.tweaks.block;

import javax.annotation.Nonnull;

import net.minecraft.block.*;
import net.minecraft.item.DyeColor;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class BlockFittedCarpet extends CarpetBlock {

	public static final BooleanProperty NORTHWEST = BooleanProperty.create("northwest");
	public static final BooleanProperty NORTHEAST = BooleanProperty.create("northeast");
	public static final BooleanProperty SOUTHWEST = BooleanProperty.create("southwest");
	public static final BooleanProperty SOUTHEAST = BooleanProperty.create("southeast");

	public BlockFittedCarpet(DyeColor color, Block original) {
		super(color, Block.Properties.from(original)
				.hardnessAndResistance(0.1F)
		);
		this.setRegistryName(original.getRegistryName());

		this.setDefaultState(this.getDefaultState()
				.with(NORTHWEST, false)
				.with(NORTHEAST, false)
				.with(SOUTHWEST, false)
				.with(SOUTHEAST, false));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(NORTHWEST, NORTHEAST, SOUTHWEST, SOUTHEAST);
	}

	@Nonnull
	@Override
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
		BlockPos down = pos.down();
		BlockState below = world.getBlockState(down);
		Block block = below.getBlock();
		//if(block instanceof BlockSlab && !((BlockSlab)block).isDouble() && below.getValue(BlockSlab.HALF) == EnumBlockHalf.BOTTOM) {
		//	state = setProperties(state, 0b1111);
		//} else
		if(block instanceof StairsBlock && below.get(StairsBlock.HALF) == Half.BOTTOM) {
			state = setProperties(state, getStairShape(below));
		}
		return state;
	}

	private int getStairShape(BlockState stairs) {
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
		return 0;
	}

	private BlockState setProperties(BlockState state, int i) {
		return state
				.with(NORTHWEST, (i & 8) > 0)
				.with(NORTHEAST, (i & 4) > 0)
				.with(SOUTHWEST, (i & 2) > 0)
				.with(SOUTHEAST, (i & 1) > 0);
	}

	@Nonnull
	@Override
	public VoxelShape getCollisionShape(BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, ISelectionContext context) {
		// if any of the parts are lowered, no collision box
		if(state.get(NORTHWEST) || state.get(NORTHEAST) || state.get(SOUTHWEST) || state.get(SOUTHEAST)) {
			return VoxelShapes.empty();
		}
		return super.getCollisionShape(state, world, pos, context);
	}
}
