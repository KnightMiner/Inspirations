package knightminer.inspirations.library.recipe.cauldron;

import java.util.List;

import knightminer.inspirations.library.Util;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import slimeknights.mantle.util.RecipeMatch;

public class DyeCauldronRecipe implements ISimpleCauldronRecipe {

	private RecipeMatch input;
	private ItemStack result;
	private EnumDyeColor color;
	public DyeCauldronRecipe(RecipeMatch input, EnumDyeColor color, ItemStack result) {
		this.input = input;
		this.result = result;
		this.color = color;
	}

	public DyeCauldronRecipe(ItemStack input, EnumDyeColor color, ItemStack result) {
		this(RecipeMatch.of(input), color, result);
	}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return state.getType() == CauldronContents.DYE
				&& state.getColor() == color.colorValue
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
}
