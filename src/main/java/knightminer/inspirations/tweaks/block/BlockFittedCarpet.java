package knightminer.inspirations.tweaks.block;

import knightminer.inspirations.library.Util;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.BlockStairs.EnumShape;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
		return setProperties(state, getStairShape(world, pos.down()));
	}

	@Override
	@Deprecated
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		// if any of the parts are lowered, no collision box
		if(getStairShape(world, pos) > 0) {
			return NULL_AABB;
		}
		return super.getCollisionBoundingBox(state, world, pos);
	}

	private static final AxisAlignedBB[] BOUNDS;
	private static final AxisAlignedBB BOUNDS_NW = new AxisAlignedBB(0.0,    0.0, 0.0,    0.5625, 0.0625, 0.5625);
	private static final AxisAlignedBB BOUNDS_NE = new AxisAlignedBB(0.4375, 0.0, 0.0,    1.0,    0.0625, 0.5625);
	private static final AxisAlignedBB BOUNDS_SW = new AxisAlignedBB(0.0,    0.0, 0.4375, 0.5625, 0.0625, 1.0);
	private static final AxisAlignedBB BOUNDS_SE = new AxisAlignedBB(0.4375, 0.0, 0.4375, 1.0,    0.0625, 1.0);
	static {
		// bits are NW NE SW SE
		BOUNDS = new AxisAlignedBB[]{
				CARPET_AABB, // 0000
				CARPET_AABB, // SE: 0001
				CARPET_AABB, // SW: 0010
				new AxisAlignedBB(0.0, 0.0, 0.0, 1.0,    0.0625, 0.5625), // 0011, NORTH
				CARPET_AABB, // NE: 0100
				new AxisAlignedBB(0.0, 0.0, 0.0, 0.5625, 0.0625, 1.0), // 0101, WEST
				CARPET_AABB, // 0110
				BOUNDS_NW,   // 0111
				CARPET_AABB, // 1000
				CARPET_AABB, // 1001
				new AxisAlignedBB(0.4375, 0.0, 0.0,    1.0, 0.0625, 1.0), // 1010, EAST
				BOUNDS_NE, // 1011
				new AxisAlignedBB(0.0,    0.0, 0.4375, 1.0, 0.0625, 1.0), // 1100, SOUTH
				BOUNDS_SW, // 1101
				BOUNDS_SE, // 1110
				CARPET_AABB, // 1111
		};
	}
	@Override
	@Deprecated
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BOUNDS[getStairShape(source, pos.down())];
	}

	@Deprecated
	@Override
	public RayTraceResult collisionRayTrace(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {
		int shape = getStairShape(world, pos.down());
		// if three corners up or checkerboard, run on just up
		switch(shape) {
			case 0b0001:
			case 0b0010:
			case 0b0100:
			case 0b0110:
			case 0b1000:
			case 0b1001:
				break;
			default:
				return super.collisionRayTrace(state, world, pos, start, end);
		}

		// basically the same BlockStairs does
		// Raytrace through all AABBs (plate, legs) and return the nearest one
		List<RayTraceResult> list = new ArrayList<>();
		if ((shape & 0b1000) == 0) list.add(rayTrace(pos, start, end, BOUNDS_NW));
		if ((shape & 0b0100) == 0) list.add(rayTrace(pos, start, end, BOUNDS_NE));
		if ((shape & 0b0010) == 0) list.add(rayTrace(pos, start, end, BOUNDS_SW));
		if ((shape & 0b0001) == 0) list.add(rayTrace(pos, start, end, BOUNDS_SE));

		return Util.closestResult(list, end);
	}


	/* Utils */

	/**
	 * Gets the shape for the carpet from the given stairs
	 * @param world  World access
	 * @param pos    Stairs position
	 * @return  4 bit integer with bits in order NW, NE, SW, SE. If the bit is set, the carpet is down
	 */
	private int getStairShape(IBlockAccess world, BlockPos pos) {
		IBlockState stairs = world.getBlockState(pos);
		if (!(stairs.getBlock() instanceof BlockStairs) || stairs.getValue(BlockStairs.HALF) == EnumHalf.TOP) {
			return 0b0000;
		}

		// seemed like the simplest way, convert each shape to four bits
		// bits are NW NE SW SE
		stairs = stairs.getActualState(world, pos);
		EnumShape shape = stairs.getValue(BlockStairs.SHAPE);
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
		return 0b0000;
	}

	/**
	 * Sets the state properties based on the given shape
	 * @param state  Carpet block state
	 * @param i Shape integer from {@link #getStairShape(IBlockAccess, BlockPos)}
	 * @return  New stairs shape
	 */
	private IBlockState setProperties(IBlockState state, int i) {
		return state
				.withProperty(NORTHWEST, (i & 8) > 0)
				.withProperty(NORTHEAST, (i & 4) > 0)
				.withProperty(SOUTHWEST, (i & 2) > 0)
				.withProperty(SOUTHEAST, (i & 1) > 0);
	}
}
