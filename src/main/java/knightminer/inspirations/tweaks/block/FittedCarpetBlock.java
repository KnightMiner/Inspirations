package knightminer.inspirations.tweaks.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.DyeColor;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;

public class FittedCarpetBlock extends FlatCarpetBlock {
	public FittedCarpetBlock(DyeColor color, Block original) {
		super(color, original);
		this.setDefaultState(this.getStateContainer().getBaseState()
			.with(NORTHWEST, false)
			.with(NORTHEAST, false)
			.with(SOUTHWEST, false)
			.with(SOUTHEAST, false)
		);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(NORTHWEST, NORTHEAST, SOUTHWEST, SOUTHEAST);
	}


	private static final VoxelShape[] BOUNDS = new VoxelShape[16];
	private static final VoxelShape[] COLLISION = new VoxelShape[16];
	static {
		// Compute all the different possible shapes.
		int HIGH = 0;
		int LOW = -8;
		for(int i = 0; i < 16; i++) {
			boolean NW = (i & 0b1000) == 0; // -X -Z
			boolean NE = (i & 0b0100) == 0; // +X -Z
			boolean SW = (i & 0b0010) == 0; // -X +Z
			boolean SE = (i & 0b0001) == 0; // +X +Z

			if (!NW && !NE && !SW && !SE) {
				// Fully lowered, bit of a special case.
				BOUNDS[i] = VoxelShapes.or(
						makeCuboidShape(0, -7, 0, 17, -8, 17),
						makeCuboidShape(-1, -16, -1, 0, -7, 17),
						makeCuboidShape(-1, -16, -1, 16, -7, 0),
						makeCuboidShape(16, -16, -1, 17, -7, 17),
						makeCuboidShape(-1, -16, 16, 16, -7, 17)
				);
				COLLISION[i] = makeCuboidShape(0, -7, 0, 17, -8, 17);
				continue;
			}

			// First each flat segment, high or low.
			VoxelShape shape = VoxelShapes.or(
					makeCuboidShape(0, NW ? HIGH : LOW, 0,  8, (NW ? HIGH : LOW) + 1,  8),
					makeCuboidShape(8, NE ? HIGH : LOW, 0, 16, (NE ? HIGH : LOW) + 1,  8),
					makeCuboidShape(0, SW ? HIGH : LOW, 8,  8, (SW ? HIGH : LOW) + 1, 16),
					makeCuboidShape(8, SE ? HIGH : LOW, 8, 16, (SE ? HIGH : LOW) + 1, 16)
			);

			// Only provide collision within our own block, any further down messes up the player's movement.
			COLLISION[i] = shape;

			// Add the lowermost shapes around the base.
			if (!NE & !NW) {
				shape = VoxelShapes.or(shape, makeCuboidShape(0, -16, -1, 16, -7, 0));
			}
			if (!SE && !SW) {
				shape = VoxelShapes.or(shape, makeCuboidShape(0, -16, 16, 16, -7, 17));
			}
			if (!NW && !SW) {
				shape = VoxelShapes.or(shape, makeCuboidShape(-1, -16, 0, 0, -7, 16));
			}
			if (!NE && !SE) {
				shape = VoxelShapes.or(shape, makeCuboidShape(16, -16, 0, 17, -7, 16));
			}

			// Then, for each of the spaces between generate verticals if they're different.
			if (NW && !NE) {
				shape = VoxelShapes.or(shape, makeCuboidShape(8, LOW, 0, 9, 1, 8));
			}
			if (!NW && NE) {
				shape = VoxelShapes.or(shape, makeCuboidShape(7, LOW, 0, 8, 1, 8));
			}

			if (SW && !SE) {
				shape = VoxelShapes.or(shape, makeCuboidShape(8, LOW, 8, 9, 1, 16));
			}
			if (!SW && SE) {
				shape = VoxelShapes.or(shape, makeCuboidShape(7, LOW, 8, 8, 1, 16));
			}

			if (NW && !SW) {
				shape = VoxelShapes.or(shape, makeCuboidShape(0, LOW, 8, 8, 1, 9));
			}
			if (!NW && SW) {
				shape = VoxelShapes.or(shape, makeCuboidShape(0, LOW, 7, 8, 1, 8));
			}

			if (NE && !SE) {
				shape = VoxelShapes.or(shape, makeCuboidShape(8, LOW, 8, 16, 1, 9));
			}
			if (!NE && SE) {
				shape = VoxelShapes.or(shape, makeCuboidShape(8, LOW, 7, 16, 1, 8));
			}

			// Last, a the missing 1x1 post for both heights in outer corners.
			if (!NW & !SE && !SW) { // NE
				shape = VoxelShapes.or(shape,
						makeCuboidShape(7, -8, 8, 8, 1, 9),
						makeCuboidShape(-1, -16, 16, 0, -7, 17)
				);
			}
			if (!NE & !SE && !SW) { // NW
				shape = VoxelShapes.or(shape,
						makeCuboidShape(8, -8, 8, 9, 1, 9),
						makeCuboidShape(16, -16, 16, 17, -7, 17)
				);
			}
			if (!NE && !NW && !SW) { // SE
				shape = VoxelShapes.or(shape,
						makeCuboidShape(7, -8, 7, 8, 1, 8),
						makeCuboidShape(-1, -16, -1, 0, -7, 0)
				);
			}
			if (!NE && !NW & !SE) { // SW
				shape = VoxelShapes.or(shape,
						makeCuboidShape(8, -8, 7, 9, 1, 8),
						makeCuboidShape(16, -16, -1, 17, -7, 0)
				);
			}
			BOUNDS[i] = shape;
		}
	}


	@Nonnull
	@Override
	public VoxelShape getShape(BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, ISelectionContext context) {
		return BOUNDS[
				(state.get(NORTHWEST) ? 8 : 0) |
				(state.get(NORTHEAST) ? 4 : 0) |
				(state.get(SOUTHWEST) ? 2 : 0) |
				(state.get(SOUTHEAST) ? 1 : 0)
				];
	}

	@Deprecated
	@Nonnull
	@Override
	public VoxelShape getCollisionShape(BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, ISelectionContext context) {
		return COLLISION[
				(state.get(NORTHWEST) ? 8 : 0) |
				(state.get(NORTHEAST) ? 4 : 0) |
				(state.get(SOUTHWEST) ? 2 : 0) |
				(state.get(SOUTHEAST) ? 1 : 0)
				];
	}
}
