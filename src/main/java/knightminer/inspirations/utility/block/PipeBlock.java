package knightminer.inspirations.utility.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import knightminer.inspirations.utility.InspirationsUtility;
import knightminer.inspirations.utility.tileentity.PipeTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DropperBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import slimeknights.mantle.block.InventoryBlock;

import javax.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public class PipeBlock extends InventoryBlock implements IHidable, IWaterLoggable {
	// Facing is the direction we output to.
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	// These six values specify if another pipe/hopper is in this direction for us
	// to visually connect to.
	private static final BooleanProperty NORTH = BlockStateProperties.NORTH;
	private static final BooleanProperty EAST = BlockStateProperties.EAST;
	private static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
	private static final BooleanProperty WEST = BlockStateProperties.WEST;
	private static final BooleanProperty UP = BlockStateProperties.UP;
	private static final BooleanProperty DOWN = BlockStateProperties.DOWN;
	// If this is set, there is a hopper on our output side which isn't facing towards us.
	// We then render a longer pipe to connect with the spout model.
	public static final BooleanProperty HOPPER = BooleanProperty.create("hopper");

	private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	// Direction.getIndex() -> Property. Order is D-U-N-S-W-E
	public static final BooleanProperty[] DIR_ENABLED = new BooleanProperty[] {DOWN, UP, NORTH, SOUTH, WEST, EAST};

	public PipeBlock() {
		super(Block.Properties
				.create(Material.IRON, MaterialColor.STONE)  // Darker than iron blocks.
				.hardnessAndResistance(3.0F, 8.0F)
				.sound(SoundType.METAL)
		);
		this.setDefaultState(this.getStateContainer().getBaseState()
				.with(FACING, Direction.NORTH)
				.with(NORTH, false)
				.with(EAST, false)
				.with(SOUTH, false)
				.with(WEST, false)
				.with(UP, false)
				.with(DOWN, false)
				.with(HOPPER, false)
				.with(WATERLOGGED, false)
		);
	}

	/* IHidable */

	@Override
	public boolean isEnabled() {
		return Config.enablePipe.get();
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> stacks) {
		if(shouldAddtoItemGroup(group)) {
			super.fillItemGroup(group, stacks);
		}
	}

	/* Block state settings */

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED, FACING, NORTH, EAST, SOUTH, WEST, UP, DOWN, HOPPER);
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.with(FACING, mirror.mirror(state.get(FACING)));
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public BlockState updatePostPlacement(BlockState state, Direction neighFacing, BlockState neighState, IWorld world, BlockPos pos, BlockPos neighPos) {
		Direction outFacing = state.get(FACING);

		// We only need to check the one side that updated.
		state = state.with(DIR_ENABLED[neighFacing.getIndex()], canConnectTo(world, pos, outFacing, neighFacing));

		// Check if the output side is a hopper if that side was changed.
		if (outFacing == neighFacing) {
			BlockState offsetState = world.getBlockState(pos.offset(outFacing));
			state = state.with(HOPPER,
						offsetState.getBlock() instanceof HopperBlock &&
						offsetState.get(HopperBlock.FACING) != outFacing.getOpposite()
				);
		}
		return state;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getPos();

		Direction facing = context.getFace().getOpposite();
		// only allow up if allowed in the config.
		if (!Config.pipeUpwards.get() && facing == Direction.UP) {
			facing = context.getPlacementHorizontalFacing();
		}

		BlockState offsetState = world.getBlockState(pos.offset(facing));
		// When first placed, check every side.
		return this.getDefaultState()
				.with(FACING, facing)
				.with(HOPPER, offsetState.getBlock() instanceof HopperBlock && offsetState.get(HopperBlock.FACING) != facing.getOpposite())
				.with(WATERLOGGED, context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER)
				.with(UP,    canConnectTo(world, pos, facing, Direction.UP))
				.with(DOWN,  canConnectTo(world, pos, facing, Direction.DOWN))
				.with(NORTH, canConnectTo(world, pos, facing, Direction.NORTH))
				.with(EAST,  canConnectTo(world, pos, facing, Direction.EAST))
				.with(SOUTH, canConnectTo(world, pos, facing, Direction.SOUTH))
				.with(WEST,  canConnectTo(world, pos, facing, Direction.WEST));
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
		// return false if holding a pipe to make easier to place
		Item item = player.getHeldItem(hand).getItem();
		if(item == InspirationsUtility.pipe.asItem() || Block.getBlockFromItem(item) instanceof HopperBlock) {
			return ActionResultType.PASS;
		}
		return super.onBlockActivated(state, world, pos, player, hand, trace);
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		// If destroyed, drop contents.
		if (state.getBlock() != newState.getBlock()) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof IInventory) {
				InventoryHelper.dropInventoryItems(world, pos, (IInventory)te);
			}
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}

	/* Model and shape */

	private static boolean canConnectTo(IWorld world, BlockPos pos, Direction facing, Direction side) {
		// ignore side pipe is facing
		if(facing == side) return false;

		BlockState state = world.getBlockState(pos.offset(side));
		Block block = state.getBlock();
		Direction opposite = side.getOpposite();
		// if it is a known item output thingy and is facing us, connect
		if((block instanceof PipeBlock || block instanceof DropperBlock) && state.get(FACING) == opposite) return true;
		// hopper check, we can skip on down since hoppers cannot face up
		return side != Direction.DOWN && block instanceof HopperBlock && state.get(HopperBlock.FACING) == opposite;
	}


	/* Tile Entity */
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new PipeTileEntity();
	}

	@Override
	protected boolean openGui(PlayerEntity player, World world, BlockPos pos) {
		if (!(player instanceof ServerPlayerEntity)) {
			throw new AssertionError("Needs to be server!");
		}
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof PipeTileEntity) {
			NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) te, pos);
			return true;
		}
		return false;
	}

	/* Bounds */

	// base bounds
	private static final VoxelShape BOUNDS_CENTER = VoxelShapes.create(0.375, 0.25, 0.375, 0.625, 0.5, 0.625),
			// main bounds for side pipes
			BOUNDS_DOWN  = VoxelShapes.create(0.375, 0, 0.375, 0.625, 0.25, 0.625),
			BOUNDS_UP    = VoxelShapes.create(0.375, 0.5, 0.375, 0.625, 1, 0.625),
			BOUNDS_NORTH = VoxelShapes.create(0.375, 0.25, 0, 0.625, 0.5, 0.375),
			BOUNDS_SOUTH = VoxelShapes.create(0.375, 0.25, 0.625, 0.625, 0.5, 1),
			BOUNDS_WEST  = VoxelShapes.create(0, 0.25, 0.375, 0.375, 0.5, 0.625),
			BOUNDS_EAST  = VoxelShapes.create(0.625, 0.25, 0.375, 1, 0.5, 0.625),
			// extra bounds for the raytrace to select the little connections
			BOUNDS_DOWN_CONNECT  = VoxelShapes.create(0.34375, 0,       0.34375, 0.65625, 0.0625,  0.65625),
			BOUNDS_UP_CONNECT    = VoxelShapes.create(0.34375, 0.9375,  0.34375, 0.65625, 1,       0.65625),
			BOUNDS_NORTH_CONNECT = VoxelShapes.create(0.34375, 0.21875, 0,       0.65625, 0.53125, 0.0625 ),
			BOUNDS_SOUTH_CONNECT = VoxelShapes.create(0.34375, 0.21875, 0.9375,  0.65625, 0.53125, 1      ),
			BOUNDS_WEST_CONNECT  = VoxelShapes.create(0,       0.21875, 0.34375, 0.0625,  0.53125, 0.65625),
			BOUNDS_EAST_CONNECT  = VoxelShapes.create(0.9375,  0.21875, 0.34375, 1,       0.53125, 0.65625);


	// Compute a static lookup table for all the combinations.
	// First index is the facing, the second is a connections bitmask.
	private static final VoxelShape[][] BOUNDS = new VoxelShape[6][64];

	static {
		// above side bounds in an array to index easier - DUNSWE
		VoxelShape[] BOUNDS_SIDES = {
				BOUNDS_DOWN, BOUNDS_UP, BOUNDS_NORTH, BOUNDS_SOUTH, BOUNDS_WEST, BOUNDS_EAST
		};
		VoxelShape[] BOUNDS_CONN_SIDES = {
				BOUNDS_DOWN_CONNECT, BOUNDS_UP_CONNECT, BOUNDS_NORTH_CONNECT,
				BOUNDS_SOUTH_CONNECT, BOUNDS_WEST_CONNECT, BOUNDS_EAST_CONNECT
		};
		for (int i = 0; i < 64; i++) {
			VoxelShape shape = BOUNDS_CENTER;
			for (int j = 0; j < 6; j++) {
				if ((i & (1<<j)) != 0) {
					shape = VoxelShapes.or(shape, BOUNDS_CONN_SIDES[j], BOUNDS_SIDES[j]);
				}
			}
			for (int j = 0; j < 6; j++) {
				BOUNDS[j][i] = VoxelShapes.or(shape, BOUNDS_SIDES[j]);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		int bitmask = 0;
		for (int i = 0; i < 6; i++) {
			bitmask |= state.get(DIR_ENABLED[i]) ? (1 << i) : 0;
		}
		return BOUNDS[state.get(FACING).getIndex()][bitmask];
	}
}
