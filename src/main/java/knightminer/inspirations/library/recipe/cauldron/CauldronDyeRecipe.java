package knightminer.inspirations.library.recipe.cauldron;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import knightminer.inspirations.library.Util;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import slimeknights.mantle.util.RecipeMatch;

/**
 * Cauldron recipe to dye an item stack from colored cauldron water
 */
@ParametersAreNonnullByDefault
public class CauldronDyeRecipe implements ISimpleCauldronRecipe {

	private RecipeMatch input;
	private ItemStack result;
	private EnumDyeColor color;

	/**
	 * @param input  Input recipe match entry
	 * @param color  Color to test for in the cauldron
	 * @param result Resulting item stack
	 */
	public CauldronDyeRecipe(RecipeMatch input, EnumDyeColor color, ItemStack result) {
		this.input = input;
		this.result = result;
		this.color = color;
	}

	/**
	 * @param input  Input item stack
	 * @param color  Color to test for in the cauldron
	 * @param result Resulting item stack
	 */
	public CauldronDyeRecipe(ItemStack input, EnumDyeColor color, ItemStack result) {
		this(RecipeMatch.of(input), color, result);
	}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return state.getColor() == color.colorValue
				&& input.matches(Util.createNonNullList(stack)).isPresent();
	}

	@Override
	public int getLevel(int level) {
		return level - 1;
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
	public Object getInputState() {
		return color;
	}

	@Override
	public int getInputLevel() {
		return 1;
	}

	@Override
	public String toString() {
		return String.format("CauldronDyeRecipe: %s dyed %s", result.toString(), color.getName());
	}
}
