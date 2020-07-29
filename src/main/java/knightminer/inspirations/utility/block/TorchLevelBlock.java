package knightminer.inspirations.utility.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.Random;

public class TorchLevelBlock extends TorchBlock {
	private static final DirectionProperty SWING = DirectionProperty.create("swing", (dir) -> dir != Direction.DOWN);

	public TorchLevelBlock() {
		super(Block.Properties
				.create(Material.MISCELLANEOUS)
				.doesNotBlockMovement()
				.hardnessAndResistance(0)
				.setLightLevel(state -> 14)
				.tickRandomly()
				.sound(SoundType.WOOD), ParticleTypes.FLAME
		);
		setDefaultState(getDefaultState().with(SWING, Direction.UP));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(SWING);
	}

	private boolean isPowered(BlockState state) {
		return state.get(SWING) != Direction.UP;
	}

	@Override
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		Direction swing = state.get(SWING);
		double x = pos.getX() + 0.5D;
		double y = pos.getY() + 0.7D;
		double z = pos.getZ() + 0.5D;

		if(isPowered(state)) {
			int offsetX = swing.getXOffset();
			int offsetZ = swing.getZOffset();
			world.addParticle(ParticleTypes.SMOKE, x + 0.23D * offsetX, y - 0.05D, z + 0.23D * offsetZ, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.FLAME, x + 0.23D * offsetX, y - 0.05D, z + 0.23D * offsetZ, 0.0D, 0.0D, 0.0D);
		} else {
			world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
		}
	}

	/*
	 * Powering
	 */

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
		if (world.isRemote) {
			return ActionResultType.SUCCESS;
		}

		float pitch;
		if (isPowered(state)) {
			state = state.with(SWING, Direction.UP);
			pitch = 0.5f;
		} else {
			state = state.with(SWING, player.getHorizontalFacing());
			pitch = 0.6f;
		}

		world.setBlockState(pos, state, 3);
		// play sound
		world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, pitch);
		// notify update
		world.notifyNeighborsOfStateChange(pos, this);
		world.notifyNeighborsOfStateChange(pos.down(), this);

		return ActionResultType.SUCCESS;
	}


	/**
	 * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
	 */

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		// if powered, send updates for power
		if (state.getBlock() != newState.getBlock() && !isMoving && isPowered(state)) {
			world.notifyNeighborsOfStateChange(pos, this);
			world.notifyNeighborsOfStateChange(pos.down(), this);
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return isPowered(state) ? 15 : 0;
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public int getStrongPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		if (!isPowered(state)) {
			return 0;
		}
		return side == Direction.DOWN ? 15 : 0;
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public boolean canProvidePower(BlockState state) {
		return true;
	}
}
