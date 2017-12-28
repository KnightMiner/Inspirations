package knightminer.inspirations.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import knightminer.inspirations.library.recipe.cauldron.CauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import slimeknights.mantle.util.RecipeMatch;

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

	/**
	 * Internal function used to allow the config to set the list of book keywords. Should not need to be called outside of Inspirations
	 * @param keywords Keyword list
	 */
	public static void setBookKeywords(String[] keywords) {
		bookKeywords = keywords;
	}


	/*
	 * Anvil smashing
	 */
	private static Map<IBlockState, IBlockState> anvilSmashing = new HashMap<>();
	private static Map<Block, IBlockState> anvilSmashingBlocks = new HashMap<>();
	private static Set<Material> anvilBreaking = new HashSet<>();

	/**
	 * Registers an anvil smashing result for the given block state
	 * @param input   Input state
	 * @param result  Result state
	 */
	public static void registerAnvilSmashing(IBlockState input, IBlockState result) {
		anvilSmashing.put(input, result);
	}

	/**
	 * Registers an anvil smashing result for the given block state
	 * @param input   Input state
	 * @param result  Result block
	 */
	public static void registerAnvilSmashing(IBlockState input, Block result) {
		registerAnvilSmashing(input, result.getDefaultState());
	}

	/**
	 * Registers an anvil smashing result to break the given blockstate
	 * @param input   Input block
	 */
	public static void registerAnvilBreaking(IBlockState input) {
		registerAnvilSmashing(input, Blocks.AIR);
	}

	/**
	 * Registers an anvil smashing result for the given block
	 * @param input   Input block
	 * @param result  Result state
	 */
	public static void registerAnvilSmashing(Block input, IBlockState result) {
		anvilSmashingBlocks.put(input, result);
	}

	/**
	 * Registers an anvil smashing result to break the given block
	 * @param input   Input block
	 * @param result  Result block
	 */
	public static void registerAnvilSmashing(Block input, Block result) {
		registerAnvilSmashing(input, result.getDefaultState());
	}

	/**
	 * Registers an anvil smashing result to break the given block
	 * @param input   Input block
	 */
	public static void registerAnvilBreaking(Block input) {
		registerAnvilSmashing(input, Blocks.AIR);
	}

	/**
	 * Registers an anvil smashing result to break the given material
	 * @param material  Input material
	 */
	public static void registerAnvilBreaking(Material material) {
		anvilBreaking.add(material);
	}

	/**
	 * Gets the result of an anvil landing on the given blockstate
	 * @param state  Input blockstate
	 * @return  BlockState result. Will be air if its breaking, or null if there is no behavior
	 */
	public static IBlockState getAnvilSmashResult(IBlockState state) {
		if(anvilSmashing.containsKey(state)) {
			return anvilSmashing.get(state);
		}
		Block block = state.getBlock();
		if(anvilSmashingBlocks.containsKey(block)) {
			return anvilSmashingBlocks.get(block);
		}
		if(anvilBreaking.contains(state.getMaterial())) {
			return Blocks.AIR.getDefaultState();
		}
		return null;
	}

	/**
	 * Checks if we have a state specific smashing result. Used for JEI to filter out the lists for blocks
	 * @param state  State to check
	 * @return  True if we have a state specific result
	 */
	public static boolean hasAnvilSmashStateResult(IBlockState state) {
		return anvilSmashing.containsKey(state);
	}

	/**
	 * Gets all smashing recipes in the form of blockstate to blockstate
	 * @return  List of map entries for the recipes
	 */
	public static List<Map.Entry<IBlockState,IBlockState>> getAllAnvilStateSmashing() {
		return ImmutableList.copyOf(anvilSmashing.entrySet());
	}

	/**
	 * Gets all smashing recipes in the form of block to blockstate
	 * @return  List of map entries for the recipes
	 */
	public static List<Map.Entry<Block,IBlockState>> getAllAnvilBlockSmashing() {
		return ImmutableList.copyOf(anvilSmashingBlocks.entrySet());
	}


	/*
	 * Cauldron recipes
	 */
	private static List<ICauldronRecipe> cauldronRecipes = new ArrayList<>();

	/**
	 * Gets the result of a cauldron recipe
	 * @param input      ItemStack input
	 * @param isBoiling  Whether the cauldron is boiling
	 * @return  Result of the recipe
	 */
	public static ICauldronRecipe getCauldronResult(ItemStack input, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
		for(ICauldronRecipe recipe : cauldronRecipes) {
			if(recipe.matches(input, boiling, level, state)) {
				return recipe;
			}
		}
		return null;
	}

	/**
	 * Gets the result of a cauldron recipe
	 * @param input      ItemStack input
	 * @param isBoiling  Whether the cauldron is boiling
	 * @return  Result of the recipe
	 */
	public static ICauldronRecipe getCauldronResult(ItemStack input, boolean boiling, int level) {
		return getCauldronResult(input, boiling, level, ICauldronRecipe.CauldronState.WATER);
	}

	/**
	 * Adds a new cauldron recipe
	 * @param recipe  Recipe to add
	 */
	public static void addCauldronRecipe(ICauldronRecipe recipe) {
		cauldronRecipes.add(recipe);
	}

	/**
	 * Adds a new cauldron recipe
	 * @param input      ItemStack to check for
	 * @param output     Recipe output
	 * @param boiling  Whether the cauldron must be boiling or not
	 */
	public static void addCauldronRecipe(ItemStack input, ItemStack output, boolean boiling) {
		addCauldronRecipe(new CauldronRecipe(RecipeMatch.of(input), output, boiling));
	}

	/**
	 * Adds a new cauldron recipe
	 * @param input      oreDict name to check for
	 * @param output     Recipe output
	 * @param boiling  Whether the cauldron must be boiling or not
	 */
	public static void addCauldronRecipe(String input, ItemStack output, boolean boiling) {
		addCauldronRecipe(new CauldronRecipe(RecipeMatch.of(input), output, boiling));
	}

	/**
	 * Adds a new cauldron recipe
	 * @param input      RecipeMatch to check for
	 * @param output     Recipe output
	 * @param boiling  Whether the cauldron must be boiling or not
	 */
	public static void addCauldronRecipe(RecipeMatch input, ItemStack output, boolean boiling) {
		addCauldronRecipe(new CauldronRecipe(input, output, boiling));
	}

	/**
	 * Gets all cauldron recipes
	 * @return  A list of all cauldron recipes
	 */
	public static List<ICauldronRecipe> getAllCauldronRecipes() {
		return ImmutableList.copyOf(cauldronRecipes);
	}
}
