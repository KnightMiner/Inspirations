package knightminer.inspirations.building.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.block.HidableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nonnull;

public class PathBlock extends HidableBlock {

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
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return shape;
	}

	@Deprecated
	@Nonnull
	@Override
	public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, ISelectionContext context) {
		return collShape;
	}

	/* Solid surface below */

	@Deprecated
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
		return super.isValidPosition(state, world, pos) && this.canBlockStay(world, pos);
	}

	private boolean canBlockStay(IWorldReader world, BlockPos pos) {
		BlockPos down = pos.down();
		return Block.hasSolidSide(world.getBlockState(down), world, pos, Direction.UP);
	}

	@Deprecated
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean p_220069_6_) {
		if (!this.canBlockStay(world, pos)) {
			world.destroyBlock(pos, true);
		}
	}
}
