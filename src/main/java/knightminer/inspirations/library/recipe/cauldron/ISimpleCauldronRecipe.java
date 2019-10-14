package knightminer.inspirations.library.recipe.cauldron;

import java.util.List;

import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;

/**
 * This is any cauldron recipe using items and fluids simple enough to be displayed in JEI
 */
public interface ISimpleCauldronRecipe extends ICauldronRecipe {
	/**
	 * Gets the inputs of the recipe for display in JEI
	 * @return Recipe inputs
	 */
	List<ItemStack> getInput();

	/**
	 * Gets the result of this recipe for display in JEI
	 * @return Recipe result
	 */
	default ItemStack getResult() {
		return ItemStack.EMPTY;
	}

	/**
	 * Gets whether this recipe requires the cauldron to be above fire
	 * @return True if the recipe requires fire
	 */
	default boolean isBoiling() {
		return false;
	}

	/**
	 * Gets the input state of this recipe for display in JEI. In order for it to be used, it must be either a Fluid, EnumDyeColor, or PotionType
	 * @return Input state as a Fluid, EnumDyeColor, or PotionType
	 */
	default Object getInputState() {
		return Fluids.WATER;
	}

	/**
	 * Gets the result state of this recipe for display in JEI. In order for it to be used, it must be either a Fluid, EnumDyeColor, or PotionType
	 * @return Result state as a Fluid, EnumDyeColor, or PotionType
	 */
	default Object getState() {
		return getInputState();
	}

	/**
	 * Gets the display starting level for display in JEI. The result level will be determined using {@link #getLevel(int)}
	 * @return
	 */
	default int getInputLevel() {
		return InspirationsRegistry.getCauldronMax();
	}

	@Override
	default ItemStack getResult(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return getResult().copy();
	}
}
