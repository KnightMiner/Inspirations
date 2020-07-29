package knightminer.inspirations.library.recipe.cauldron;

import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import slimeknights.mantle.util.RecipeMatch;

import javax.annotation.Nullable;

/**
 * Recipe to transform an item stack using a fluid
 */
@Deprecated
public class PotionCauldronRecipe extends RecipeMatchCauldronRecipe {

	protected Potion potion;

	/**
	 * @param input   Input recipe match entry
	 * @param potion  Required potion in the cauldron
	 * @param result  Resulting item stack
	 * @param levels  Amount of levels consumed by the recipe, also determines how many levels are required to perform the recipe
	 * @param boiling If true, the cauldron must be above fire, if false it must not be above fire. Set to null to ignore fire
	 */
	public PotionCauldronRecipe(RecipeMatch input, Potion potion, ItemStack result, int levels, @Nullable Boolean boiling) {
		super(input, result, boiling, levels);
		this.potion = potion;
	}

	@Override
	protected boolean matches(CauldronState state) {
		return state.getPotion() == potion;
	}

	@Override
	public Object getInputState() {
		return potion == Potions.WATER ? Fluids.WATER : potion;
	}

	@Override
	public String toString() {
		return String.format("FluidCauldronRecipe: %s from %s", getResult().toString(), potion.getRegistryName());
	}
}
