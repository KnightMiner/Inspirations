package knightminer.inspirations.utility.block;

import com.google.common.collect.ImmutableMap;
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

import javax.annotation.Nullable;
import java.util.Random;

public class BricksButtonBlock extends HidableBlock {

  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  private static final BooleanProperty POWERED = AbstractButtonBlock.POWERED;
  private final ImmutableMap<Direction,AxisAlignedBB> buttonBounds;

  public BricksButtonBlock(ImmutableMap<Direction,AxisAlignedBB> buttonBounds) {
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
  protected void createBlockStateDefinition(StateContainer.Builder<Block,BlockState> builder) {
    builder.add(FACING, POWERED);
    super.createBlockStateDefinition(builder);
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
  }

  @Override
  public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity player, ItemStack stack) {
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
  public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
    // if you did not click the secret button, no button for you
    if (!getButtonBox(state).contains(trace.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ()))) {
      return ActionResultType.PASS;
    }

    // if already powered, we done here
    if (state.getValue(POWERED)) {
      return ActionResultType.SUCCESS;
    }

    world.setBlock(pos, state.setValue(POWERED, true), 3);
    world.playSound(player, pos, SoundEvents.STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
    world.updateNeighborsAt(pos, this);
    world.getBlockTicks().scheduleTick(pos, this, 20);
    return ActionResultType.SUCCESS;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    if (world.isClientSide) {
      return;
    }
    if ((state.getValue(POWERED))) {
      world.setBlockAndUpdate(pos, state.setValue(POWERED, false));
      world.updateNeighborsAt(pos, this);
      world.playSound(null, pos, SoundEvents.STONE_BUTTON_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
    }
  }

  public static final ImmutableMap<Direction,AxisAlignedBB> BRICK_BUTTON;
  public static final ImmutableMap<Direction,AxisAlignedBB> NETHER_BUTTON;

  static {
    ImmutableMap.Builder<Direction,AxisAlignedBB> bounds = ImmutableMap.builder();
    bounds.put(Direction.NORTH, new AxisAlignedBB(0.3125, 0.3125, 0, 0.75, 0.5, 0.0625));
    bounds.put(Direction.SOUTH, new AxisAlignedBB(0.25, 0.3125, 0.9375, 0.6875, 0.5, 1.0125));
    bounds.put(Direction.WEST, new AxisAlignedBB(0, 0.3125, 0.25, 0.0625, 0.5, 0.6875));
    bounds.put(Direction.EAST, new AxisAlignedBB(0.9375, 0.3125, 0.3125, 1.0125, 0.5, 0.75));
    BRICK_BUTTON = bounds.build();

    bounds = ImmutableMap.builder();
    bounds.put(Direction.NORTH, new AxisAlignedBB(0.5, 0.5, 0, 0.9375, 0.6875, 0.0625));
    bounds.put(Direction.SOUTH, new AxisAlignedBB(0.0625, 0.5, 0.9375, 0.5, 0.6875, 1.0125));
    bounds.put(Direction.WEST, new AxisAlignedBB(0, 0.5, 0.0625, 0.0625, 0.6875, 0.5));
    bounds.put(Direction.EAST, new AxisAlignedBB(0.9375, 0.5, 0.5, 1.0125, 0.6875, 0.9375));
    NETHER_BUTTON = bounds.build();
  }

  private AxisAlignedBB getButtonBox(BlockState state) {
    return buttonBounds.get(state.getValue(FACING));
  }


  /* Redstone logic */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
    if (state.getBlock() != newState.getBlock() && state.getValue(POWERED)) {
      world.updateNeighborsAt(pos, this);
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
  public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
    return false;
  }
}
