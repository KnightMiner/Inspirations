package knightminer.inspirations.library.recipe.anvil;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;

import java.util.List;

/**
 * Base interface for all anvil recipes. Contains all methods required to determine new state, itemstack, and height
 * based on the recipe
 * <p>
 * Parameters are considered stateless and generally should not modify the input stack except in the case of
 * transformInput()
 */
public interface IAnvilRecipe {
	/**
	 * Checks if the recipe matches the given input
	 *
	 * @param stack  Input stack
	 * @param height Fall height of the anvil
	 * @param state  State of the block the anvil landed on
	 * @return true if the recipe matches
	 */
	boolean matches(ItemStack stack, int height, IBlockState state);

	/**
	 * Transforms the input itemstack for the recipe.
	 *
	 * @param stack  Input stack to transform
	 * @param height Fall height of the anvil
	 * @param state  State of the block the anvil landed on
	 * @return
	 */
	List<ItemStack> transformInput(ItemStack stack, int height, IBlockState state);

	/**
	 * Get the resulting block state for this recipe
	 *
	 * @param stack  Input stack
	 * @param height Fall height of the anvil
	 * @param state  State of the block the anvil landed on
	 * @return new block state
	 */
	default IBlockState getState(ItemStack stack, int height, IBlockState state) {
		return state;
	}
}
