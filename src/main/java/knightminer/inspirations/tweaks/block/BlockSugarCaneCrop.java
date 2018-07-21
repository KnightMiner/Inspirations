package knightminer.inspirations.tweaks.block;

import knightminer.inspirations.tweaks.InspirationsTweaks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

public class BlockSugarCaneCrop extends BlockBlockCrop {

	private static final AxisAlignedBB[] BOUNDS = {
			new AxisAlignedBB(0.125, 0, 0.125, 0.875, 0.125, 0.875),
			new AxisAlignedBB(0.125, 0, 0.125, 0.875, 0.25,  0.875),
			new AxisAlignedBB(0.125, 0, 0.125, 0.875, 0.375, 0.875),
			new AxisAlignedBB(0.125, 0, 0.125, 0.875, 0.5,   0.875),
			new AxisAlignedBB(0.125, 0, 0.125, 0.875, 0.625, 0.875),
			new AxisAlignedBB(0.125, 0, 0.125, 0.875, 0.75,  0.875),
			new AxisAlignedBB(0.125, 0, 0.125, 0.875, 0.875, 0.875)
	};
	public BlockSugarCaneCrop() {
		super(Blocks.REEDS, EnumPlantType.Beach, BOUNDS);
		this.setUnlocalizedName("reeds");
	}

	@Override
	public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
		return Blocks.REEDS.canBlockStay(world, pos);
	}

	@Override
	public Item getSeed() {
		return InspirationsTweaks.sugarCaneSeeds;
	}
}
