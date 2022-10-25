package knightminer.inspirations.utility.block;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import java.util.Random;

public class TorchLeverBlock extends TorchBlock {
  private static final DirectionProperty SWING = DirectionProperty.create("swing", (dir) -> dir != Direction.DOWN);

  public TorchLeverBlock(BlockBehaviour.Properties props, ParticleOptions particles) {
    super(props, particles);
    registerDefaultState(defaultBlockState().setValue(SWING, Direction.UP));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
    builder.add(SWING);
  }

  private boolean isPowered(BlockState state) {
    return state.getValue(SWING) != Direction.UP;
  }

  @Override
  public void animateTick(BlockState state, Level world, BlockPos pos, Random rand) {
    Direction swing = state.getValue(SWING);
    double x = pos.getX() + 0.5D;
    double y = pos.getY() + 0.7D;
    double z = pos.getZ() + 0.5D;

    // particleData is the appropriate flame particle passed to the constructor.
    if (isPowered(state)) {
      int offsetX = swing.getStepX();
      int offsetZ = swing.getStepZ();
      world.addParticle(ParticleTypes.SMOKE, x + 0.23D * offsetX, y - 0.05D, z + 0.23D * offsetZ, 0.0D, 0.0D, 0.0D);
      world.addParticle(flameParticle, x + 0.23D * offsetX, y - 0.05D, z + 0.23D * offsetZ, 0.0D, 0.0D, 0.0D);
    } else {
      world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
      world.addParticle(flameParticle, x, y, z, 0.0D, 0.0D, 0.0D);
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

    float pitch;
    if (isPowered(state)) {
      state = state.setValue(SWING, Direction.UP);
      pitch = 0.5f;
    } else {
      state = state.setValue(SWING, player.getDirection());
      pitch = 0.6f;
    }

    world.setBlock(pos, state, 3);
    // play sound
    world.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, pitch);
    // notify update
    world.updateNeighborsAt(pos, this);
    world.updateNeighborsAt(pos.below(), this);

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
    if (state.getBlock() != newState.getBlock() && !isMoving && isPowered(state)) {
      world.updateNeighborsAt(pos, this);
      world.updateNeighborsAt(pos.below(), this);
    }
    super.onRemove(state, world, pos, newState, isMoving);
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
    return isPowered(state) ? 15 : 0;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public int getDirectSignal(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
    if (!isPowered(state)) {
      return 0;
    }
    return side == Direction.DOWN ? 15 : 0;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public boolean isSignalSource(BlockState state) {
    return true;
  }
}
