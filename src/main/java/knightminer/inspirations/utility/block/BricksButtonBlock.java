package knightminer.inspirations.utility.block;

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
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BricksButtonBlock extends HidableBlock {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty POWERED = AbstractButtonBlock.POWERED;
	private final ImmutableMap<Direction, AxisAlignedBB> buttonBounds;

	public BricksButtonBlock(ImmutableMap<Direction, AxisAlignedBB> buttonBounds) {
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
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
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
	@Deprecated
	@Nonnull
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	/**
	 * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
	 * blockstate.
	 */
	@Deprecated
	@Nonnull
	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.with(FACING, mirror.mirror(state.get(FACING)));
	}

	/* Pressing the button */

	@Deprecated
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
		// if you did not click the secret button, no button for you
		if(!getButtonBox(state).contains(trace.getHitVec().subtract(pos.getX(), pos.getY(), pos.getZ()))) {
			return ActionResultType.PASS;
		}

		// if already powered, we done here
		if (state.get(POWERED)) {
			return ActionResultType.SUCCESS;
		}

		world.setBlockState(pos, state.with(POWERED, true), 3);
		world.playSound(player, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
		world.notifyNeighborsOfStateChange(pos, this);
		world.getPendingBlockTicks().scheduleTick(pos, this, 20);
		return ActionResultType.SUCCESS;
	}

	@Deprecated
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {}

	@Deprecated
	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
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

	@Deprecated
	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock() && state.get(POWERED)) {
			world.notifyNeighborsOfStateChange(pos, this);
		}

		super.onReplaced(state, world, pos, newState, isMoving);
	}

	@Deprecated
	@Override
	public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return state.get(POWERED) ? 15 : 0;
	}

	@Deprecated
	@Override
	public int getStrongPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		// we may be a button, but we act as though ourself is the block that is powered
		return 0;
	}

	@Deprecated
	@Override
	public boolean canProvidePower(BlockState state) {
		return true;
	}

	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
		return false;
	}
}
