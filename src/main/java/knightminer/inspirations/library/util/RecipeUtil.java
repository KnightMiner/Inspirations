package knightminer.inspirations.library.util;

import java.util.function.Consumer;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
		return string.matches("^[a-z0-9_.-]+:[a-z0-9_.-]+$");
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
		NonNullList<ItemStack> subItems = NonNullList.create();
		stack.getItem().fillItemGroup(ItemGroup.SEARCH, subItems);
		for(ItemStack subStack : subItems) {
			callback.accept(subStack);
		}
	}

	/**
	 * Gets a block and meta from a string
	 * @param string  Input string in the format "modid:block[:meta]"
	 * @return the block, or null if errored
	 */
	@Nullable
	private static Block getBlockFromString(String string) {
		if(!isValidItemStack(string, false)) {
			log.warn("Invalid block string {}", string);
			return null;
		}

		Block block = GameRegistry.findRegistry(Block.class).getValue(new ResourceLocation(string));
		if(block == null || block == Blocks.AIR) {
			log.debug("Failed to find block {}", string);
			return null;
		}
		return block;
	}

	/**
	 * Returns a blockstate from the given string
	 * @param string  Input string, in the form "modid:block".
	 * @return  Block state for the given string
	 */
	@Nullable
	public static BlockState getBlockStateFromString(String string) {
		Block block = getBlockFromString(string);
		return (block == null) ? null : block.getDefaultState();
	}

	/**
	 * Runs the given callback on all states returned by the block state string
	 * @param string         Input string in the format "modid:block"
	 * @param stateConsumer  Callback for block state results
	 * @param blockConsumer  Callback for block results. If null, runs the state consumer on all possible block states.
	 */
	public static void forBlockInString(String string, Consumer<BlockState> stateConsumer, Consumer<Block> blockConsumer) {
		Block block = getBlockFromString(string);
		if(block == null) {
			return;
		}
		// if we have no block consumer, run the state consumer on all valid states
		if(blockConsumer == null) {
			for(BlockState state : block.getStateContainer().getValidStates()) {
				stateConsumer.accept(state);
			}
		} else {
			// if there is a block consumer use that
			blockConsumer.accept(block);
		}
	}
}
