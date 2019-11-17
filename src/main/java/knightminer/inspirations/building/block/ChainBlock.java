package knightminer.inspirations.building.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;

public class ChainBlock extends RopeBlock {
	public ChainBlock(Item rungsItem, Properties props) {
		super(rungsItem, props);
	}

	/* Bounds */

	// Override shape for this version.
	public static final VoxelShape[] SHAPE = new VoxelShape[3];
	public static final VoxelShape[] SHAPE_BOTTOM = new VoxelShape[3];

	static {
		VoxelShape chain_core = VoxelShapes.or(
				Block.makeCuboidShape(6.5, 0, 7.5, 9.5, 16, 8.5),
				Block.makeCuboidShape(7.5, 0, 6.5, 8.5, 16, 9.5)
		);
		VoxelShape chain_core_bottom = VoxelShapes.or(
				Block.makeCuboidShape(6.5, 4, 7.5, 9.5, 16, 8.5),
				Block.makeCuboidShape(7.5, 4, 6.5, 8.5, 16, 9.5)
		);

		VoxelShape chain_rungs_x = VoxelShapes.or(
				Block.makeCuboidShape(1, 4, 7.5, 15, 5, 8.5),
				Block.makeCuboidShape(1, 8, 7.5, 15, 9, 8.5),
				Block.makeCuboidShape(1, 12, 7.5, 15, 13, 8.5)
		);
		VoxelShape chain_rungs_z = VoxelShapes.or(
				Block.makeCuboidShape(7.5, 4, 1, 8.5, 5, 15),
				Block.makeCuboidShape(7.5, 8, 1, 8.5, 9, 15),
				Block.makeCuboidShape(7.5, 12, 1, 8.5, 13, 15)
		);

		SHAPE[Rungs.NONE.ordinal()] = chain_core;
		SHAPE_BOTTOM[Rungs.NONE.ordinal()] = chain_core_bottom;

		SHAPE[Rungs.X.ordinal()] = VoxelShapes.or(chain_core, chain_rungs_x,
				Block.makeCuboidShape(1, 0, 7.5, 15, 1, 8.5)
		);
		SHAPE_BOTTOM[Rungs.X.ordinal()] = VoxelShapes.or(chain_core_bottom, chain_rungs_x);

		SHAPE[Rungs.Z.ordinal()] = VoxelShapes.or(chain_core, chain_rungs_z,
				Block.makeCuboidShape(7.5, 0, 1, 8.5, 1, 15)
		);
		SHAPE_BOTTOM[Rungs.Z.ordinal()] = VoxelShapes.or(chain_core_bottom, chain_rungs_z);
	}

	@Nonnull
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return (state.get(BOTTOM) ? SHAPE_BOTTOM: SHAPE)[state.get(RUNGS).ordinal()];
	}
}
