package knightminer.inspirations.library.util;

import java.util.List;

import com.google.common.base.Splitter;

import knightminer.inspirations.library.Util;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public final class RecipeUtil {
	private RecipeUtil() {}

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

	/**
	 * Parse a block state from a string with the format modid:name[:meta]
	 * @param input input string
	 * @return the block state or null if none could be found
	 */
	public static IBlockState getBlockstateFromString(String input) {
		List<String> parts = Splitter.on(":").splitToList(input);
		if(parts.size() < 2 || parts.size() > 3) {
			return null;
		}

		// find block
		String modId = parts.get(0);
		String itemName = parts.get(1);
		Block block = GameRegistry.findRegistry(Block.class).getValue(new ResourceLocation(modId, itemName));
		if(block == null) {
			return null;
		}

		// get actual state
		if(parts.size() == 3) {
			// parse meta
			Integer meta = Util.getInteger(parts.get(2));
			return meta != null && meta >= 0 ? block.getStateFromMeta(meta) : null;
		} else {
			// use default state
			return block.getDefaultState();
		}
	}

	/**
	 * Transform a string with format mod:item[:meta][*count] into an item stack.
	 * @param input input string
	 * @return item stack, might be {@link ItemStack#EMPTY} if it cannot be parsed
	 */
	public static ItemStack getItemStackWithCountFromString(String input) {
		// split off the item count
		String[] parts = input.split("\\*");
		if(parts.length == 0 || parts.length > 2) {
			return ItemStack.EMPTY;
		}

		// transform the item stack part
		ItemStack stack = RecipeUtil.getItemStackFromString(parts[0], false);
		if(stack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		// if the item count was specified, set it for the item stack
		if(parts.length > 1) {
			Integer count = Util.getInteger(parts[1]);
			if(count == null || count < 0) {
				return ItemStack.EMPTY;
			}
			stack.setCount(count);
		}
		return stack;
	}
}
