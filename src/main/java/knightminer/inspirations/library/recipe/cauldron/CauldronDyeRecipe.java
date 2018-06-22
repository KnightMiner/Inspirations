package knightminer.inspirations.library.recipe.cauldron;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import knightminer.inspirations.library.Util;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.mantle.util.RecipeMatch;

/**
 * Cauldron recipe to dye an item stack from colored cauldron water
 */
@ParametersAreNonnullByDefault
public class CauldronDyeRecipe implements ISimpleCauldronRecipe {

	private RecipeMatch input;
	private ItemStack result;
	private EnumDyeColor color;
	private int levels;

	/**
	 * @param input  Input recipe match entry
	 * @param color  Color to test for in the cauldron
	 * @param result Resulting item stack
	 */
	public CauldronDyeRecipe(RecipeMatch input, EnumDyeColor color, ItemStack result, int levels) {
		this.input = input;
		this.result = result;
		this.color = color;
		this.levels = levels;
	}

	public CauldronDyeRecipe(RecipeMatch input, EnumDyeColor color, ItemStack result) {
		this(input, color, result, 1);
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
		return level >= levels && state.getColor() == color.colorValue
				&& input.matches(Util.createNonNullList(stack)).isPresent();
	}

	@Override
	public ItemStack transformInput(ItemStack stack, boolean boiling, int level, CauldronState state) {
		NonNullList<ItemStack> list = Util.createNonNullList(stack);
		RecipeMatch.removeMatch(list, input.matches(list).get());
		return stack;
	}

	@Override
	public int getLevel(int level) {
		return level - levels;
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
		return levels == 0 ? 1 : levels;
	}

	@Override
	public String toString() {
		return String.format("CauldronDyeRecipe: %s dyed %s", result.toString(), color.getName());
	}
}
