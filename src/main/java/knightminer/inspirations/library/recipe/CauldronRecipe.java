package knightminer.inspirations.library.recipe;

import knightminer.inspirations.library.Util;
import net.minecraft.item.ItemStack;
import slimeknights.mantle.util.RecipeMatch;

public class CauldronRecipe {
	private RecipeMatch input;
	private ItemStack result;
	private boolean boiling;

	public CauldronRecipe(RecipeMatch input, ItemStack result, boolean boiling) {
		this.input = input;
		this.result = result;
		this.boiling = boiling;
	}

	/**
	 * Checks if the recipe matches the given input
	 * @param input    Input item stack
	 * @param boiling  Whether the cauldron is boiling
	 * @return  True if it matches, false otherwise
	 */
	public boolean matches(ItemStack input, boolean boiling) {
		// if boiling is required, ensure it is set
		if(this.boiling && !boiling) {
			return false;
		}
		return this.input.matches(Util.createNonNullList(input)).isPresent();
	}

	/**
	 * Gets the result of this recipe
	 * @param input      Input stack
	 * @param boiling  Whether the water is boiling
	 * @return  Copy of result stack
	 */
	public ItemStack getResult(ItemStack input, boolean boiling) {
		return getResult().copy();
	}

	/**
	 * Gets the input of the recipe for display in JEI
	 * @return  Recipe input
	 */
	public RecipeMatch getInput() {
		return input;
	}

	/**
	 * Gets the result of this recipe for display in JEI
	 * @return  Recipe result
	 */
	public ItemStack getResult() {
		return result;
	}

	/**
	 * Gets the result of this recipe for display in JEI
	 * @return  Recipe result
	 */
	public boolean isBoiling() {
		return boiling;
	}
}
