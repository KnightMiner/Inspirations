package knightminer.inspirations.library.util;

import knightminer.inspirations.common.Config;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public final class RecipeUtil {
	public static final String TAG_TEXTURE = "texture";

	private RecipeUtil() {}

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

	/**
	 * Adds all blocks from the oredict to the specified block for getSubBlocks
	 */
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

	/**
	 * Checks if the string is valid to be parsed using {@link #getItemStackFromString(String, boolean)}
	 * @param string         Input string
	 * @param allowWildcard  If true, -1 will be a valid metadata to use as wildcard
	 * @return  True if the string is valid to parse with
	 */
	public static boolean isValidItemStack(String string, boolean allowWildcard) {
		String metaString = allowWildcard ? "(-1|[0-9]+)" : "[0-9]+";
		return string.matches("^[a-z_.-]+:[a-z_.-]+(:" + metaString + ")?$");
	}

	/**
	 * Parses an itemstack from a string in the format of "modid:item[:meta]"
	 * @param string         Input string
	 * @param allowWildcard  If true, -1 will be a valid metadata to use as a wildcard.
	 * 						 Additionally, changes the default metadata to wildcard instead of 0
	 * @return  ItemStack parsed from the string, or EMPTY if it is either an invalid string or the item cannot be found.
	 * 			Use (@link {@link #isValidItemStack(String, boolean)} if you need to determine if a string is valid without the item being found
	 */
	public static ItemStack getItemStackFromString(String string, boolean allowWildcard) {
		if(!isValidItemStack(string, allowWildcard)) {
			return ItemStack.EMPTY;
		}

		String[] parts = string.split(":");
		Item item = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(parts[0], parts[1]));
		if(item == null || item == Items.AIR) {
			return ItemStack.EMPTY;
		}

		int meta = allowWildcard ? -1 : 0;
		if(parts.length > 2) {
			// already validated above
			meta = Integer.parseInt(parts[2]);
		}
		if(meta == -1) {
			meta = OreDictionary.WILDCARD_VALUE;
		}

		return new ItemStack(item, 1, meta);
	}
}
