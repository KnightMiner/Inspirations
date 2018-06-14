package knightminer.inspirations.utility.block;

import java.util.Random;

import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCarpetedPressurePlate extends BlockPressurePlate {

	protected static final AxisAlignedBB PRESSED_AABB = new AxisAlignedBB(0, 0, 0, 1, 0.078125, 1);
	protected static final AxisAlignedBB UNPRESSED_AABB = new AxisAlignedBB(0, 0, 0, 1, 0.09375, 1);
	public static final PropertyEnum<EnumDyeColor> COLOR1;
	public static final PropertyEnum<EnumDyeColor> COLOR2;
	static {
		// split the color property into two
		EnumDyeColor[] colors1 = new EnumDyeColor[8];
		EnumDyeColor[] colors2 = new EnumDyeColor[8];
		for(int i = 0; i < 8; i++) {
			colors1[i] = EnumDyeColor.byMetadata(i);
			colors2[i] = EnumDyeColor.byMetadata(i+8);
		}
		COLOR1 = PropertyEnum.create("color", EnumDyeColor.class, colors1);
		COLOR2 = PropertyEnum.create("color", EnumDyeColor.class, colors2);
	}

	private boolean lastHalf;
	public BlockCarpetedPressurePlate(boolean lastHalf) {
		super(Material.ROCK, Sensitivity.MOBS);
		this.setHardness(0.5F);
		this.setSoundType(SoundType.CLOTH);
		this.lastHalf = lastHalf;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		boolean flag = this.getRedstoneStrength(state) > 0;
		return flag ? PRESSED_AABB : UNPRESSED_AABB;
	}

	/* Drops */

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(Blocks.CARPET);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(lastHalf ? COLOR2 : COLOR1).getMetadata();
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		drops.add(new ItemStack(Blocks.CARPET, 1, damageDropped(state)));
		drops.add(new ItemStack(Blocks.STONE_PRESSURE_PLATE));
	}

	@Override
	@Deprecated
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(Blocks.CARPET, 1, damageDropped(state));
	}

	/* Blockstate */

	@Override
	public IBlockState getStateFromMeta(int meta) {
		IBlockState state = this.getDefaultState().withProperty(POWERED, (meta & 1) == 1);
		if(lastHalf) {
			state = state.withProperty(COLOR2, EnumDyeColor.byMetadata((meta >> 1)+8));
		} else {
			state = state.withProperty(COLOR1, EnumDyeColor.byMetadata(meta >> 1));
		}
		return state;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = state.getValue(POWERED).booleanValue() ? 1 : 0;
		meta += (state.getValue(lastHalf ? COLOR2 : COLOR1).getMetadata() % 8) << 1;
		return meta;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {POWERED, COLOR1});
	}

	// there is no clean way to set the variant in the constructor as it must be set before the super constructor is called
	// hence this subclass
	public static class BlockCarpetedPressurePlate2 extends BlockCarpetedPressurePlate {
		public BlockCarpetedPressurePlate2() {
			super(true);
		}

		@Override
		protected BlockStateContainer createBlockState() {
			return new BlockStateContainer(this, new IProperty[] {POWERED, COLOR2});
		}
	}
}
