package knightminer.inspirations.utility.block;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.utility.InspirationsUtility;
import knightminer.inspirations.utility.tileentity.TilePipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockDropper;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import slimeknights.mantle.block.BlockInventory;

public class BlockPipe extends BlockInventory {

	public static final PropertyDirection FACING = BlockDirectional.FACING;
	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyBool UP = PropertyBool.create("up");
	public static final PropertyBool DOWN = PropertyBool.create("down");
	public static final PropertyBool HOPPER = PropertyBool.create("hopper");
	public BlockPipe() {
		super(Material.IRON);
		this.setHardness(3.0F);
		this.setResistance(8.0F);
		this.setSoundType(SoundType.METAL);
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(FACING, EnumFacing.NORTH)
				.withProperty(NORTH, false)
				.withProperty(EAST, false)
				.withProperty(SOUTH, false)
				.withProperty(WEST, false)
				.withProperty(UP, false)
				.withProperty(DOWN, false)
				.withProperty(HOPPER, false));
		this.setCreativeTab(CreativeTabs.REDSTONE);
	}


	/* Block state settings */

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, NORTH, EAST, SOUTH, WEST, UP, DOWN, HOPPER);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror) {
		return state.withRotation(mirror.toRotation(state.getValue(FACING)));
	}

	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		facing = facing.getOpposite();
		// only allow up if allowed in the config
		if (!Config.pipeUpwards && facing == EnumFacing.UP) {
			facing = EnumFacing.DOWN;
		}
		return this.getDefaultState().withProperty(FACING, facing);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float clickX, float clickY, float clickZ) {
		// return false if holding a pipe to make easier to place
		Item item = player.getHeldItem(hand).getItem();
		if(item == Item.getItemFromBlock(InspirationsUtility.pipe) || Block.getBlockFromItem(item) instanceof BlockHopper) {
			return false;
		}
		return super.onBlockActivated(world, pos, state, player, hand, side, clickX, clickY, clickZ);
	}


	/* Model and shape */

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		EnumFacing facing = state.getValue(FACING);
		IBlockState offsetState = world.getBlockState(pos.offset(facing));
		return state.withProperty(HOPPER, offsetState.getBlock() instanceof BlockHopper && offsetState.getValue(BlockHopper.FACING) != facing.getOpposite())
				.withProperty(UP,    canConnectTo(world, pos, facing, EnumFacing.UP))
				.withProperty(DOWN,  canConnectTo(world, pos, facing, EnumFacing.DOWN))
				.withProperty(NORTH, canConnectTo(world, pos, facing, EnumFacing.NORTH))
				.withProperty(EAST,  canConnectTo(world, pos, facing, EnumFacing.EAST))
				.withProperty(SOUTH, canConnectTo(world, pos, facing, EnumFacing.SOUTH))
				.withProperty(WEST,  canConnectTo(world, pos, facing, EnumFacing.WEST));
	}

	private static boolean canConnectTo(IBlockAccess world, BlockPos pos, EnumFacing facing, EnumFacing side) {
		// ignore side pipe is facing
		if(facing == side) return false;

		IBlockState state = world.getBlockState(pos.offset(side));
		Block block = state.getBlock();
		EnumFacing opposite = side.getOpposite();
		// if it is a known item output thingy and is facing us, connect
		// TODO: would be nice to make this dynamic, but I dont think there is a "can push fluids" property to query
		if((block instanceof BlockPipe || block instanceof BlockDropper) && state.getValue(FACING) == opposite) return true;
		// hopper check, we can skip on down since hoppers cannot face up
		return side != EnumFacing.DOWN && block instanceof BlockHopper && state.getValue(BlockHopper.FACING) == opposite;
	}


	/* Tile Entity */

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TilePipe();
	}

	@Override
	protected boolean openGui(EntityPlayer player, World world, BlockPos pos) {
		player.openGui(Inspirations.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}


	/* Bounds */

	// binary map of bounds, order of bits is EWSNUD
	private static final AxisAlignedBB FULL_BOUNDS[] = new AxisAlignedBB[64];
	static {
		for(int i = 0; i < 64; i++) {
			FULL_BOUNDS[i] = new AxisAlignedBB((i & 0b010000) > 0 ? 0 : 0.34375, (i & 0b000001) > 0 ? 0 : 0.21875, (i & 0b000100) > 0 ? 0 : 0.34375,
					(i & 0b100000) > 0 ? 1 : 0.65625, (i & 0b000010) > 0 ? 1 : 0.53125, (i & 0b001000) > 0 ? 1 : 0.65625);
		}
	}

	@Nonnull
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		// yeah, magic bit math stuffs
		// order of bits is EWSNUD, we start by or'ing in the index bit shifted
		state = state.getActualState(source, pos);
		int index = 1 << state.getValue(FACING).getIndex()
				| (state.getValue(EAST) ? 32 : 0)
				| (state.getValue(WEST) ? 16 : 0)
				| (state.getValue(SOUTH) ? 8 : 0)
				| (state.getValue(NORTH) ? 4 : 0)
				| (state.getValue(UP) ? 2 : 0)
				| (state.getValue(DOWN) ? 1 : 0);

		return FULL_BOUNDS[index];
	}

	// base bounds
	private static final AxisAlignedBB BOUNDS_CENTER = new AxisAlignedBB(0.375, 0.25, 0.375, 0.625, 0.5, 0.625),
			// main bounds for side pipes
			BOUNDS_DOWN  = new AxisAlignedBB(0.375, 0,    0.375, 0.625, 0.25, 0.625),
			BOUNDS_UP    = new AxisAlignedBB(0.375, 0.5,  0.375, 0.625, 1,    0.625),
			BOUNDS_NORTH = new AxisAlignedBB(0.375, 0.25, 0,     0.625, 0.5,  0.375),
			BOUNDS_SOUTH = new AxisAlignedBB(0.375, 0.25, 0.625, 0.625, 0.5,  1    ),
			BOUNDS_WEST  = new AxisAlignedBB(0,     0.25, 0.375, 0.375, 0.5,  0.625),
			BOUNDS_EAST  = new AxisAlignedBB(0.625, 0.25, 0.375, 1,     0.5,  0.625),
			// extra bounds for the raytrace to select the little connections
			BOUNDS_DOWN_CONNECT  = new AxisAlignedBB(0.34375, 0,       0.34375, 0.65625, 0.0625,  0.65625),
			BOUNDS_UP_CONNECT    = new AxisAlignedBB(0.34375, 0.9375,  0.34375, 0.59375, 1,       0.65625),
			BOUNDS_NORTH_CONNECT = new AxisAlignedBB(0.34375, 0.21875, 0,       0.65625, 0.53125, 0.0625 ),
			BOUNDS_SOUTH_CONNECT = new AxisAlignedBB(0.34375, 0.21875, 0.9375,  0.65625, 0.53125, 1      ),
			BOUNDS_WEST_CONNECT  = new AxisAlignedBB(0,       0.21875, 0.34375, 0.0625,  0.53125, 0.65625),
			BOUNDS_EAST_CONNECT  = new AxisAlignedBB(0.9375,  0.21875, 0.34375, 1,       0.53125, 0.65625);
	// above side bounds in an array to index easier - DUNSWE
	private static final AxisAlignedBB BOUNDS_SIDES[] = {
			BOUNDS_DOWN, BOUNDS_UP, BOUNDS_NORTH, BOUNDS_SOUTH, BOUNDS_WEST, BOUNDS_EAST
	};

	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean p_185477_7_) {
		state = state.getActualState(world, pos);
		// center
		addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDS_CENTER);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDS_SIDES[state.getValue(FACING).getIndex()]);

		// add each side
		if(state.getValue(UP))    addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDS_UP);
		if(state.getValue(DOWN))  addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDS_DOWN);
		if(state.getValue(NORTH)) addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDS_NORTH);
		if(state.getValue(SOUTH)) addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDS_SOUTH);
		if(state.getValue(WEST))  addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDS_WEST);
		if(state.getValue(EAST))  addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDS_EAST);
	}

	@Deprecated
	@Override
	public RayTraceResult collisionRayTrace(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {
		state = state.getActualState(world, pos);

		// basically the same BlockStairs does
		// Raytrace through all included AABBs (sides) and return the nearest
		// in the case of channels, we need to ensure each piece is actually enabled first though
		List<RayTraceResult> list = new ArrayList<>(8);
		list.add(rayTrace(pos, start, end, BOUNDS_CENTER));
		list.add(rayTrace(pos, start, end, BOUNDS_SIDES[state.getValue(FACING).getIndex()]));

		// add each enabled side
		if(state.getValue(UP)){
			list.add(rayTrace(pos, start, end, BOUNDS_UP));
			list.add(rayTrace(pos, start, end, BOUNDS_UP_CONNECT));
		}
		if(state.getValue(DOWN)) {
			list.add(rayTrace(pos, start, end, BOUNDS_DOWN));
			list.add(rayTrace(pos, start, end, BOUNDS_DOWN_CONNECT));
		}
		if(state.getValue(NORTH)) {
			list.add(rayTrace(pos, start, end, BOUNDS_NORTH));
			list.add(rayTrace(pos, start, end, BOUNDS_NORTH_CONNECT));
		}
		if(state.getValue(SOUTH)) {
			list.add(rayTrace(pos, start, end, BOUNDS_SOUTH));
			list.add(rayTrace(pos, start, end, BOUNDS_SOUTH_CONNECT));
		}
		if(state.getValue(WEST)) {
			list.add(rayTrace(pos, start, end, BOUNDS_WEST));
			list.add(rayTrace(pos, start, end, BOUNDS_WEST_CONNECT));
		}
		if(state.getValue(EAST)) {
			list.add(rayTrace(pos, start, end, BOUNDS_EAST));
			list.add(rayTrace(pos, start, end, BOUNDS_EAST_CONNECT));
		}

		// compare results
		RayTraceResult result = null;
		double max = 0.0D;
		for(RayTraceResult raytraceresult : list) {
			if(raytraceresult != null) {
				double distance = raytraceresult.hitVec.squareDistanceTo(end);
				if(distance > max) {
					result = raytraceresult;
					max = distance;
				}
			}
		}

		return result;
	}
}
