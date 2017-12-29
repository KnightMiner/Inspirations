package knightminer.inspirations.utility.block;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

public class BlockRedstoneBarrel extends Block {

	private static final AxisAlignedBB BOUNDS = new AxisAlignedBB(0.0625, 0, 0.0625, 0.9375, 1, 0.9375);
	public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 15);
	public BlockRedstoneBarrel() {
		super(Material.ROCK);

		this.setCreativeTab(CreativeTabs.REDSTONE);
		this.setHardness(1.5F);
		this.setResistance(10.0F);
		this.setSoundType(SoundType.STONE);
	}


	/* Blockstate */

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, LEVEL);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(LEVEL, meta);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(LEVEL);
	}


	/* Properties */

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
		drops.add(new ItemStack(this));
		int level = state.getValue(LEVEL);
		if(level > 0) {
			drops.add(new ItemStack(Items.REDSTONE, level));
		}
	}

	@Nonnull
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BOUNDS;
	}

	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for render
	 */
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}


	/* Redstone */
	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
		return blockState.getValue(LEVEL);
	}

	/**
	 * Called when the block is right clicked by a player.
	 */
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		int level = state.getValue(LEVEL);
		ItemStack stack = player.getHeldItem(hand);

		// holding redstone: fill
		if(stack.getItem() == Items.REDSTONE) {
			if(level < 15) {
				if(!world.isRemote) {
					if(!player.capabilities.isCreativeMode) {
						stack.shrink(1);
					}
					setLevel(world, pos, state, level + 1);
				}
				return true;
			}

			return true;

			// not holding redstone: extract
		} else if(level > 0) {
			if(!world.isRemote) {
				ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.REDSTONE));
				setLevel(world, pos, state, level - 1);
			}
			return true;
		}
		return false;
	}

	public void setLevel(World world, BlockPos pos, IBlockState state, int level) {
		world.setBlockState(pos, state.withProperty(LEVEL, Integer.valueOf(MathHelper.clamp(level, 0, 15))), 2);
		world.updateComparatorOutputLevel(pos, this);
	}
}
