package knightminer.inspirations.building.block;

import java.util.Locale;

import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.ToolType;

public class BlockMulch extends FallingBlock {

	protected static final AxisAlignedBB BOUNDS = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.9375D, 1.0D);

	public BlockMulch(MulchColor color) {
		super(Properties.create(Material.WOOD)
			.harvestTool(ToolType.SHOVEL)
			.sound(SoundType.WET_GRASS)
			.hardnessAndResistance(0.6F)
		);
	}

	/*
	 * Plants
	 */

@Override
	public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction direction, net.minecraftforge.common.IPlantable plantable) {
		// we are fine with most plants, but saplings are a bit much
		// this is mostly cop out since I have no way of stopping sapling growth
		return plantable.getPlantType(world, pos.offset(direction)) == PlantType.Plains && !(plantable instanceof SaplingBlock);
	}


	public static enum MulchColor implements IStringSerializable {
		PLAIN,
		BROWN,
		YELLOW,
		AMBER,
		RUBY,
		RED,
		BLACK,
		BLUE;

		@Override
		public String getName() {
			return this.name().toLowerCase(Locale.US);
		}
	}
}
