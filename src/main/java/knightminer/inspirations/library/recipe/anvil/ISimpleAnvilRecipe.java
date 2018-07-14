package knightminer.inspirations.library.recipe.anvil;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Optional;

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
	 * @return Input block state, returns empty if there is no requirement
	 */
	default Optional<IBlockState> getState() {
		return Optional.empty();
	}

	/**
	 * Gets the fall height requirement for display in JEI.
	 *
	 * @return the required fall height, returns empty if there is no requirement
	 */
	default Optional<Integer> getFallHeight() {
		return Optional.empty();
	}

}
