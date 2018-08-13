package knightminer.inspirations.library.util;

import knightminer.inspirations.common.Config;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import slimeknights.mantle.property.PropertyString;

public final class TextureBlockUtil {

	public static final String TAG_TEXTURE = "texture";
	public static final PropertyString TEXTURE_PROP = new PropertyString("TEXTURE");

	private TextureBlockUtil() {}

	/**
	 * Call in {@link Block#onBlockPlacedBy(World, BlockPos, IBlockState, net.minecraft.entity.EntityLivingBase, ItemStack)}
	 * to set the texture tag to the Tile Entity
	 * @param world  World where the block was placed
	 * @param pos    Block position
	 * @param stack  Item stack
	 */
	public static void placeTextureBlock(World world, BlockPos pos, ItemStack stack) {
		NBTTagCompound tag = TagUtil.getTagSafe(stack);
		TileEntity te = world.getTileEntity(pos);
		if(te != null) {
			NBTTagCompound textureTag = tag.getCompoundTag(TextureBlockUtil.TAG_TEXTURE);
			if(textureTag == null) {
				textureTag = new NBTTagCompound();
			}

			updateTextureBlock(te, textureTag);
		}
	}

	/**
	 * Called in blocks to get the item stack for the current block
	 * @param world  World
	 * @param pos    Pos
	 * @param state  State
	 * @return
	 */
	public static ItemStack getBlockItemStack(IBlockAccess world, BlockPos pos, IBlockState state) {
		Block block = state.getBlock();
		ItemStack stack = new ItemStack(block, 1, block.damageDropped(state));
		TileEntity te = world.getTileEntity(pos);
		if(te != null) {
			NBTTagCompound texture = getTextureBlock(te);
			if(texture.getSize() > 0) {
				NBTTagCompound tags = new NBTTagCompound();
				tags.setTag(TextureBlockUtil.TAG_TEXTURE, texture);
				stack.setTagCompound(tags);
			}
		}
		return stack;
	}

	/**
	 * Updates the current texture block in the TE
	 * @param tag
	 */
	public static void updateTextureBlock(TileEntity te, NBTTagCompound tag) {
		if(te != null) {
			te.getTileData().setTag(TextureBlockUtil.TAG_TEXTURE, tag);
		}
	}

	/**
	 * Gets the current texture block from the TE
	 * @return
	 */
	public static NBTTagCompound getTextureBlock(TileEntity te) {
		if(te == null) {
			return new NBTTagCompound();
		}
		return te.getTileData().getCompoundTag(TextureBlockUtil.TAG_TEXTURE);
	}

	/**
	 * Creates a new item stack with the given block as it's texture tag
	 * @param texturable  Base block to texture
	 * @param texMeta     Base meta
	 * @param block       Block to use as the texture
	 * @param blockMeta   Meta for the texture
	 * @return  The item stack with the proper NBT
	 */
	public static ItemStack createTexturedStack(Block texturable, int texMeta, Block block, int blockMeta) {
		ItemStack stack = new ItemStack(texturable, 1, texMeta);

		if(block != null) {
			ItemStack blockStack = new ItemStack(block, 1, blockMeta);
			NBTTagCompound tag = new NBTTagCompound();
			NBTTagCompound subTag = new NBTTagCompound();
			blockStack.writeToNBT(subTag);
			tag.setTag(TextureBlockUtil.TAG_TEXTURE, subTag);
			stack.setTagCompound(tag);
		}

		return stack;
	}

	/**
	 * Gets the itemstack that determines the leg's texture from the table
	 * @param table  Input table
	 * @return  The itemstack determining the leg's texture, or null if none exists
	 */
	public static ItemStack getStackTexture(ItemStack table) {
		NBTTagCompound tag = TagUtil.getTagSafe(table).getCompoundTag(TextureBlockUtil.TAG_TEXTURE);
		return new ItemStack(tag);
	}

	/**
	 * Adds all blocks from the oredict to the specified block for getSubBlocks
	 */
	public static void addBlocksFromOredict(String oredict, Block block, int meta, NonNullList<ItemStack> list) {
		for(ItemStack stack : OreDictionary.getOres(oredict, false)) {
			Block textureBlock = Block.getBlockFromItem(stack.getItem());
			int textureMeta = stack.getMetadata();

			if(textureMeta == OreDictionary.WILDCARD_VALUE) {
				NonNullList<ItemStack> subBlocks = NonNullList.create();
				textureBlock.getSubBlocks(CreativeTabs.SEARCH, subBlocks);
				for(ItemStack subBlock : subBlocks) {
					list.add(createTexturedStack(block, meta, Block.getBlockFromItem(subBlock.getItem()), subBlock.getMetadata()));
					if(!Config.showAllVariants) {
						return;
					}
				}
			}
			else {
				list.add(createTexturedStack(block, meta, textureBlock, textureMeta));
				if(!Config.showAllVariants) {
					return;
				}
			}
		}
	}
}
