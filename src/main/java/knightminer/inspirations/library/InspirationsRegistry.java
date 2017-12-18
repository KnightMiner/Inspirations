package knightminer.inspirations.library;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InspirationsRegistry {
	/*
	 * Books
	 */
	private static Map<ItemMetaKey,Boolean> books = new HashMap<>();
	private static String[] bookKeywords = new String[0];

	/**
	 * Checks if the given item stack is a book
	 * @param stack  Input stack
	 * @return  True if its a book
	 */
	public static boolean isBook(ItemStack stack) {
		return books.computeIfAbsent(new ItemMetaKey(stack), InspirationsRegistry::isBook);
	}

	/**
	 * Helper function to check if a stack is a book, used internally by the book map
	 * @param key  Item meta combination
	 * @return  True if it is a book
	 */
	private static Boolean isBook(ItemMetaKey key) {
		Item item = key.getItem();
		// blocks are not books, catches bookshelves
		if(Block.getBlockFromItem(item) != Blocks.AIR) {
			return false;
		}

		// look through every keyword from the config
		ItemStack stack = key.makeItemStack();
		for(String keyword : bookKeywords) {
			// if the unlocalized name or the registry name has the keyword, its a book
			if(item.getRegistryName().getResourcePath().contains(keyword)
					|| stack.getUnlocalizedName().contains(keyword)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Registers an override to state a stack is definately a book or not a book, primarily used by the config
	 * @param stack   Itemstack which is a book
	 * @param isBook  True if its a book, false if its not a book
	 */
	public static void registerBook(ItemStack stack, boolean isBook) {
		books.put(new ItemMetaKey(stack), isBook);
	}

	/**
	 * Registers an override to state a stack is definately a book or not a book, primarily used by the config
	 * @param item  Item which is a book
	 * @param meta  Meta which is a book
	 * @param isBook  True if its a book, false if its not a book
	 */
	public static void registerBook(Item item, int meta, boolean isBook) {
		books.put(new ItemMetaKey(item, meta), isBook);
	}

	public static void setBookKeywords(String[] keywords) {
		bookKeywords = keywords;
	}
}
