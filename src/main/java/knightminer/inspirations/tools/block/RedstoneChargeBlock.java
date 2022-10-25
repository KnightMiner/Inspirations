package knightminer.inspirations.tools.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
  public boolean isAir(BlockState state, IBlockReader world, BlockPos pos) {
    return true;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public boolean canBeReplaced(BlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
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
  protected void createBlockStateDefinition(StateContainer.Builder<Block,BlockState> builder) {
    builder.add(FACING, QUICK);
  }

  /* Fading */

  @Override
  public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
    if (!world.isClientSide()) {
      world.updateNeighborsAt(pos.relative(state.getValue(FACING)), this);
      world.getBlockTicks().scheduleTick(pos, this, state.getValue(QUICK) ? 2 : 20);
    }
    super.setPlacedBy(world, pos, state, entity, stack);
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!isMoving && state.getBlock() != newState.getBlock()) {
      super.onRemove(state, worldIn, pos, newState, false);
      worldIn.updateNeighborsAt(pos, this);
      worldIn.updateNeighborsAt(pos.relative(state.getValue(FACING)), this);
    }
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    if (!world.isClientSide) {
      world.removeBlock(pos, false);
      world.playSound(null, pos, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);
    }
  }


  /* Powering */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public int getSignal(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
    return 15;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public int getDirectSignal(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
    return state.getValue(FACING).getOpposite() == side ? 15 : 0;
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


  /* Bounds */
  private static final VoxelShape BOUNDS = Block.box(6, 6, 6, 10, 10, 10);

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
    return BOUNDS;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
    return VoxelShapes.empty();
  }


  /* Properties */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
    return !world.getBlockState(pos).getFluidState().isEmpty();
  }

  @Deprecated
  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return canSurvive(defaultBlockState(), context.getLevel(), context.getClickedPos()) ? defaultBlockState() : Blocks.AIR.defaultBlockState();
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
    Direction facing = stateIn.getValue(FACING);

    int offX = facing.getStepX();
    int offY = facing.getStepY();
    int offZ = facing.getStepZ();

    double x = pos.getX() + 0.5;
    double y = pos.getY() + 0.5;
    double z = pos.getZ() + 0.5;
    for (double i = 0; i <= 0.25; i += 0.05) {
      worldIn.addParticle(RedstoneParticleData.REDSTONE, x + offX * i, y + offY * i, z + offZ * i, 0, 0, 0);
    }
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public BlockRenderType getRenderShape(BlockState state) {
    return BlockRenderType.INVISIBLE;
  }
}
