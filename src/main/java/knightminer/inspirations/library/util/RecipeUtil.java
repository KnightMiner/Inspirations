package knightminer.inspirations.library.util;

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
}
