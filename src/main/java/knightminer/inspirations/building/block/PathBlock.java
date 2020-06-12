package knightminer.inspirations.building.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.block.HidableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
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

import javax.annotation.Nonnull;

public class PathBlock extends HidableBlock implements IWaterLoggable {
	private final VoxelShape shape;
	private final VoxelShape collShape;

	public static BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

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
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED);
	}

	public BlockState getStateForPlacement(BlockItemUseContext context) {
		IFluidState fluid = context.getWorld().getFluidState(context.getPos());
		return getDefaultState().with(WATERLOGGED, fluid.getFluid() == Fluids.WATER);
	}

	@Nonnull
	public IFluidState getFluidState(BlockState state) {
	  return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Nonnull
	public BlockState updatePostPlacement(BlockState state, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull IWorld world, @Nonnull BlockPos currentPos, @Nonnull BlockPos facingPos) {
		if (state.get(WATERLOGGED)) {
			world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		return super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
	}

	/* Block Shape */

	public static final VoxelShape SHAPE_ROUND = VoxelShapes.or(
		Block.makeCuboidShape(1, 0, 5, 15, 1, 11),
		Block.makeCuboidShape(5, 0, 1, 11, 1, 15),
		Block.makeCuboidShape(2, 0, 3, 14, 1, 13),
		Block.makeCuboidShape(3, 0, 2, 13, 1, 14)
	).simplify();
	public static final VoxelShape SHAPE_TILE = VoxelShapes.or(
		Block.makeCuboidShape(1, 0, 1, 7, 1, 7),
		Block.makeCuboidShape(9, 0, 1, 15, 1, 7),
		Block.makeCuboidShape(9, 0, 9, 15, 1, 15),
		Block.makeCuboidShape(1, 0, 9, 7, 1, 15)
	);
	public static final VoxelShape SHAPE_BRICK = VoxelShapes.or(
			Block.makeCuboidShape(0, 0, 0, 3, 1, 3),
			Block.makeCuboidShape(4, 0, 0, 7, 1, 7),
			Block.makeCuboidShape(0, 0, 4, 3, 1, 11),
			Block.makeCuboidShape(12, 0, 8, 15, 1, 15),
			Block.makeCuboidShape(8, 0, 0, 11, 1, 3),
			Block.makeCuboidShape(8, 0, 12, 11, 1, 16),
			Block.makeCuboidShape(12, 0, 0, 16, 1, 3),
			Block.makeCuboidShape(8, 0, 4, 15, 1, 7),
			Block.makeCuboidShape(4, 0, 8, 11, 1, 11),
			Block.makeCuboidShape(0, 0, 12, 7, 1, 15)
	);
	// There's multiple variants for these, just use a square.
	public static final VoxelShape SHAPE_ROCK = Block.makeCuboidShape(.5, 0, .5, 15.5, 1, 15.5);

	@Deprecated
	@Nonnull
	@Override
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
		return shape;
	}

	@Deprecated
	@Nonnull
	@Override
	public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
		return collShape;
	}

	/* Solid surface below */

	@Deprecated
	@Override
	public boolean isValidPosition(@Nonnull BlockState state, @Nonnull IWorldReader world, @Nonnull BlockPos pos) {
		return super.isValidPosition(state, world, pos) && this.canBlockStay(world, pos);
	}

	private boolean canBlockStay(IWorldReader world, BlockPos pos) {
		BlockPos down = pos.down();
		BlockState state = world.getBlockState(down);
		return Block.hasSolidSide(state, world, pos, Direction.UP) || state.getBlock() instanceof MulchBlock;
	}

	@Deprecated
	@Override
	public void neighborChanged(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block other, @Nonnull BlockPos fromPos, boolean isMoving) {
		if (!this.canBlockStay(world, pos)) {
			world.destroyBlock(pos, true);
		} else if (state.get(WATERLOGGED)) {
			world.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
	}
}
