package knightminer.inspirations.library.recipe.anvil;

import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * This is any anvil recipe using items simple enough to be displayed in JEI
 */
public interface ISimpleAnvilRecipe extends IAnvilRecipe {
	/**
	 * Gets the inputs of the recipe for display in JEI
	 *
	 * @return Recipe inputs
	 */
	List<ItemStack> getInput();

	/**
	 * Gets the result of this recipe for display in JEI
	 *
	 * @return Recipe result
	 */
	List<ItemStack> getResult();

	/**
	 * Gets the input block state of this recipe for display in JEI.
	 *
	 * @return Input block state
	 */
	default Object getInputState() {
		return null;
	}

	/**
	 * Gets the fall height requirement for display in JEI.
	 *
	 * @return
	 */
	default Integer getFallHeight() {
		return null;
	}

}
