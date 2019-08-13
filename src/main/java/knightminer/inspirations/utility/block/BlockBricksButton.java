package knightminer.inspirations.utility.block;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.block.HidableBlock;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockBricksButton extends HidableBlock {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty POWERED = AbstractButtonBlock.POWERED;
	private final ImmutableMap<Direction, AxisAlignedBB> buttonBounds;

	public BlockBricksButton(ImmutableMap<Direction, AxisAlignedBB> buttonBounds) {
		super(Block.Properties
				.create(Material.ROCK)
				.hardnessAndResistance(1.5F, 10.0F)
				.sound(SoundType.STONE)
				.tickRandomly(),
				Config.enableBricksButton::get
		);
		this.buttonBounds = buttonBounds;

		this.setDefaultState(this.getStateContainer().getBaseState()
				.with(FACING, Direction.NORTH)
				.with(POWERED, false));
	}


	/* Blockstate */

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED);
		super.fillStateContainer(builder);
	}

	@Override
	public BlockState getStateForPlacement(BlockState state, Direction facing, BlockState neighState, IWorld world, BlockPos pos, BlockPos facingPos, Hand hand) {
		return state.with(FACING, facing.getOpposite());
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity player, ItemStack stack) {
		if (player != null) {
			world.setBlockState(pos, state.with(FACING, player.getAdjustedHorizontalFacing().getOpposite()));
		}
		super.onBlockPlacedBy(world, pos, state, player, stack);
	}

	/**
	 * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
	 * blockstate.
	 */
	@Nonnull
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	/**
	 * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
	 * blockstate.
	 */
	@Nonnull
	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.with(FACING, mirror.mirror(state.get(FACING)));
	}

	/* Pressing the button */

	/**
	 * How many world ticks before ticking
	 */
	@Override
	public int tickRate(IWorldReader p_149738_1_) {
		return 20;
	}

	/**
	 * Called when the block is right clicked by a player.
	 */
	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
		// if you did not click the secret button, no button for you
		if(!getButtonBox(state).contains(trace.getHitVec().subtract(new Vec3d(pos)))) {
			return false;
		}

		// if already powered, we done here
		if (state.get(POWERED)) {
			return true;
		}

		world.setBlockState(pos, state.with(POWERED, true), 3);
		world.playSound(player, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
		world.notifyNeighborsOfStateChange(pos, this);
		world.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(world));
		return true;
	}


	@Override
	public void randomTick(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
	}

	@Override
	public void tick(BlockState state, World world, BlockPos pos, Random random) {
		if (world.isRemote) {
			return;
		}
		if ((state.get(POWERED))) {
			world.setBlockState(pos, state.with(POWERED, false));
			world.notifyNeighborsOfStateChange(pos, this);
			world.playSound(null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
		}
	}

	public static final ImmutableMap<Direction, AxisAlignedBB> BRICK_BUTTON;
	public static final ImmutableMap<Direction, AxisAlignedBB> NETHER_BUTTON;
	static {
		ImmutableMap.Builder<Direction, AxisAlignedBB> bounds = ImmutableMap.builder();
		bounds.put(Direction.NORTH, new AxisAlignedBB(0.3125, 0.3125, 0,      0.75,   0.5, 0.0625));
		bounds.put(Direction.SOUTH, new AxisAlignedBB(0.25,   0.3125, 0.9375, 0.6875, 0.5, 1.0125));
		bounds.put(Direction.WEST,  new AxisAlignedBB(0,      0.3125, 0.25,   0.0625, 0.5, 0.6875));
		bounds.put(Direction.EAST,  new AxisAlignedBB(0.9375, 0.3125, 0.3125, 1.0125, 0.5, 0.75  ));
		BRICK_BUTTON = bounds.build();

		bounds = ImmutableMap.builder();
		bounds.put(Direction.NORTH, new AxisAlignedBB(0.5,    0.5, 0,      0.9375, 0.6875, 0.0625));
		bounds.put(Direction.SOUTH, new AxisAlignedBB(0.0625, 0.5, 0.9375, 0.5,    0.6875, 1.0125));
		bounds.put(Direction.WEST,  new AxisAlignedBB(0,      0.5, 0.0625, 0.0625, 0.6875, 0.5));
		bounds.put(Direction.EAST,  new AxisAlignedBB(0.9375, 0.5, 0.5,    1.0125, 0.6875, 0.9375));
		NETHER_BUTTON = bounds.build();
	}

	private AxisAlignedBB getButtonBox(BlockState state) {
		return buttonBounds.get(state.get(FACING));
	}


	/* Redstone logic */


	/**
	 * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
	 */

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock() && state.get(POWERED)) {
			world.notifyNeighborsOfStateChange(pos, this);
		}

		super.onReplaced(state, world, pos, newState, isMoving);
	}

	@Override
	public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return state.get(POWERED) ? 15 : 0;
	}

	@Override
	public int getStrongPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		// we may be a button, but we act as though ourself is the block that is powered
		return 0;
	}

	/**
	 * Can this block provide power. Only wire currently seems to have this change based on its state.
	 */

	@Override
	public boolean canProvidePower(BlockState state) {
		return true;
	}

	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
		return false;
	}

}
