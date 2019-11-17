package knightminer.inspirations.building.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.block.HidableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

public class RopeBlock extends HidableBlock implements IWaterLoggable {

	public static final EnumProperty<Rungs> RUNGS = EnumProperty.create("rungs", Rungs.class);
	public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	// Number of items used per block.
	public static final int RUNG_ITEM_COUNT = 4;

	private Item rungsItem;

	public RopeBlock(Item rungsItem, Properties props) {
		super(props, Config.enableRope::get);
		this.setDefaultState(this.stateContainer.getBaseState()
				.with(BOTTOM, false)
				.with(RUNGS, Rungs.NONE)
				.with(WATERLOGGED, false)
		);
		this.rungsItem = rungsItem;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(BOTTOM, RUNGS, WATERLOGGED);
	}

	@Deprecated
	@Override
	@Nonnull
	public IFluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	public Item getRungsItem() {
		return rungsItem;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockPos down = context.getPos().down();
		return getDefaultState()
				.with(BOTTOM, !canConnectTo(context.getWorld().getBlockState(down), context.getWorld(), down))
				.with(RUNGS, Rungs.NONE)
				.with(WATERLOGGED, context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER);
	}

	private static final VoxelShape ATTACH_TOP = Block.makeCuboidShape(6.0, 15, 6.0, 10.0, 16, 10.0);
	private static final VoxelShape ATTACH_BOTTOM = Block.makeCuboidShape(6.0, 0, 6.0, 10.0, 1, 10.0);

	private boolean canConnectTo(BlockState state, IBlockReader world, BlockPos pos) {
		if (state.getBlock() == this) {
			return true;
		}
		// Check if the top of the block is able to attach to the rope - the center 4x4 must
		// all be present.
		return !state.isIn(BlockTags.LEAVES) && !VoxelShapes.compare(
				state.getCollisionShape(world, pos).project(Direction.UP), ATTACH_TOP, IBooleanFunction.ONLY_SECOND
		);
	}

	/* Ropey logic */

	@Deprecated
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
		return super.isValidPosition(state, world, pos) && isValidRope(world, pos);
	}

	private boolean isValidRope(IWorldReader world, BlockPos pos) {
		BlockPos up = pos.up();
		BlockState state = world.getBlockState(up);
		if (state.getBlock() == this) {
			return true;
		}
		// Check if the bottom of the block is able to attach to the rope - the center 4x4 must
		// all be present.
		return !state.isIn(BlockTags.LEAVES) && !VoxelShapes.compare(
				state.getCollisionShape(world, pos).project(Direction.DOWN), ATTACH_BOTTOM, IBooleanFunction.ONLY_SECOND
		);
	}

	@Deprecated
	@Override
	@Nonnull
	public BlockState updatePostPlacement(@Nonnull BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
		// if the rope is not valid, break it
		if (!this.isValidRope(world, pos)) {
			return Blocks.AIR.getDefaultState();
		}
		if (facing == Direction.DOWN) {
			BlockPos down = pos.down();
			return state.with(BOTTOM, !canConnectTo(world.getBlockState(down), world, down));
		}
		return state;
	}

	// right click with a rope to extend downwards
	@Deprecated
	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		// no need to check verticals, one is not possible and the other normal block placement
		if (hit.getFace().getAxis().isVertical()) {
			return false;
		}

		ItemStack stack = player.getHeldItem(hand);
		// check if the item is the same type as us
		if (Block.getBlockFromItem(stack.getItem()) != this) {
			return false;
		}

		// find the first block at the bottom of the rope
		BlockPos next = pos.down();
		while (world.getBlockState(next).getBlock() == this) {
			next = next.down();
		}
		if (this.isValidPosition(state, world, next)) {
			BlockItem itemBlock = (BlockItem) stack.getItem();
			if (itemBlock.tryPlace(new DirectionalPlaceContext(world, next, hit.getFace(), stack, hit.getFace())) == ActionResultType.SUCCESS) {
				if (player.isCreative()) {
					// Refund the item.
					stack.grow(1);
				}
			}
		}

		return true;
	}

	// when breaking, place all items from ropes below at the position of this rope
	@Override
	public void onBlockHarvested(World world, BlockPos pos, BlockState state, @Nonnull PlayerEntity player) {
		// break all blocks below that are ropes
		BlockPos next = pos.down();
		int count = 0;
		int rungs = 0;
		// go down to the bottom
		BlockState below = world.getBlockState(next);
		while (below.getBlock() == this) {
			count++;
			if (below.get(RUNGS) != Rungs.NONE) {
				rungs++;
			}
			next = next.down();
			below = world.getBlockState(next);
		}
		// then break them coming back up
		for (int i = 0; i < count; i++) {
			next = next.up();
			world.destroyBlock(next, false);
		}

		// then spawn their items up here
		ItemStack drops = new ItemStack(this, count);
		spawnAsEntity(world, pos, drops);
		if (rungs > 0) {
			spawnAsEntity(world, pos, new ItemStack(rungsItem, rungs * RUNG_ITEM_COUNT));
		}

		super.onBlockHarvested(world, pos, state, player);
	}

	/* Block properties */

	@Override
	public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity) {
		return true;
	}

	@Nonnull
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}


	/* Bounds */

	// Shape for collisions. Indexes are Rungs ordinals.
	public static final VoxelShape[] SHAPE = new VoxelShape[3];
	public static final VoxelShape[] SHAPE_BOTTOM = new VoxelShape[3];

	static {
		VoxelShape rope_core = Block.makeCuboidShape(7, 0, 7, 9, 16, 9);
		VoxelShape rope_core_bottom = VoxelShapes.or(
				Block.makeCuboidShape(7, 7, 7, 9, 16, 9),
				Block.makeCuboidShape(6.5, 4, 6.5, 9.5, 7, 9.5)
		);

		VoxelShape rope_rungs_x = VoxelShapes.or(
				Block.makeCuboidShape(1, 5, 7, 15, 7, 9),
				Block.makeCuboidShape(1, 9, 7, 15, 11, 9),
				Block.makeCuboidShape(1, 13, 7, 15, 15, 9)
		);
		VoxelShape rope_rungs_z = VoxelShapes.or(
				Block.makeCuboidShape(7, 5, 1, 9, 7, 15),
				Block.makeCuboidShape(7, 9, 1, 9, 11, 15),
				Block.makeCuboidShape(7, 13, 1, 9, 15, 15)
		);

		SHAPE[Rungs.NONE.ordinal()] = rope_core;
		SHAPE_BOTTOM[Rungs.NONE.ordinal()] = rope_core_bottom;

		SHAPE[Rungs.X.ordinal()] = VoxelShapes.or(rope_core, rope_rungs_x,
				Block.makeCuboidShape(1, 1, 7, 15, 3, 9)
		);
		SHAPE_BOTTOM[Rungs.X.ordinal()] = VoxelShapes.or(rope_core_bottom, rope_rungs_x);

		SHAPE[Rungs.Z.ordinal()] = VoxelShapes.or(rope_core, rope_rungs_z,
				Block.makeCuboidShape(7, 1, 1, 9, 3, 15)
		);
		SHAPE_BOTTOM[Rungs.Z.ordinal()] = VoxelShapes.or(rope_core_bottom, rope_rungs_z);
	}

	@Deprecated
	@Nonnull
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return (state.get(BOTTOM) ? SHAPE_BOTTOM: SHAPE)[state.get(RUNGS).ordinal()];
	}

	public enum Rungs implements IStringSerializable {
		NONE,
		X,
		Z;

		@Override
		public String getName() {
			return this.name().toLowerCase(Locale.US);
		}

		public static Rungs fromAxis(Direction.Axis axis) {
			switch (axis) {
				case X:
					return X;
				case Z:
					return Z;
			}
			return NONE;
		}
	}
}
