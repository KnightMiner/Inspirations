package knightminer.inspirations.library.recipe.cauldron;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import slimeknights.mantle.util.RecipeMatch;

/**
 * Cauldron recipe to dye an item stack from colored cauldron water
 */
@ParametersAreNonnullByDefault
public class CauldronDyeRecipe extends CauldronRecipeMatchRecipe {

	private DyeColor color;

	/**
	 * @param input  Input recipe match entry
	 * @param color  Color to test for in the cauldron
	 * @param result Resulting item stack
	 */
	public CauldronDyeRecipe(RecipeMatch input, DyeColor color, ItemStack result, int levels) {
		super(input, result, null, levels);
		this.color = color;
	}

	public CauldronDyeRecipe(RecipeMatch input, DyeColor color, ItemStack result) {
		this(input, color, result, 1);
	}

	/**
	 * @param input  Input item stack
	 * @param color  Color to test for in the cauldron
	 * @param result Resulting item stack
	 */
	public CauldronDyeRecipe(ItemStack input, DyeColor color, ItemStack result) {
		this(RecipeMatch.of(input), color, result);
	}

	@Override
	protected boolean matches(CauldronState state) {
		return state.getColor() == color.colorValue;
	}

	@Override
	public Object getInputState() {
		return color;
	}

	@Override
	public String toString() {
		return String.format("CauldronDyeRecipe: %s dyed %s", getResult().toString(), color.getName());
	}
}
