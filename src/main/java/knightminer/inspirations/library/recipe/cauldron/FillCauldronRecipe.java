package knightminer.inspirations.library.recipe.cauldron;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import slimeknights.mantle.util.RecipeMatch;

/**
 * Cauldron recipe to fill the cauldron from a fluid container
 */
@ParametersAreNonnullByDefault
public class FillCauldronRecipe extends CauldronFluidRecipe {

	private int amount;

	/**
	 * Full constructor
	 * @param input     Input container
	 * @param fluid     Fluid produced and required in the current cauldron
	 * @param amount    Amount of fluid produced
	 * @param container Resulting container after filling the cauldron
	 * @param boiling   If true, the cauldron must be placed above lava for this recipe
	 * @param sound     Sound to play when filling
	 */
	public FillCauldronRecipe(RecipeMatch input, Fluid fluid, int amount, ItemStack container, @Nullable Boolean boiling, SoundEvent sound) {
		super(input, fluid, container, boiling, 0, sound);
		this.amount = amount;
	}

	/**
	 * Full constructor
	 * @param input     Input container
	 * @param fluid     Fluid produced and required in the current cauldron
	 * @param amount    Amount of fluid produced
	 * @param container Resulting container after filling the cauldron
	 * @param sound     Sound to play when filling
	 */
	public FillCauldronRecipe(RecipeMatch input, Fluid fluid, int amount, ItemStack container, SoundEvent sound) {
		this(input, fluid, amount, container, null, sound);
	}

	/**
	 * Constructor with default sound
	 * @param input     Input container
	 * @param fluid     Fluid produced and required in the current cauldron
	 * @param amount    Amount of fluid produced
	 * @param container Resulting container after filling the cauldron
	 */
	public FillCauldronRecipe(RecipeMatch input, Fluid fluid, int amount, ItemStack container) {
		this(input, fluid, amount, container, SoundEvents.ITEM_BOTTLE_EMPTY);
	}

	/**
	 * Constructor with default sound and empty container
	 * @param input  Input container
	 * @param fluid  Fluid produced and required in the current cauldron
	 * @param amount Amount of fluid produced
	 */
	public FillCauldronRecipe(RecipeMatch input, Fluid fluid, int amount) {
		this(input, fluid, amount, ItemStack.EMPTY);
	}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		if(level == InspirationsRegistry.getCauldronMax() || (level > 0 && !state.matches(fluid))
				|| (this.boiling != null && boiling != this.boiling.booleanValue())) {
			return false;
		}

		return matches(stack);
	}

	@Override
	public CauldronState getState(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return fluid;
	}

	@Override
	public int getLevel(int level) {
		return level + amount;
	}

	@Override
	public int getInputLevel() {
		return 0;
	}

	@Override
	public ItemStack getContainer(ItemStack stack) {
		return ItemStack.EMPTY;
	}

	@Override
	public String toString() {
		return String.format("FillCauldronRecipe: filling with %s", fluid.getFluid().getAttributes().getTranslationKey());
	}
}
