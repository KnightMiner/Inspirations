package knightminer.inspirations.building.block;

import java.util.Locale;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMulch extends BlockFalling {

	protected static final AxisAlignedBB BOUNDS = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.9375D, 1.0D);

	public static final PropertyEnum<MulchColor> COLOR = PropertyEnum.create("color", MulchColor.class);

	public BlockMulch() {
		super(Material.WOOD);

		this.setHarvestLevel("shovel", -1);
		this.setSoundType(SoundType.GROUND);
		this.setHardness(0.6f);
	}

	/*
	 * Types
	 */

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, COLOR);
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState()
				.withProperty(COLOR, MulchColor.fromMeta(meta));
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(COLOR).getMeta();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for(MulchColor type : MulchColor.values()) {
			list.add(new ItemStack(this, 1, type.getMeta()));
		}
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}


	/*
	 * Plants
	 */

	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, net.minecraftforge.common.IPlantable plantable) {
		// we are fine with most plants, but saplings are a bit much
		// this is mostly cop out since I have no way of stopping sapling growth
		return plantable.getPlantType(world, pos.offset(direction)) == EnumPlantType.Plains && !(plantable instanceof BlockSapling);
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

		private int meta;
		MulchColor() {
			this.meta = ordinal();
		}

		public int getMeta() {
			return meta;
		}

		@Override
		public String getName() {
			return this.name().toLowerCase(Locale.US);
		}

		public static MulchColor fromMeta(int i) {
			if(i < 0 || i >= values().length) {
				i = 0;
			}
			return values()[i];
		}
	}
}
