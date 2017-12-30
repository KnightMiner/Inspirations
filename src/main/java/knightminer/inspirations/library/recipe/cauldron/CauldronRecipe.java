package knightminer.inspirations.library.recipe.cauldron;

import java.util.List;

import knightminer.inspirations.library.Util;
import net.minecraft.item.ItemStack;
import slimeknights.mantle.util.RecipeMatch;

public class CauldronRecipe implements ISimpleCauldronRecipe {
	private RecipeMatch input;
	private ItemStack result;
	private Boolean boiling;

	public CauldronRecipe(RecipeMatch input, ItemStack result, Boolean boiling) {
		this.input = input;
		this.result = result;
		this.boiling = boiling;
	}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		// if boiling is required, ensure it is set
		if(state.matches(CauldronState.WATER) && this.boiling != null && boiling != this.boiling.booleanValue()) {
			return false;
		}

		return this.input.matches(Util.createNonNullList(stack)).isPresent();
	}

	@Override
	public List<ItemStack> getInput() {
		return input.getInputs();
	}

	@Override
	public ItemStack getResult() {
		return result;
	}

	@Override
	public int getLevel(int level) {
		return level - 1;
	}

	@Override
	public boolean isBoiling() {
		return boiling;
	}
}
