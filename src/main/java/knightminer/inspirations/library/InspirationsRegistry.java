package knightminer.inspirations.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.event.RegisterEvent.RegisterCauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.CauldronFluidRecipe;
import knightminer.inspirations.library.recipe.cauldron.CauldronFluidTransformRecipe;
import knightminer.inspirations.library.recipe.cauldron.FillCauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe.CauldronState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockLilyPad;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.oredict.OreDictionary;
import slimeknights.mantle.util.RecipeMatch;

public class InspirationsRegistry {
	public static final Logger log = Util.getLogger("api");
	private static boolean cauldronBigger = false, expensiveCauldronBrewing = false;

	/**
	 * Sets a value from the Inspirations config into the registry. Used to keep the config out of the library
	 * @param key    Key to set
	 * @param value  Boolean value
	 * @deprecated  For internal use only
	 */
	@Deprecated
	public static void setConfig(String key, boolean value) {
		switch(key) {
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
	 * Flowers
	 */
	private static Map<ItemMetaKey,Boolean> flowers = new HashMap<>();

	/**
	 * Checks if the given item stack is a flower
	 * @param stack  Input stack
	 * @return  True if its a flower
	 */
	public static boolean isFlower(ItemStack stack) {
		return flowers.computeIfAbsent(new ItemMetaKey(stack), InspirationsRegistry::isFlower);
	}

	/**
	 * Helper function to check if a stack is a flower, basically to implement vanilla logic, but slightly extended
	 * @param key  Item meta combination
	 * @return  True if it is a flower
	 */
	private static boolean isFlower(ItemMetaKey key) {
		Block block = Block.getBlockFromItem(key.getItem());
		return block instanceof BlockBush
				&& !(block instanceof BlockDoublePlant)
				&& !(block instanceof BlockTallGrass)
				&& !(block instanceof BlockCrops)
				&& !(block instanceof BlockLilyPad);
	}

	/**
	 * Registers an override to state a stack is definitely a flower or not a flower, primarily used by the config
	 * @param stack     ItemStack which is a flower
	 * @param isFlower  True if its a flower, false if its not a flower
	 */
	public static void registerFlower(ItemStack stack, boolean isFlower) {
		flowers.put(new ItemMetaKey(stack), isFlower);
	}

	/**
	 * Registers an override to state a stack is definitely a flower or not a flower, primarily used by the config
	 * @param block      Block which is a flower
	 * @param meta      Meta which is a flower
	 * @param isFlower  True if its a flower, false if its not a flower
	 */
	public static void registerFlower(Block block, int meta, boolean isFlower) {
		Item item = Item.getItemFromBlock(block);
		if(item != Items.AIR) {
			flowers.put(new ItemMetaKey(item, meta), isFlower);
		}
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
	private static Set<ItemMetaKey> cauldronBlacklist = new HashSet<>();
	private static Set<Fluid> cauldronWater = new HashSet<>();
	private static Set<Block> cauldronFireBlocks = new HashSet<>();
	private static Set<IBlockState> cauldronFireStates = new HashSet<>();

	/**
	 * Returns the maximum size for the cauldron
	 * @return  4 if bigger cauldron, 3 otherwise
	 */
	public static int getCauldronMax() {
		return cauldronBigger ? 4 : 3;
	}

	/**
	 * Returns if cauldron brewing is more expensive
	 * @return  True if cauldron brewing is more expensive
	 */
	public static boolean expensiveCauldronBrewing() {
		return expensiveCauldronBrewing;
	}

	/**
	 * Gets the result of a cauldron recipe
	 * @param input      ItemStack input
	 * @param isBoiling  Whether the cauldron is boiling
	 * @return  Result of the recipe
	 */
	public static ICauldronRecipe getCauldronResult(ItemStack input, boolean boiling, int level, CauldronState state) {
		for(ICauldronRecipe recipe : cauldronRecipes) {
			if(recipe.matches(input, boiling, level, state)) {
				return recipe;
			}
		}
		return null;
	}

	/**
	 * Adds a new cauldron recipe
	 * @param recipe  Recipe to add
	 */
	public static void addCauldronRecipe(ICauldronRecipe recipe) {
		if(new RegisterCauldronRecipe(recipe).fire()) {
			cauldronRecipes.add(recipe);
		} else {
			log.debug("Cauldron recipe '{}' canceled by event", recipe);
		}
	}

	/**
	 * Adds a new cauldron recipe
	 * @param input      ItemStack to check for
	 * @param output     Recipe output
	 * @param boiling  Whether the cauldron must be boiling or not
	 */
	public static void addCauldronRecipe(ItemStack input, ItemStack output, Boolean boiling) {
		addCauldronRecipe(new CauldronFluidRecipe(RecipeMatch.of(input), output, boiling));
	}

	/**
	 * Adds a new cauldron recipe
	 * @param input      oreDict name to check for
	 * @param output     Recipe output
	 * @param boiling  Whether the cauldron must be boiling or not
	 */
	public static void addCauldronRecipe(String input, ItemStack output, Boolean boiling) {
		addCauldronRecipe(new CauldronFluidRecipe(RecipeMatch.of(input), output, boiling));
	}

	/**
	 * Adds a fluid transform cauldron recipe with a variant for one layer and for 3
	 * @param stack      Input stack. Stack size will be checked, and doubled for a cauldron with more than one bottle
	 * @param input      Input fluid
	 * @param output     Output fluid
	 * @param boiling  Whether the cauldron must be boiling or not. If null, the cauldron being boiling is ignored
	 */
	public static void addCauldronScaledTransformRecipe(ItemStack stack, Fluid input, Fluid output, Boolean boiling) {
		addCauldronRecipe(new CauldronFluidTransformRecipe(RecipeMatch.of(stack, stack.getCount(), 1), input, output, boiling, 1));
		stack = stack.copy();
		int count = stack.getCount();
		stack.setCount(count * 2);
		if(Config.enableBiggerCauldron) {
			addCauldronRecipe(new CauldronFluidTransformRecipe(RecipeMatch.of(stack, stack.getCount(), 1), input, output, boiling, 2));
			stack = stack.copy();
			stack.setCount(count * 3);
		}
		addCauldronRecipe(new CauldronFluidTransformRecipe(RecipeMatch.of(stack, stack.getCount(), 1), input, output, boiling, (Config.enableBiggerCauldron ? 4 : 3)));
	}

	/**
	 * Adds a item to empty into and fill from the cauldron
	 * @param filled     Filled version of container
	 * @param container  Empty version of container
	 * @param fluid      Fluid contained
	 * @param amount     Amount this container counts for
	 */
	public static void addCauldronFluidItem(ItemStack filled, ItemStack container, Fluid fluid, int amount) {
		addCauldronRecipe(new FillCauldronRecipe(RecipeMatch.of(filled), fluid, amount, container.copy()));
		addCauldronRecipe(new CauldronFluidRecipe(RecipeMatch.of(container), fluid, filled.copy(), null, amount, SoundEvents.ITEM_BOTTLE_FILL));
	}

	/**
	 * Adds a item to empty into and fill from the cauldron
	 * @param filled     Filled version of container
	 * @param container  Empty version of container
	 * @param fluid      Fluid contained
	 */
	public static void addCauldronFluidItem(ItemStack filled, ItemStack container, Fluid fluid) {
		addCauldronFluidItem(filled, container, fluid, 1);
	}

	/**
	 * Gets all cauldron recipes
	 * @return  A list of all cauldron recipes
	 */
	public static List<ICauldronRecipe> getAllCauldronRecipes() {
		return ImmutableList.copyOf(cauldronRecipes);
	}

	/**
	 * Adds the given fluid as cauldron water. Used for rain and fire checks among a few other things
	 * @param fluid  Fluid to add
	 */
	public static void addCauldronWater(Fluid fluid) {
		cauldronWater.add(fluid);
	}

	/**
	 * Checks if this fluid is considered water in the cauldron.
	 * Used for rain checks along with some recipe transformations
	 * @param fluid  Fluid to check
	 * @return  True if the fluid is considered water
	 */
	public static boolean isCauldronWater(Fluid fluid) {
		return fluid != null && cauldronWater.contains(fluid);
	}

	/**
	 * Adds an item to the cauldron blacklist, preventing its normal cauldron interaction
	 * @param item  Item to add
	 * @param meta  Metadata to use, supports wildcard
	 */
	public static void addCauldronBlacklist(Item item, int meta) {
		cauldronBlacklist.add(new ItemMetaKey(item, meta));
	}

	/**
	 * Checks if an item is blacklisted from its normal cauldron interaction
	 * @param stack  ItemStack to check
	 * @return  True if the item is blacklisted
	 */
	public static boolean isCauldronBlacklist(ItemStack stack) {
		// check both the item with its current meta and with wildcard meta
		return cauldronBlacklist.contains(new ItemMetaKey(stack))
				|| cauldronBlacklist.contains(new ItemMetaKey(stack.getItem(), OreDictionary.WILDCARD_VALUE));
	}

	/**
	 * Registers all variants of a block as acting as fire to heat a cauldron
	 * @param block  Block to register
	 */
	public static void registerCauldronFire(Block block) {
		cauldronFireBlocks.add(block);
	}

	/**
	 * Registers a single block state to act as fire for a cauldron
	 * @param block  Block state to register
	 */
	public static void registerCauldronFire(IBlockState block) {
		cauldronFireStates.add(block);
	}

	/**
	 * Checks if a state is considered fire in a cauldron
	 * @param state  State to check
	 * @return  True if the state is considered fire
	 */
	public static boolean isCauldronFire(IBlockState state) {
		return cauldronFireBlocks.contains(state.getBlock()) || cauldronFireStates.contains(state);
	}
}
