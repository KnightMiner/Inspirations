package knightminer.inspirations.utility.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
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

public class TorchLeverWallBlock extends WallTorchBlock {
  private static final BooleanProperty POWERED = BlockStateProperties.POWERED;
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

  public TorchLeverWallBlock(AbstractBlock.Properties props, IParticleData particles) {
    super(props, particles);
    registerDefaultState(defaultBlockState().setValue(POWERED, false));
  }

  @Override
  protected void createBlockStateDefinition(StateContainer.Builder<Block,BlockState> builder) {
    builder.add(POWERED, FACING);
  }


  @Override
  public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
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
  public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
    if (world.isClientSide) {
      return ActionResultType.SUCCESS;
    }

    // update state
    state = state.cycle(POWERED);
    world.setBlock(pos, state, 3);
    // play sound
    float pitch = state.getValue(POWERED) ? 0.6F : 0.5F;
    world.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, pitch);
    // notify update
    world.updateNeighborsAt(pos, this);
    world.updateNeighborsAt(pos.relative(state.getValue(FACING).getOpposite()), this);
    return ActionResultType.SUCCESS;
  }


  /**
   * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
   */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
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
  public int getSignal(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
    return state.getValue(POWERED) ? 15 : 0;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public int getDirectSignal(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
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
