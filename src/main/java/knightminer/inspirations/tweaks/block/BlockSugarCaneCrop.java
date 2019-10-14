package knightminer.inspirations.tweaks.block;

import knightminer.inspirations.tweaks.InspirationsTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.PlantType;

import javax.annotation.Nonnull;

public class BlockSugarCaneCrop extends BlockBlockCrop {

	private static final VoxelShape[] BOUNDS = {
			VoxelShapes.create(0.125, 0, 0.125, 0.875, 0.125, 0.875),
			VoxelShapes.create(0.125, 0, 0.125, 0.875, 0.25,  0.875),
			VoxelShapes.create(0.125, 0, 0.125, 0.875, 0.375, 0.875),
			VoxelShapes.create(0.125, 0, 0.125, 0.875, 0.5,   0.875),
			VoxelShapes.create(0.125, 0, 0.125, 0.875, 0.625, 0.875),
			VoxelShapes.create(0.125, 0, 0.125, 0.875, 0.75,  0.875),
			VoxelShapes.create(0.125, 0, 0.125, 0.875, 0.875, 0.875)
	};
	public BlockSugarCaneCrop() {
		super(Blocks.SUGAR_CANE, PlantType.Beach, BOUNDS, Block.Properties.from(Blocks.SUGAR_CANE));
	}

	@Nonnull
	@Override
	public String getTranslationKey() {
		return "item.inspirations.sugar_cane_seeds";
	}

	@Override
	public boolean isValidPosition(@Nonnull BlockState state, IWorldReader world, @Nonnull BlockPos pos) {
		return Blocks.SUGAR_CANE.isValidPosition(Blocks.SUGAR_CANE.getDefaultState(), world, pos);
	}

	@Nonnull
	@Override
	protected IItemProvider getSeedsItem() {
		return InspirationsTweaks.sugarCaneSeeds;
	}
}
