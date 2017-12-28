package knightminer.inspirations.library.recipe.cauldron;

import java.util.List;

import net.minecraft.item.ItemStack;

/**
 * This is any cauldron recipe simple enough to be displayed in JEI
 */
public interface ISimpleCauldronRecipe extends ICauldronRecipe {
	/**
	 * Gets the inputs of the recipe for display in JEI
	 * @return  Recipe inputs
	 */
	List<ItemStack> getInput();

	/**
	 * Gets the result of this recipe for display in JEI
	 * @return  Recipe result
	 */
	ItemStack getResult();

	/**
	 * Gets whether this recipe is boiling
	 * @return  True if the recipe is boiling
	 */
	default boolean isBoiling() {
		return false;
	};

	/**
	 * Gets the state result of this recipe for display in JEI
	 * @return  Result state
	 */
	CauldronState getState();

	/**
	 * Gets the initial state of this recipe
	 * @return  Result state
	 */
	default CauldronState getInitialState() {
		return getState();
	};

	/**
	 * Gets the display starting level for display in JEI. The result will pass this into getLevel()
	 * @return
	 */
	default int getStartLevel() {
		return 3;
	}

	@Override
	default ItemStack getResult(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return getResult().copy();
	}
}
