package knightminer.inspirations.building.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.block.HidableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class PathBlock extends HidableBlock implements IWaterLoggable {
  private final static BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

  private final VoxelShape shape;
  private final VoxelShape collShape;

  public PathBlock(VoxelShape shape, MaterialColor mapColor) {
    super(Block.Properties.create(Material.ROCK, mapColor)
                          .hardnessAndResistance(1.5F, 10F)
                          .harvestTool(ToolType.PICKAXE).harvestLevel(0),
          Config.enablePath::get
         );
    // Each path has a different shape, but use the bounding box for collisions.
    this.shape = shape;
    this.collShape = VoxelShapes.create(shape.getBoundingBox());
    setDefaultState(getDefaultState().with(WATERLOGGED, false));
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block,BlockState> builder) {
    builder.add(WATERLOGGED);
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    FluidState fluid = context.getWorld().getFluidState(context.getPos());
    return getDefaultState().with(WATERLOGGED, fluid.getFluid() == Fluids.WATER);
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public FluidState getFluidState(BlockState state) {
    return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
    if (state.get(WATERLOGGED)) {
      world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
    }
    return super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
  }

  /* Block Shape */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return shape;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
    return collShape;
  }

  /* Solid surface below */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
    return super.isValidPosition(state, world, pos) && this.canBlockStay(world, pos);
  }

  private boolean canBlockStay(IWorldReader world, BlockPos pos) {
    BlockPos down = pos.down();
    BlockState state = world.getBlockState(down);
    return Block.hasSolidSideOnTop(world, pos) || state.getBlock() instanceof MulchBlock;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void neighborChanged(BlockState state, World world, BlockPos pos, Block other, BlockPos fromPos, boolean isMoving) {
    if (!this.canBlockStay(world, pos)) {
      world.destroyBlock(pos, true);
    } else if (state.get(WATERLOGGED)) {
      world.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
    }
  }
}
