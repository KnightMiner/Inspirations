package knightminer.inspirations.library.util;

import knightminer.inspirations.common.Config;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public final class RecipeUtil {
	public static final String TAG_TEXTURE = "texture";

	private RecipeUtil() {}

	public static ItemStack createTexturedStack(Block texturable, int tableMeta, Block block, int blockMeta) {
		ItemStack stack = new ItemStack(texturable, 1, tableMeta);

		if(block != null) {
			ItemStack blockStack = new ItemStack(block, 1, blockMeta);
			NBTTagCompound tag = new NBTTagCompound();
			NBTTagCompound subTag = new NBTTagCompound();
			blockStack.writeToNBT(subTag);
			tag.setTag(TAG_TEXTURE, subTag);
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
		NBTTagCompound tag = TagUtil.getTagSafe(table).getCompoundTag(TAG_TEXTURE);
		return new ItemStack(tag);
	}

	public static void addBlocksFromOredict(String oredict, Block block, int meta, NonNullList<ItemStack> list) {
		for(ItemStack stack : OreDictionary.getOres(oredict, false)) {
			Block textureBlock = Block.getBlockFromItem(stack.getItem());
			int textureMeta = stack.getItemDamage();

			if(textureMeta == OreDictionary.WILDCARD_VALUE) {
				NonNullList<ItemStack> subBlocks = NonNullList.create();
				textureBlock.getSubBlocks(CreativeTabs.SEARCH, subBlocks);
				for(ItemStack subBlock : subBlocks) {
					list.add(createTexturedStack(block, meta, Block.getBlockFromItem(subBlock.getItem()), subBlock.getItemDamage()));
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
