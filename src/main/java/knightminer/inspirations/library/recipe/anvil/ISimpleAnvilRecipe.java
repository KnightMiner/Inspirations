package knightminer.inspirations.library.recipe.anvil;

import net.minecraft.block.state.IBlockState;
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
	default ItemStack getResult() {
		return ItemStack.EMPTY;
	}

	/**
	 * Gets whether this recipe requires the cauldron to be above fire
	 *
	 * @return True if the recipe requires fire
	 */
	default boolean isBoiling() {
		return false;
	}

	/**
	 * Gets the input block state of this recipe for display in JEI.
	 *
	 * @return Input block state
	 */
	default Object getInputState() {
		return null;
	}

	/**
	 * Gets the result block state of this recipe for display in JEI.
	 *
	 * @return Result state as a Fluid, EnumDyeColor, or PotionType
	 */
	default Object getState() {
		return getInputState();
	}

	/**
	 * Gets the fall height requirement for display in JEI.
	 *
	 * @return
	 */
	default Integer getFallHeight() {
		return null;
	}

	@Override default ItemStack getResult(ItemStack stack, int height, IBlockState state) {
		return getResult().copy();
	}

}
