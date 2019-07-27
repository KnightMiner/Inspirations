package knightminer.inspirations.library.util;

import java.util.function.Consumer;

import net.minecraft.item.Items;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.state.BlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public final class RecipeUtil {
	private static final Logger log = LogManager.getLogger("inspirations-lib");

	private RecipeUtil() {}

	/**
	 * Checks if the string is valid to be parsed using {@link #getItemStackFromString(String, boolean)}
	 * @param string         Input string
	 * @param allowWildcard  If true, -1 will be a valid metadata to use as wildcard
	 * @return  True if the string is valid to parse with
	 */
	public static boolean isValidItemStack(String string, boolean allowWildcard) {
		String metaString = allowWildcard ? "(-1|[0-9]+)" : "[0-9]+";
		return string.matches("^[a-z0-9_.-]+:[a-z0-9_.-]+(:" + metaString + ")?$");
	}

	/**
	 * Parses an itemstack from a string in the format of "modid:item"
	 * @param string         Input string
	 * @param allowWildcard  If true, -1 will be a valid metadata to use as a wildcard.
	 * 						 Additionally, changes the default metadata to wildcard instead of 0
	 * @return  ItemStack parsed from the string, or EMPTY if it is either an invalid string or the item cannot be found.
	 * 			Use (@link {@link #isValidItemStack(String, boolean)} if you need to determine if a string is valid without the item being found
	 */
	public static ItemStack getItemStackFromString(String string, boolean allowWildcard) {
		if(!isValidItemStack(string, allowWildcard)) {
			log.warn("Invalid stack string {}", string);
			return ItemStack.EMPTY;
		}

		String[] parts = string.split(":");
		Item item = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(parts[0], parts[1]));
		if(item == null || item == Items.AIR) {
			log.debug("Failed to find stack {}", string);
			return ItemStack.EMPTY;
		}

		return new ItemStack(item);
	}

	/**
	 * Runs the provided callback on all itemstacks obtained from the given stack string
	 * @param string    Input string in the format of "modid:item[:meta]". Treats no meta as wildcard
	 * @param callback  Callback function to run for all stacks
	 */
	public static void forStackInString(String string, Consumer<ItemStack> callback) {
		ItemStack stack = getItemStackFromString(string, true);
		if(stack.isEmpty()) {
			return;
		}

		// if wildcard, run all stacks
		if(stack.getMetadata() == OreDictionary.WILDCARD_VALUE) {
			NonNullList<ItemStack> subItems = NonNullList.create();
			stack.getItem().getSubItems(CreativeTabs.SEARCH, subItems);
			for(ItemStack subStack : subItems) {
				callback.accept(subStack);
			}
		} else {
			callback.accept(stack);
		}
	}

	/**
	 * Gets a block and meta from a string
	 * @param string  Input string in the format "modid:block[:meta]"
	 * @return  Pair of block and meta, or block and null if meta is unspecified or -1. Null if errored
	 */
	private static Pair<Block, Integer> getBlockFromString(String string) {
		if(!isValidItemStack(string, false)) {
			log.warn("Invalid block string {}", string);
			return null;
		}

		String[] parts = string.split(":");
		Block block = GameRegistry.findRegistry(Block.class).getValue(new ResourceLocation(parts[0], parts[1]));
		if(block == null || block == Blocks.AIR) {
			log.debug("Failed to find block {}", string);
			return null;
		}

		Integer meta = null;
		if(parts.length > 2) {
			// already validated above
			meta = Integer.parseInt(parts[2]);
			if(meta == -1) {
				meta = null;
			}
		}

		return Pair.of(block, meta);
	}

	/**
	 * Returns a blockstate from the given string
	 * @param string  Input string, in the form "modid:block[:meta]". If meta is unset or -1 it returns the default state
	 * @return  Block state for the given string
	 */
	@SuppressWarnings("deprecation")
	public static IBlockState getBlockStateFromString(String string) {
		Pair<Block, Integer> pair = getBlockFromString(string);
		if(pair == null) {
			return null;
		}
		if(pair.getRight() == null) {
			return pair.getLeft().getDefaultState();
		} else {
			return pair.getLeft().getStateFromMeta(pair.getRight());
		}
	}

	/**
	 * Runs the given callback on all states returned by the block state string
	 * @param string         Input string in the format "modid:block[:meta]"
	 * @param stateConsumer  Callback for block state results
	 * @param blockConsumer  Callback for block results. If null, runs the state consumer on all possible block states
	 */
	@SuppressWarnings("deprecation")
	public static void forBlockInString(String string, Consumer<IBlockState> stateConsumer, Consumer<Block> blockConsumer) {
		Pair<Block, Integer> pair = getBlockFromString(string);
		if(pair == null) {
			return;
		}
		if(pair.getRight() == null) {
			// if we have no metadata and no block consumer, run the state consumer on all valid states
			if(blockConsumer == null) {
				for(IBlockState state : pair.getLeft().getBlockState().getValidStates()) {
					stateConsumer.accept(state);
				}
			} else {
				// if there is a block consumer use that
				blockConsumer.accept(pair.getLeft());
			}
		} else {
			stateConsumer.accept(pair.getLeft().getStateFromMeta(pair.getRight()));
		}
	}
}
