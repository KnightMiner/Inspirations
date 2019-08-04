package knightminer.inspirations.tweaks.block;

import javax.annotation.Nonnull;

import net.minecraft.block.*;
import net.minecraft.item.DyeColor;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class BlockFittedCarpet extends BlockFlatCarpet {
	public BlockFittedCarpet(DyeColor color, Block original) {
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

	@Nonnull
	@Override
	public VoxelShape getCollisionShape(BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, ISelectionContext context) {
		// if any of the parts are lowered, no collision box
		if(state.get(NORTHWEST) || state.get(NORTHEAST) || state.get(SOUTHWEST) || state.get(SOUTHEAST)) {
			return VoxelShapes.empty();
		}
		return super.getCollisionShape(state, world, pos, context);
	}
}
