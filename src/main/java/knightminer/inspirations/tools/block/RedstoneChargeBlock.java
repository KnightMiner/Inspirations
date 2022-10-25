package knightminer.inspirations.tools.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Random;

public class RedstoneChargeBlock extends Block {

  public static final BooleanProperty QUICK = BooleanProperty.create("quick");
  public static final DirectionProperty FACING = DirectionalBlock.FACING;

  public RedstoneChargeBlock() {
    super(Block.Properties.of(Material.DECORATION)
                          .strength(0)
                          .lightLevel((state) -> 2)
         );

    this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.DOWN).setValue(QUICK, false));
  }

  // These should be overwritten by everything.

  @Override
  public boolean isAir(BlockState state) {
    return true;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public boolean canBeReplaced(BlockState p_196253_1_, BlockPlaceContext p_196253_2_) {
    return true;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
    return PushReaction.DESTROY;
  }

  /* Blockstate */

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
    builder.add(FACING, QUICK);
  }

  /* Fading */

  @Override
  public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
    if (!world.isClientSide()) {
      world.updateNeighborsAt(pos.relative(state.getValue(FACING)), this);
      world.scheduleTick(pos, this, state.getValue(QUICK) ? 2 : 20);
    }
    super.setPlacedBy(world, pos, state, entity, stack);
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!isMoving && state.getBlock() != newState.getBlock()) {
      super.onRemove(state, worldIn, pos, newState, false);
      worldIn.updateNeighborsAt(pos, this);
      worldIn.updateNeighborsAt(pos.relative(state.getValue(FACING)), this);
    }
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
    if (!world.isClientSide) {
      world.removeBlock(pos, false);
      world.playSound(null, pos, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);
    }
  }


  /* Powering */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
    return 15;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public int getDirectSignal(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
    return state.getValue(FACING).getOpposite() == side ? 15 : 0;
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


  /* Bounds */
  private static final VoxelShape BOUNDS = Block.box(6, 6, 6, 10, 10, 10);

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
    return BOUNDS;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
    return Shapes.empty();
  }


  /* Properties */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
    return !world.getBlockState(pos).getFluidState().isEmpty();
  }

  @Deprecated
  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return canSurvive(defaultBlockState(), context.getLevel(), context.getClickedPos()) ? defaultBlockState() : Blocks.AIR.defaultBlockState();
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
    Direction facing = stateIn.getValue(FACING);

    int offX = facing.getStepX();
    int offY = facing.getStepY();
    int offZ = facing.getStepZ();

    double x = pos.getX() + 0.5;
    double y = pos.getY() + 0.5;
    double z = pos.getZ() + 0.5;
    for (double i = 0; i <= 0.25; i += 0.05) {
      worldIn.addParticle(DustParticleOptions.REDSTONE, x + offX * i, y + offY * i, z + offZ * i, 0, 0, 0);
    }
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public RenderShape getRenderShape(BlockState state) {
    return RenderShape.INVISIBLE;
  }
}
