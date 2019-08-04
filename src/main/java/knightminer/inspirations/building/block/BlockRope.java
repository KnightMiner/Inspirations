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

public class BlockRope extends HidableBlock implements IWaterLoggable {

	public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public BlockRope(Properties props) {
		super(props, Config.enableRope::get);
		this.setDefaultState(this.stateContainer.getBaseState()
				.with(BOTTOM, false)
				.with(WATERLOGGED, false)
		);
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(BOTTOM, WATERLOGGED);
	}

	@Nonnull
	public IFluidState getFluidState(BlockState state) {
	  return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = super.getStateForPlacement(context);
		BlockPos down = context.getPos().down();
		return state
			.with(BOTTOM, !canConnectTo(context.getWorld().getBlockState(down), context.getWorld(), down))
			.with(WATERLOGGED, context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER);
	}

	private boolean canConnectTo(BlockState state, IBlockReader world, BlockPos pos) {
		if(state.getBlock() == this) {
			return true;
		}
		return !state.isIn(BlockTags.LEAVES) && !VoxelShapes.compare(
				state.getCollisionShape(world, pos).project(Direction.UP), BOUNDS, IBooleanFunction.ONLY_SECOND
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
		return Block.hasSolidSide(state, world, up, Direction.DOWN) || state.getBlock() == this;
	}

	/**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
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
		if(hit.getFace().getAxis().isVertical()) {
			return false;
		}

		ItemStack stack = player.getHeldItem(hand);
		// check if the item is the same type as us
		if(Block.getBlockFromItem(stack.getItem()) != this) {
			return false;
		}

		// find the first block at the bottom of the rope
		BlockPos next = pos.down();
		while(world.getBlockState(next).getBlock() == this) {
			next = next.down();
		}
		if(this.isValidPosition(state, world, next)) {
			BlockItem itemBlock = (BlockItem)stack.getItem();
			if(itemBlock.tryPlace(new DirectionalPlaceContext(world, next, hit.getFace(), stack, hit.getFace())) == ActionResultType.SUCCESS) {
				SoundType soundtype = this.getSoundType(state, world, next, player);
				if(player.isCreative()) {
					// Refund the item.
					stack.grow(1);
				}
			}
		}

		return true;
	}

	// when breaking, place all items from ropes below at the position of this rope
	@Override
	public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		// break all blocks below that are ropes
		BlockPos next = pos.down();
		int count = 0;
		// go down to the bottom
		while(world.getBlockState(next).getBlock() == this) {
			next = next.down();
			count++;
		}
		// then break them coming back up
		for(int i = 0; i < count; i++) {
			next = next.up();
			world.destroyBlock(next, false);
		}

		// then spawn their items up here
		ItemStack drops = new ItemStack(this, count);
		spawnAsEntity(world, pos, drops);
	}


	/* Block properties */

	@Override
	public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity) {
		return true;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}


	/* Bounds */

	protected static final VoxelShape BOUNDS = Block.makeCuboidShape(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
	protected static final VoxelShape BOUNDS_BOTTOM = Block.makeCuboidShape(6.0, 4.0, 6.0, 10.0, 16.0, 10.0);

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if(state.get(BOTTOM)) {
			return BOUNDS_BOTTOM;
		}
		return BOUNDS;
	}
}
