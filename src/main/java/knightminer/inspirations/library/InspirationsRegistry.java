package knightminer.inspirations.library;

import com.google.common.collect.ImmutableList;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.event.RegisterEvent.RegisterCauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.CauldronFluidRecipe;
import knightminer.inspirations.library.recipe.cauldron.CauldronFluidTransformRecipe;
import knightminer.inspirations.library.recipe.cauldron.FillCauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe.CauldronState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.ToolType;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.util.RecipeMatch;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused") // This is an API.
public class InspirationsRegistry {
	public static final Logger log = Util.getLogger("api");

	// Blocks with this tag act as fire for the cauldron.
	public static final Tag<Block> TAG_CAULDRON_FIRE = new BlockTags.Wrapper(new ResourceLocation(Inspirations.modID, "cauldron_fire"));
	// Items with this tag are registered to have fluid tank functionality.
	public static final Tag<Item> TAG_DISP_FLUID_TANKS = new ItemTags.Wrapper(new ResourceLocation(Inspirations.modID, "fluid_containers"));
	// Items with this tag are registered to perform cauldron recipes.
	public static final Tag<Item> TAG_DISP_CAULDRON_RECIPES = new ItemTags.Wrapper(new ResourceLocation(Inspirations.modID, "cauldron_recipes"));

	public static final Tag<Item> TAG_MILK_CONTAINERS = new ItemTags.Wrapper(new ResourceLocation(Inspirations.modID, "milk_containers"));

	public static final ToolType SHEAR_TYPE = ToolType.get("shears");

	public static final Tag<Item> TAG_DYE_BOTTLES = new ItemTags.Wrapper(Util.getResource("dyed_water_bottles"));

	/**
	 * Sets a value from the Inspirations config into the registry. Used to keep the config out of the library
	 * @param key   Key to set
	 * @param value Boolean value
	 * @deprecated For internal use only
	 */
	@Deprecated
	public static void setConfig(String key, boolean value) {
		switch (key) {
			case "biggerCauldron":
				cauldronBigger = value;
				break;
			case "expensiveCauldronBrewing":
				expensiveCauldronBrewing = value;
				break;
			default:
				// should never happen
				throw new IllegalArgumentException("Unexpected config key " + key);
		}
	}

	/*
	 * Books
	 */
	private static Map<Item, Float> books = new HashMap<>();
	private static Map<Item, Float> bookCache = new HashMap<>();
	private static List<String> bookKeywords = new ArrayList<>();

	// Replicates Config.defaultEnchantingPower.
	private static float defaultEnchantingPower = 1.5f;

	/**
	 * Internal function to set defaultEnchantingPower.
	 * @deprecated For internal use only.
	 */
	public static void setDefaultEnchantingPower(float power) {
		defaultEnchantingPower = power;
	}

	/**
	 * Checks if the given item stack is a book
	 * @param stack Input stack
	 * @return True if its a book
	 */
	public static boolean isBook(ItemStack stack) {
		return !stack.isEmpty() && getBookEnchantingPower(stack) >= 0;
	}

	/**
	 * Checks if the given item stack is a book
	 * @param book Input stack
	 * @return True if its a book
	 */
	public static float getBookEnchantingPower(ItemStack book) {
		if (book.isEmpty()) {
			return 0;
		}
		return bookCache.computeIfAbsent(book.getItem(), InspirationsRegistry::bookPower);
	}

	/**
	 * Helper function to check if a stack is a book, used internally by the book map
	 * @param item The item.
	 * @return The enchantment power, or -1F.
	 */
	@Nonnull
	private static Float bookPower(Item item) {
		Float override = books.get(item);
		if (override != null) {
			return override;
		}

		// blocks are not books, catches bookshelves
		if (Block.getBlockFromItem(item) != Blocks.AIR) {
			return -1f;
		}

		// look through every keyword from the config
		for (String keyword : bookKeywords) {
			// if the unlocalized name or the registry name has the keyword, its a book
			if (item.getRegistryName().getPath().contains(keyword)
					|| item.getTranslationKey().contains(keyword)) {
				return defaultEnchantingPower;
			}
		}
		return -1f;
	}

	/**
	 * Registers an override to state a stack is definately a book or not a book, primarily used by the config
	 * @param item   Item which is a book
	 * @param isBook True if its a book, false if its not a book
	 * @deprecated use {@link #registerBook(Item, float)}
	 */
	@Deprecated
	public static void registerBook(Item item, boolean isBook) {
		books.put(item, isBook ? 1.5f : -1f);
	}

	/**
	 * Registers an override to state a stack is a book with an enchanting power
	 * @param item  Item which is a book
	 * @param power Enchanting power, 1.5 is default, NaN is not a book. 0 is a valid power
	 */
	public static void registerBook(Item item, float power) {
		books.put(item, power);
	}

	/**
	 * Internal function used to allow the config to set the list of book keywords. Should not need to be called outside of Inspirations
	 * @param keywords Keyword list
	 */
	public static void setBookKeywords(List<String> keywords) {
		bookKeywords = keywords;
		// Clear the cache.
		bookCache.clear();
	}

	/*
	 * Anvil smashing
	 */
	private static Map<BlockState, BlockState> anvilSmashing = new HashMap<>();
	private static Map<Block, BlockState> anvilSmashingBlocks = new HashMap<>();
	private static Set<Material> anvilBreaking = new HashSet<>();

	/**
	 * Registers an anvil smashing result for the given block state
	 * @param input  Input state
	 * @param result Result state
	 */
	public static void registerAnvilSmashing(BlockState input, BlockState result) {
		anvilSmashing.put(input, result);
	}

	/**
	 * Registers an anvil smashing result for the given block state
	 * @param input  Input state
	 * @param result Result block
	 */
	public static void registerAnvilSmashing(BlockState input, Block result) {
		registerAnvilSmashing(input, result.getDefaultState());
	}

	/**
	 * Registers an anvil smashing result to break the given blockstate
	 * @param input Input block
	 */
	public static void registerAnvilBreaking(BlockState input) {
		registerAnvilSmashing(input, Blocks.AIR);
	}

	/**
	 * Registers an anvil smashing result for the given block
	 * @param input  Input block
	 * @param result Result state
	 */
	public static void registerAnvilSmashing(Block input, BlockState result) {
		anvilSmashingBlocks.put(input, result);
	}

	/**
	 * Registers an anvil smashing result to break the given block
	 * @param input  Input block
	 * @param result Result block
	 */
	public static void registerAnvilSmashing(Block input, Block result) {
		registerAnvilSmashing(input, result.getDefaultState());
	}

	/**
	 * Registers an anvil smashing result to break the given block
	 * @param input Input block
	 */
	public static void registerAnvilBreaking(Block input) {
		registerAnvilSmashing(input, Blocks.AIR);
	}

	/**
	 * Registers an anvil smashing result to break the given material
	 * @param material Input material
	 */
	public static void registerAnvilBreaking(Material material) {
		anvilBreaking.add(material);
	}

	/**
	 * Gets the result of an anvil landing on the given blockstate
	 * @param state Input blockstate
	 * @return BlockState result. Will be air if its breaking, or null if there is no behavior
	 */
	public static BlockState getAnvilSmashResult(BlockState state) {
		if (anvilSmashing.containsKey(state)) {
			return anvilSmashing.get(state);
		}
		Block block = state.getBlock();
		if (anvilSmashingBlocks.containsKey(block)) {
			return anvilSmashingBlocks.get(block);
		}
		if (anvilBreaking.contains(state.getMaterial())) {
			return Blocks.AIR.getDefaultState();
		}
		return null;
	}

	/**
	 * Checks if we have a state specific smashing result. Used for JEI to filter out the lists for blocks
	 * @param state State to check
	 * @return True if we have a state specific result
	 */
	public static boolean hasAnvilSmashStateResult(BlockState state) {
		return anvilSmashing.containsKey(state);
	}

	/**
	 * Gets all smashing recipes in the form of blockstate to blockstate
	 * @return List of map entries for the recipes
	 */
	public static List<Map.Entry<BlockState, BlockState>> getAllAnvilStateSmashing() {
		return ImmutableList.copyOf(anvilSmashing.entrySet());
	}

	/**
	 * Gets all smashing recipes in the form of block to blockstate
	 * @return List of map entries for the recipes
	 */
	public static List<Map.Entry<Block, BlockState>> getAllAnvilBlockSmashing() {
		return ImmutableList.copyOf(anvilSmashingBlocks.entrySet());
	}


	/*
	 * Cauldron recipes
	 */
	private static List<ICauldronRecipe> cauldronRecipes = new ArrayList<>();
	private static Set<Item> cauldronBlacklist = new HashSet<>();

	/**
	 * Gets the result of a cauldron recipe
	 * @param input   ItemStack input
	 * @param boiling Whether the cauldron is boiling
	 * @return Result of the recipe
	 */
	public static ICauldronRecipe getCauldronResult(ItemStack input, boolean boiling, int level, CauldronState state) {
		for (ICauldronRecipe recipe : cauldronRecipes) {
			if (recipe.matches(input, boiling, level, state)) {
				return recipe;
			}
		}
		return null;
	}

	/**
	 * Adds a new cauldron recipe
	 * @param recipe Recipe to add
	 */
	public static void addCauldronRecipe(ICauldronRecipe recipe) {
		if (new RegisterCauldronRecipe(recipe).fire()) {
			cauldronRecipes.add(recipe);
		} else {
			log.debug("Cauldron recipe '{}' canceled by event", recipe);
		}
	}

	/**
	 * Adds a new cauldron recipe
	 * @param input   ItemStack to check for
	 * @param output  Recipe output
	 * @param boiling Whether the cauldron must be boiling or not
	 */
	public static void addCauldronRecipe(ItemStack input, ItemStack output, Boolean boiling) {
		addCauldronRecipe(new CauldronFluidRecipe(RecipeMatch.of(input), output, boiling));
	}

	/**
	 * Adds a fluid transform cauldron recipe with a variant for one layer and for 3
	 * @param stack   Input stack. Stack size will be checked, and doubled for a cauldron with more than one bottle
	 * @param input   Input fluid
	 * @param output  Output fluid
	 * @param boiling Whether the cauldron must be boiling or not. If null, the cauldron being boiling is ignored
	 */
	public static void addCauldronScaledTransformRecipe(ItemStack stack, Fluid input, Fluid output, Boolean boiling) {
		addCauldronRecipe(new CauldronFluidTransformRecipe(RecipeMatch.of(stack, stack.getCount(), 1), input, output, boiling, 1));
		stack = stack.copy();
		int count = stack.getCount();
		stack.setCount(count * 2);
		if (cauldronBigger) {
			addCauldronRecipe(new CauldronFluidTransformRecipe(RecipeMatch.of(stack, stack.getCount(), 1), input, output, boiling, 2));
			stack = stack.copy();
			stack.setCount(count * 3);
		}
		addCauldronRecipe(new CauldronFluidTransformRecipe(RecipeMatch.of(stack, stack.getCount(), 1), input, output, boiling, cauldronBigger ? 4 : 3));
	}

	/**
	 * Adds a item to empty into and fill from the cauldron
	 * @param filled    Filled version of container
	 * @param container Empty version of container
	 * @param fluid     Fluid contained
	 * @param amount    Amount this container counts for
	 */
	public static void addCauldronFluidItem(ItemStack filled, ItemStack container, Fluid fluid, int amount) {
		addCauldronRecipe(new FillCauldronRecipe(RecipeMatch.of(filled), fluid, amount, container.copy()));
		addCauldronRecipe(new CauldronFluidRecipe(RecipeMatch.of(container), fluid, filled.copy(), null, amount, SoundEvents.ITEM_BOTTLE_FILL));
	}

	/**
	 * Adds a item to empty into and fill from the cauldron
	 * @param filled    Filled version of container
	 * @param container Empty version of container
	 * @param fluid     Fluid contained
	 */
	public static void addCauldronFluidItem(ItemStack filled, ItemStack container, Fluid fluid) {
		addCauldronFluidItem(filled, container, fluid, 1);
	}

	/**
	 * Gets all cauldron recipes
	 * @return A list of all cauldron recipes
	 */
	public static List<ICauldronRecipe> getAllCauldronRecipes() {
		return ImmutableList.copyOf(cauldronRecipes);
	}

	/**
	 * Adds an item to the cauldron blacklist, preventing its normal cauldron interaction
	 * @param item Item to add
	 */
	public static void addCauldronBlacklist(Item item) {
		cauldronBlacklist.add(item);
	}

	/**
	 * Checks if an item is blacklisted from its normal cauldron interaction
	 * @param stack ItemStack to check
	 * @return True if the item is blacklisted
	 */
	public static boolean isCauldronBlacklist(ItemStack stack) {
		// check both the item with its current meta and with wildcard meta
		return cauldronBlacklist.contains(stack.getItem());
	}


	/*
	 * Cauldron properties
	 */
	private static boolean cauldronBigger = false, expensiveCauldronBrewing = false;
	private static Set<Fluid> cauldronWater = new HashSet<>();
	private static Map<Block, CauldronState> cauldronBlockStates = new HashMap<>();
	private static Map<CauldronState, BlockState> cauldronFullStates = new HashMap<>();

	/**
	 * Returns the maximum size for the cauldron
	 * @return 4 if bigger cauldron, 3 otherwise
	 */
	public static int getCauldronMax() {
		return cauldronBigger ? 4 : 3;
	}

	/**
	 * Returns if cauldron brewing is more expensive
	 * @return True if cauldron brewing is more expensive
	 */
	public static boolean expensiveCauldronBrewing() {
		return expensiveCauldronBrewing;
	}

	/**
	 * Adds the given fluid as cauldron water. Used for rain and fire checks among a few other things
	 * @param fluid Fluid to add
	 */
	public static void addCauldronWater(Fluid fluid) {
		cauldronWater.add(fluid);
	}

	/**
	 * Checks if this fluid is considered water in the cauldron. Means it is a valid base for some recipes
	 * @param fluid Fluid to check
	 * @return True if the fluid is considered water
	 */
	public static boolean isCauldronWater(Fluid fluid) {
		return fluid != null && cauldronWater.contains(fluid);
	}

	/**
	 * Checks if a state is considered fire in a cauldron
	 * @param state State to check
	 * @return True if the state is considered fire
	 */
	public static boolean isCauldronFire(BlockState state) {
		if (state.getBlock().isIn(TAG_CAULDRON_FIRE)) {
			return true;
		}
		if (state.getBlock() == Blocks.CAMPFIRE && state.get(CampfireBlock.LIT)) {
			return true;
		}
		return false;
	}

	/**
	 * Internal method to add the normal cauldron block, just needs to run after the substitution
	 */
	public static void registerDefaultCauldron() {
		cauldronBlockStates.put(Blocks.CAULDRON, CauldronState.WATER);
	}

	/**
	 * Links a block state to a full iron cauldron state, should not be used for items that are not an iron cauldron
	 * @param state         Block state being registered
	 * @param cauldronState State the cauldron is filled with
	 */
	public static void registerFullCauldron(BlockState state, CauldronState cauldronState) {
		cauldronBlockStates.put(state.getBlock(), cauldronState);
		cauldronFullStates.put(cauldronState, state);
	}

	/**
	 * Checks if a block is a normal iron cauldron
	 * @param state State to check
	 * @return True if its a normal cauldron, false otherwise
	 */
	public static boolean isNormalCauldron(BlockState state) {
		return cauldronBlockStates.containsKey(state.getBlock());
	}

	/**
	 * Gets the state for the given cauldron
	 * @param state Block state instance
	 * @return Cauldron state for the given block state
	 */
	public static CauldronState getCauldronState(BlockState state) {
		Block block = state.getBlock();
		if (cauldronBlockStates.containsKey(block)) {
			return cauldronBlockStates.get(block);
		}
		throw new IllegalArgumentException("Attempted to get state of a cauldron that is not registered");
	}

	/**
	 * Checks if a given cauldron state has a full cauldron
	 * @param state Cauldron state to check
	 * @return True if it has a full cauldron
	 */
	public static boolean hasFullCauldron(CauldronState state) {
		return cauldronFullStates.containsKey(state);
	}

	/**
	 * Gets the full cauldron for a given state
	 * @param state Cauldron state to check
	 * @return Full cauldron block state
	 */
	@Nullable
	public static BlockState getFullCauldron(CauldronState state) {
		return cauldronFullStates.get(state);
	}
}
