package knightminer.inspirations.library.recipe.cauldron;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraftforge.fluids.FluidRegistry;
import slimeknights.mantle.util.RecipeMatch;

/**
 * Recipe to transform an item stack using a fluid
 */
@ParametersAreNonnullByDefault
public class CauldronPotionRecipe extends CauldronRecipeMatchRecipe {

	protected PotionType potion;

	/**
	 * @param input    Input recipe match entry
	 * @param potion   Required potion in the cauldron
	 * @param result   Resulting item stack
	 * @param levels   Amount of levels consumed by the recipe, also determines how many levels are required to perform the recipe
	 * @param boiling  If true, the cauldron must be above fire, if false it must not be above fire. Set to null to ignore fire
	 */
	public CauldronPotionRecipe(RecipeMatch input, PotionType potion, ItemStack result, int levels, @Nullable Boolean boiling) {
		super(input, result, boiling, levels);
		this.potion = potion;
	}

	@Override
	protected boolean matches(CauldronState state) {
		return state.getPotion() == potion;
	}

	@Override
	public Object getInputState() {
		return potion == PotionTypes.WATER ? FluidRegistry.WATER : potion;
	}

	@Override
	public String toString() {
		return String.format("CauldronFluidRecipe: %s from %s", getResult().toString(), potion.getRegistryName());
	}
}
