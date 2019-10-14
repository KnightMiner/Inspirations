package knightminer.inspirations.building.block;

import java.util.Locale;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nonnull;

public class BlockMulch extends FallingBlock implements IHidable {

	protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);

	public BlockMulch(MaterialColor color) {
		super(Properties.create(Material.WOOD, color)
				.harvestTool(ToolType.SHOVEL)
				.sound(SoundType.WET_GRASS)
				.hardnessAndResistance(0.6F)
		);
	}

	@Override
	public boolean isEnabled() {
		return Config.enableMulch.get();
	}

	@Override
	public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
		if(shouldAddtoItemGroup(group)) {
			super.fillItemGroup(group, items);
		}
	}

	@Nonnull
	@Override
	public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	/*
	 * Plants
	 */
	@Override
	public boolean canSustainPlant(@Nonnull BlockState state, @Nonnull IBlockReader world, BlockPos pos, @Nonnull Direction direction, net.minecraftforge.common.IPlantable plantable) {
		// we are fine with most plants, but saplings are a bit much
		// this is mostly cop out since I have no way of stopping sapling growth
		return plantable.getPlantType(world, pos.offset(direction)) == PlantType.Plains && !(plantable instanceof SaplingBlock);
	}
}
