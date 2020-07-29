package knightminer.inspirations.tweaks.block;

import knightminer.inspirations.tweaks.InspirationsTweaks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.PlantType;

public class SugarCaneCropBlock extends BlockCropBlock {
	private static final VoxelShape[] BOUNDS = {
			makeCuboidShape(2, 0, 2, 14,  2, 14),
			makeCuboidShape(2, 0, 2, 14,  4, 14),
			makeCuboidShape(2, 0, 2, 14,  6, 14),
			makeCuboidShape(2, 0, 2, 14,  8, 14),
			makeCuboidShape(2, 0, 2, 14, 10, 14),
			makeCuboidShape(2, 0, 2, 14, 12, 14),
			makeCuboidShape(2, 0, 2, 14, 14, 14)
	};

	public SugarCaneCropBlock() {
		super(Blocks.SUGAR_CANE, PlantType.BEACH);
	}

	@Override
	protected IItemProvider getSeedsItem() {
		return InspirationsTweaks.cactusSeeds;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return BOUNDS[this.getAge(state)];
	}
}
