package knightminer.inspirations.building.block;

import java.util.Locale;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDoublePlant.EnumPlantType;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFlower extends BlockBush implements IGrowable {
	public static final PropertyEnum<FlowerType> TYPE = PropertyEnum.create("type", FlowerType.class);

	public BlockFlower() {
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, FlowerType.ROSE));
		this.setHardness(0F);
		this.setSoundType(SoundType.PLANT);
	}


	/* Blockstate */

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(TYPE, FlowerType.fromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(TYPE).getMeta();
	}

	/**
	 * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
	 * returns the metadata of the dropped item based on the old metadata of the block.
	 */
	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(TYPE).getMeta();
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items)  {
		for (FlowerType type : FlowerType.values()) {
			items.add(new ItemStack(this, 1, type.getMeta()));
		}
	}


	/* Planty stuff */

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return super.getBoundingBox(state, source, pos).offset(state.getOffset(source, pos));
	}

	@Override
	public Block.EnumOffsetType getOffsetType() {
		return Block.EnumOffsetType.XZ;
	}


	/* Doubling up */

	@Override
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
		return state.getValue(TYPE) != FlowerType.CYAN;
	}


	@Override
	public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
		return true;
	}


	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		EnumPlantType type = state.getValue(TYPE).getDouble();
		// should not happen, but catch anyways
		if(type == null) {
			return;
		}

		if (worldIn.isAirBlock(pos.up())) {
			Blocks.DOUBLE_PLANT.placeAt(worldIn, pos, type, 2);
		}
	}

	public static enum FlowerType implements IStringSerializable {
		ROSE(EnumPlantType.ROSE),
		SYRINGA(EnumPlantType.SYRINGA),
		PAEONIA(EnumPlantType.PAEONIA),
		CYAN(null);

		private final EnumPlantType big;
		private final int meta;
		FlowerType(EnumPlantType big) {
			this.meta = ordinal();
			this.big = big;
		}

		public int getMeta() {
			return this.meta;
		}

		public static FlowerType fromMeta(int meta) {
			if(meta < 0 || meta >= values().length) {
				meta = 0;
			}

			return values()[meta];
		}

		@Nullable
		public EnumPlantType getDouble() {
			return this.big;
		}

		@Nullable
		public static FlowerType fromDouble(@Nonnull EnumPlantType big) {
			for(FlowerType type : FlowerType.values()) {
				if(big == type.getDouble()) {
					return type;
				}
			}

			return null;
		}

		@Override
		public String getName() {
			return name().toLowerCase(Locale.US);
		}
	}
}
