package knightminer.inspirations.tweaks.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.BlockStairs.EnumShape;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockFittedCarpet extends BlockCarpet {

	public static final PropertyBool NORTHWEST = PropertyBool.create("northwest");
	public static final PropertyBool NORTHEAST = PropertyBool.create("northeast");
	public static final PropertyBool SOUTHWEST = PropertyBool.create("southwest");
	public static final PropertyBool SOUTHEAST = PropertyBool.create("southeast");

	public BlockFittedCarpet() {
		super();
		this.setHardness(0.1F);
		this.setSoundType(SoundType.CLOTH);
		this.setUnlocalizedName("woolCarpet");
		this.setLightOpacity(0);

		this.setDefaultState(this.getDefaultState().withProperty(COLOR, EnumDyeColor.WHITE)
				.withProperty(NORTHWEST, false)
				.withProperty(NORTHEAST, false)
				.withProperty(SOUTHWEST, false)
				.withProperty(SOUTHEAST, false));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, COLOR, NORTHWEST, NORTHEAST, SOUTHWEST, SOUTHEAST);
	}

	/**
	 * Get the actual Block state of this Block at the given position. This applies properties not visible in the
	 * metadata, such as fence connections.
	 */
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		BlockPos down = pos.down();
		IBlockState below = world.getBlockState(down);
		Block block = below.getBlock();
		//if(block instanceof BlockSlab && !((BlockSlab)block).isDouble() && below.getValue(BlockSlab.HALF) == EnumBlockHalf.BOTTOM) {
		//	state = setProperties(state, 0b1111);
		//} else
		if(block instanceof BlockStairs && below.getValue(BlockStairs.HALF) == EnumHalf.BOTTOM) {
			below = below.getActualState(world, down);
			state = setProperties(state, getStairShape(below));
		}
		return state;
	}

	private int getStairShape(IBlockState stairs) {
		EnumShape shape = stairs.getValue(BlockStairs.SHAPE);
		// seemed like the simplest way, convert each shape to four bits
		// bits are NW NE SW SE
		switch(stairs.getValue(BlockStairs.FACING)) {
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

	private IBlockState setProperties(IBlockState state, int i) {
		return state
				.withProperty(NORTHWEST, (i & 8) > 0)
				.withProperty(NORTHEAST, (i & 4) > 0)
				.withProperty(SOUTHWEST, (i & 2) > 0)
				.withProperty(SOUTHEAST, (i & 1) > 0);
	}

	@Override
	@Deprecated
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		// if any of the parts are lowered, no collision box
		state = state.getActualState(world, pos);
		if(state.getValue(NORTHWEST) || state.getValue(NORTHEAST) || state.getValue(SOUTHWEST) || state.getValue(SOUTHEAST)) {
			return NULL_AABB;
		}
		return super.getCollisionBoundingBox(state, world, pos);
	}
}
