package knightminer.inspirations.tweaks.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;

public abstract class BlockBlockCrop extends BlockCrops {

	private Block block;
	private EnumPlantType type;
	private final AxisAlignedBB[] bounds;
	public static final PropertyInteger SMALL_AGE = PropertyInteger.create("age", 0, 6);
	public BlockBlockCrop(Block block, EnumPlantType type, AxisAlignedBB[] bounds) {
		super();
		this.block = block;
		this.bounds = bounds;
		this.type = type;
	}

	/* Age logic */

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, getAgeProperty());
	}

	@Override
	protected PropertyInteger getAgeProperty() {
		return SMALL_AGE;
	}

	@Override
	public IBlockState withAge(int age) {
		if(age == getMaxAge()) {
			return block.getDefaultState();
		}
		return super.withAge(age);
	}

	@Override
	public boolean isMaxAge(IBlockState state) {
		// never get to max age, our max is the block
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return bounds[this.getAge(state)];
	}


	/* Crop drops */

	@Override
	public abstract Item getSeed();

	@Override
	protected Item getCrop() {
		return Item.getItemFromBlock(block);
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
		return type;
	}
}
