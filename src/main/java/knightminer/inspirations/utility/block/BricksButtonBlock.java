package knightminer.inspirations.utility.block;

import com.google.common.collect.ImmutableMap;
import knightminer.inspirations.common.block.HidableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.Random;

public class BricksButtonBlock extends HidableBlock {

  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  private static final BooleanProperty POWERED = ButtonBlock.POWERED;
  private final ImmutableMap<Direction,AABB> buttonBounds;

  public BricksButtonBlock(ImmutableMap<Direction,AABB> buttonBounds) {
    super(Block.Properties
              .of(Material.STONE)
              .strength(1.5F, 10.0F)
              .sound(SoundType.STONE)
              .randomTicks(),
          () -> false
         );
    this.buttonBounds = buttonBounds;

    this.registerDefaultState(this.getStateDefinition().any()
                             .setValue(FACING, Direction.NORTH)
                             .setValue(POWERED, false));
  }


  /* Blockstate */

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
    builder.add(FACING, POWERED);
    super.createBlockStateDefinition(builder);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
  }

  @Override
  public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity player, ItemStack stack) {
    if (player != null) {
      world.setBlockAndUpdate(pos, state.setValue(FACING, player.getMotionDirection().getOpposite()));
    }
    super.setPlacedBy(world, pos, state, player, stack);
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public BlockState rotate(BlockState state, Rotation rot) {
    return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public BlockState mirror(BlockState state, Mirror mirror) {
    return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
  }

  /* Pressing the button */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
    // if you did not click the secret button, no button for you
    if (!getButtonBox(state).contains(trace.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ()))) {
      return InteractionResult.PASS;
    }

    // if already powered, we done here
    if (state.getValue(POWERED)) {
      return InteractionResult.SUCCESS;
    }

    world.setBlock(pos, state.setValue(POWERED, true), 3);
    world.playSound(player, pos, SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.6F);
    world.updateNeighborsAt(pos, this);
    world.scheduleTick(pos, this, 20);
    return InteractionResult.SUCCESS;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
    if (world.isClientSide) {
      return;
    }
    if ((state.getValue(POWERED))) {
      world.setBlockAndUpdate(pos, state.setValue(POWERED, false));
      world.updateNeighborsAt(pos, this);
      world.playSound(null, pos, SoundEvents.STONE_BUTTON_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 0.5F);
    }
  }

  public static final ImmutableMap<Direction,AABB> BRICK_BUTTON;
  public static final ImmutableMap<Direction,AABB> NETHER_BUTTON;

  static {
    ImmutableMap.Builder<Direction,AABB> bounds = ImmutableMap.builder();
    bounds.put(Direction.NORTH, new AABB(0.3125, 0.3125, 0, 0.75, 0.5, 0.0625));
    bounds.put(Direction.SOUTH, new AABB(0.25, 0.3125, 0.9375, 0.6875, 0.5, 1.0125));
    bounds.put(Direction.WEST, new AABB(0, 0.3125, 0.25, 0.0625, 0.5, 0.6875));
    bounds.put(Direction.EAST, new AABB(0.9375, 0.3125, 0.3125, 1.0125, 0.5, 0.75));
    BRICK_BUTTON = bounds.build();

    bounds = ImmutableMap.builder();
    bounds.put(Direction.NORTH, new AABB(0.5, 0.5, 0, 0.9375, 0.6875, 0.0625));
    bounds.put(Direction.SOUTH, new AABB(0.0625, 0.5, 0.9375, 0.5, 0.6875, 1.0125));
    bounds.put(Direction.WEST, new AABB(0, 0.5, 0.0625, 0.0625, 0.6875, 0.5));
    bounds.put(Direction.EAST, new AABB(0.9375, 0.5, 0.5, 1.0125, 0.6875, 0.9375));
    NETHER_BUTTON = bounds.build();
  }

  private AABB getButtonBox(BlockState state) {
    return buttonBounds.get(state.getValue(FACING));
  }


  /* Redstone logic */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
    if (state.getBlock() != newState.getBlock() && state.getValue(POWERED)) {
      world.updateNeighborsAt(pos, this);
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
    // we may be a button, but we act as though ourself is the block that is powered
    return 0;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public boolean isSignalSource(BlockState state) {
    return true;
  }

  @Override
  public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side) {
    return false;
  }
}
