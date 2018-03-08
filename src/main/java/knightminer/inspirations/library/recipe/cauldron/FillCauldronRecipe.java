package knightminer.inspirations.library.recipe.cauldron;

import javax.annotation.ParametersAreNonnullByDefault;

import knightminer.inspirations.library.Util;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.Fluid;
import slimeknights.mantle.util.RecipeMatch;

/**
 * Cauldron recipe to fill the cauldron from a fluid container
 */
@ParametersAreNonnullByDefault
public class FillCauldronRecipe extends CauldronFluidRecipe {

	private int amount;

	/**
	 * Full constructor
	 * @param input       Input container
	 * @param fluid       Fluid produced and required in the current cauldron
	 * @param amount      Amount of fluid produced
	 * @param container   Resulting container after filling the cauldron
	 * @param sound       Sound to play when filling
	 */
	public FillCauldronRecipe(RecipeMatch input, Fluid fluid, int amount, ItemStack container, SoundEvent sound) {
		super(input, fluid, container, null, 0, sound);
		this.amount = amount;
	}

	/**
	 * Constructor with default sound
	 * @param input       Input container
	 * @param fluid       Fluid produced and required in the current cauldron
	 * @param amount      Amount of fluid produced
	 * @param container   Resulting container after filling the cauldron
	 */
	public FillCauldronRecipe(RecipeMatch input, Fluid fluid, int amount, ItemStack container) {
		this(input, fluid, amount, container, SoundEvents.ITEM_BOTTLE_EMPTY);
	}

	/**
	 * Constructor with default sound and empty container
	 * @param input       Input container
	 * @param fluid       Fluid produced and required in the current cauldron
	 * @param amount      Amount of fluid produced
	 */
	public FillCauldronRecipe(RecipeMatch input, Fluid fluid, int amount) {
		this(input, fluid, amount, ItemStack.EMPTY);
	}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		if(level == 3 || (level > 0 && !state.matches(fluid))) {
			return false;
		}

		return this.input.matches(Util.createNonNullList(stack)).isPresent();
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
	public String toString() {
		return String.format("FillCauldronRecipe: filling with %s", fluid.getFluid().getName());
	}
}
