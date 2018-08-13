package knightminer.inspirations.tweaks.block;

import javax.annotation.Nonnull;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.client.ModelHelper;
import slimeknights.mantle.property.PropertyString;

public class BlockBetterFlowerPot extends BlockFlowerPot {

	public static final PropertyBool EXTRA = PropertyBool.create("extra");
	public static final PropertyString TEXTURE = TextureBlockUtil.TEXTURE_PROP;
	public static final String TAG_TEXTURE_PATH = "texture_path";

	public BlockBetterFlowerPot() {
		this.setHardness(0.0F);
		this.setSoundType(SoundType.STONE);
		this.setUnlocalizedName("flowerPot");
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(CONTENTS, EnumFlowerType.EMPTY)
				.withProperty(LEGACY_DATA, 0)
				.withProperty(EXTRA, false));
	}

	@Override
	protected ExtendedBlockState createBlockState() {
		return new ExtendedBlockState(this, new IProperty[] {CONTENTS, LEGACY_DATA, EXTRA}, new IUnlistedProperty[] {TEXTURE});
	}

	/**
	 * Called when the block is right clicked by a player.
	 */
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		// this is basically a copy of the original method since I needed to override the private method canBePotted
		ItemStack stack = player.getHeldItem(hand);
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityFlowerPot)) {
			return false;
		}
		TileEntityFlowerPot flowerPot = (TileEntityFlowerPot) te;
		ItemStack flower = flowerPot.getFlowerItemStack();

		if(flower.isEmpty()) {
			if(!InspirationsRegistry.isFlower(stack)) {
				return false;
			}

			flowerPot.setItemStack(stack);
			flowerPot.getTileData().removeTag(TAG_TEXTURE_PATH);
			player.addStat(StatList.FLOWER_POTTED);

			if(!player.capabilities.isCreativeMode) {
				stack.shrink(1);
			}
		} else {
			ItemHandlerHelper.giveItemToPlayer(player, flower, player.inventory.currentItem);
			flowerPot.setItemStack(ItemStack.EMPTY);
		}

		flowerPot.markDirty();
		world.notifyBlockUpdate(pos, state, state, 3);
		return true;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		state = super.getActualState(state, world, pos);
		if(state.getValue(CONTENTS) == EnumFlowerType.EMPTY) {
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof TileEntityFlowerPot) {
				if(!((TileEntityFlowerPot)te).getFlowerItemStack().isEmpty()) {
					state = state.withProperty(EXTRA, true);
				}
			}
		}

		return state;
	}

	@Nonnull
	@Override
	public IBlockState getExtendedState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
		if(!state.getValue(EXTRA)) {
			return state;
		}

		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileEntityFlowerPot) {
			TileEntityFlowerPot flowerPot = (TileEntityFlowerPot) te;
			String texture = flowerPot.getTileData().getString(TAG_TEXTURE_PATH);
			if(texture.isEmpty()) {
				// load it from stored block
				ItemStack stack = flowerPot.getFlowerItemStack();
				if(!stack.isEmpty()) {
					Block block = Block.getBlockFromItem(stack.getItem());
					if(block != Blocks.AIR) {
						texture = ModelHelper.getTextureFromBlock(block, stack.getItemDamage()).getIconName();
						flowerPot.getTileData().setString(TAG_TEXTURE_PATH, texture);
					}
				}
			}
			if(!texture.isEmpty()) {
				state = ((IExtendedBlockState)state).withProperty(TEXTURE, texture);
			}
		}

		return state;
	}


	/*
	 * Comparator
	 */

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return Config.flowerPotComparator;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
		if(!Config.flowerPotComparator) {
			return 0;
		}
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileEntityFlowerPot) {
			return getComparatorSignal(((TileEntityFlowerPot) te).getFlowerItemStack());
		}

		return 0;
	}

	private int getComparatorSignal(ItemStack stack) {
		if(stack.isEmpty()) {
			return 0;
		}

		return InspirationsRegistry.getFlowerComparatorPower(stack);
	}
}
