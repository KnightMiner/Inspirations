package knightminer.inspirations.utility.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Random;

public class TorchLeverWallBlock extends WallTorchBlock {
  private static final BooleanProperty POWERED = BlockStateProperties.POWERED;
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

  public TorchLeverWallBlock(BlockBehaviour.Properties props, ParticleOptions particles) {
    super(props, particles);
    registerDefaultState(defaultBlockState().setValue(POWERED, false));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
    builder.add(POWERED, FACING);
  }


  @Override
  public void animateTick(BlockState state, Level world, BlockPos pos, Random rand) {
    Direction facing = state.getValue(FACING);
    double x = pos.getX() + 0.5D;
    double y = pos.getY() + 0.7D;
    double z = pos.getZ() + 0.5D;

    Direction opposite = facing.getOpposite();
    int offsetX = opposite.getStepX();
    int offsetZ = opposite.getStepZ();
    // particleData is the appropriate flame particle passed to the constructor.
    if (state.getValue(POWERED)) {
      world.addParticle(ParticleTypes.SMOKE, x + 0.10D * offsetX, y + 0.08D, z + 0.10D * offsetZ, 0.0D, 0.0D, 0.0D);
      world.addParticle(flameParticle, x + 0.10D * offsetX, y + 0.08D, z + 0.10D * offsetZ, 0.0D, 0.0D, 0.0D);
    } else {
      world.addParticle(ParticleTypes.SMOKE, x + 0.27D * offsetX, y + 0.22D, z + 0.27D * offsetZ, 0.0D, 0.0D, 0.0D);
      world.addParticle(flameParticle, x + 0.27D * offsetX, y + 0.22D, z + 0.27D * offsetZ, 0.0D, 0.0D, 0.0D);
    }
  }

  /*
   * Powering
   */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
    if (world.isClientSide) {
      return InteractionResult.SUCCESS;
    }

    // update state
    state = state.cycle(POWERED);
    world.setBlock(pos, state, 3);
    // play sound
    float pitch = state.getValue(POWERED) ? 0.6F : 0.5F;
    world.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, pitch);
    // notify update
    world.updateNeighborsAt(pos, this);
    world.updateNeighborsAt(pos.relative(state.getValue(FACING).getOpposite()), this);
    return InteractionResult.SUCCESS;
  }


  /**
   * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
   */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
    // if powered, send updates for power
    if (state.getBlock() != newState.getBlock() && !isMoving && state.getValue(POWERED)) {
      world.updateNeighborsAt(pos, this);
      world.updateNeighborsAt(pos.relative(state.getValue(FACING).getOpposite()), this);
    }
    super.onRemove(state, world, pos, newState, isMoving);
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
    return state.getValue(POWERED) ? 15 : 0;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public int getDirectSignal(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
    if (!state.getValue(POWERED)) {
      return 0;
    }
    return state.getValue(FACING) == side ? 15 : 0;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public boolean isSignalSource(BlockState state) {
    return true;
  }
}
