package knightminer.inspirations.building.block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.block.HidableBlock;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.*;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.*;

import java.util.Locale;

public class BlockRope extends HidableBlock implements IWaterLoggable {

	public static final EnumProperty<Rungs> RUNGS = EnumProperty.create("rungs", Rungs.class);
	public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	// Number of items used per block.
	public static final int RUNG_ITEM_COUNT = 4;

	private Item rungsItem;

	// Shape for collisions. Indexes are Rungs ordinals.
	private final VoxelShape[] shape;
	private final VoxelShape[] shape_bottom;

	public BlockRope(Properties props, Item rungsItem, VoxelShape[] shape, VoxelShape[] shape_bottom) {
		super(props, Config.enableRope::get);
		this.setDefaultState(this.stateContainer.getBaseState()
				.with(BOTTOM, false)
				.with(RUNGS, Rungs.NONE)
				.with(WATERLOGGED, false)
		);
		this.rungsItem = rungsItem;
		this.shape = shape;
		this.shape_bottom = shape_bottom;
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(BOTTOM, RUNGS, WATERLOGGED);
	}

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

	/**
	 * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
	 * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
	 * returns its solidified counterpart.
	 * Note that this method should ideally consider only the specific face passed in.
	 */
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

	// VoxelShape][Rungs]
	public static final VoxelShape[] SHAPE_ROPE = new VoxelShape[3];
	public static final VoxelShape[] SHAPE_ROPE_BOTTOM = new VoxelShape[3];
	public static final VoxelShape[] SHAPE_CHAIN = new VoxelShape[3];
	public static final VoxelShape[] SHAPE_CHAIN_BOTTOM = new VoxelShape[3];

	static {
		VoxelShape rope_core = Block.makeCuboidShape(7, 0, 7, 9, 10, 9);
		VoxelShape rope_core_bottom = VoxelShapes.or(
				Block.makeCuboidShape(7, 8, 7, 9, 16, 9),
				Block.makeCuboidShape(6.5, 4, 6.5, 9.5, 8, 9.5)
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

		SHAPE_ROPE[Rungs.NONE.ordinal()] = rope_core;
		SHAPE_ROPE_BOTTOM[Rungs.NONE.ordinal()] = rope_core_bottom;

		SHAPE_ROPE[Rungs.X.ordinal()] = VoxelShapes.or(rope_core, rope_rungs_x,
				Block.makeCuboidShape(1, 1, 7, 15, 3, 9)
		);
		SHAPE_ROPE_BOTTOM[Rungs.X.ordinal()] = VoxelShapes.or(rope_core_bottom, rope_rungs_x);

		SHAPE_ROPE[Rungs.Z.ordinal()] = VoxelShapes.or(rope_core, rope_rungs_z,
				Block.makeCuboidShape(7, 1, 1, 9, 3, 15)
		);
		SHAPE_ROPE_BOTTOM[Rungs.Z.ordinal()] = VoxelShapes.or(rope_core_bottom, rope_rungs_z);

		VoxelShape chain_core = VoxelShapes.or(
				Block.makeCuboidShape(6.5, 0, 7.5, 9.5, 16, 8.5),
				Block.makeCuboidShape(7.5, 0, 6.5, 8.5, 16, 9.5)
		);
		VoxelShape chain_core_bottom = VoxelShapes.or(
				Block.makeCuboidShape(6.5, 4, 7.5, 9.5, 16, 8.5),
				Block.makeCuboidShape(7.5, 4, 6.5, 8.5, 16, 9.5)
		);

		VoxelShape chain_rungs_x = VoxelShapes.or(
				Block.makeCuboidShape(1, 4, 7.5, 15, 5, 8.5),
				Block.makeCuboidShape(1, 8, 7.5, 15, 9, 8.5),
				Block.makeCuboidShape(1, 12, 7.5, 15, 13, 8.5)
		);
		VoxelShape chain_rungs_z = VoxelShapes.or(
				Block.makeCuboidShape(7.5, 4, 1, 8.5, 5, 15),
				Block.makeCuboidShape(7.5, 8, 1, 8.5, 9, 15),
				Block.makeCuboidShape(7.5, 12, 1, 8.5, 13, 15)
		);

		SHAPE_CHAIN[Rungs.NONE.ordinal()] = chain_core;
		SHAPE_CHAIN_BOTTOM[Rungs.NONE.ordinal()] = chain_core_bottom;

		SHAPE_CHAIN[Rungs.X.ordinal()] = VoxelShapes.or(chain_core, chain_rungs_x,
				Block.makeCuboidShape(1, 0, 7.5, 15, 1, 8.5)
		);
		SHAPE_CHAIN_BOTTOM[Rungs.X.ordinal()] = VoxelShapes.or(chain_core_bottom, chain_rungs_x);

		SHAPE_CHAIN[Rungs.Z.ordinal()] = VoxelShapes.or(chain_core, chain_rungs_z,
				Block.makeCuboidShape(7.5, 0, 1, 8.5, 1, 15)
		);
		SHAPE_CHAIN_BOTTOM[Rungs.Z.ordinal()] = VoxelShapes.or(chain_core_bottom, chain_rungs_z);
	}

	@Nonnull
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (state.get(BOTTOM)) {
			return shape_bottom[state.get(RUNGS).ordinal()];
		}
		return shape[state.get(RUNGS).ordinal()];
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
