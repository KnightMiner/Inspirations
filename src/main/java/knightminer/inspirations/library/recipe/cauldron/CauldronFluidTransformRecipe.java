package knightminer.inspirations.library.recipe.cauldron;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import slimeknights.mantle.util.RecipeMatch;

/**
 * Cauldron recipe to transform the fluid inside a cauldron into another fluid. Use primarily for soups, stews, and juices
 */
@ParametersAreNonnullByDefault
public class CauldronFluidTransformRecipe extends CauldronFluidRecipe {

	private CauldronState result;
	private int maxLevel;

	/**
	 * Constructor with default max level of 3
	 * @param input     Input item stack for transformation
	 * @param fluid     Input fluid. Use null for any "water
	 * @param result    Resulting fluid
	 * @param boiling   If true, cauldron must be above fire. If false, cauldron must not be above fire. Use null to ignore fire
	 */
	public CauldronFluidTransformRecipe(RecipeMatch input, @Nullable Fluid fluid, Fluid result, @Nullable Boolean boiling) {
		this(input, fluid, result, boiling, 3);
	}

	/**
	 * Full constructor
	 * @param input     Input item stack for transformation
	 * @param fluid     Input fluid. Use null for any "water"
	 * @param result    Resulting fluid
	 * @param boiling   If true, cauldron must be above fire. If false, cauldron must not be above fire. Use null to ignore fire
	 * @param maxLevel  Maximum level at which this recipe works. Used for some recipes which cost more for more full cauldrons
	 */
	public CauldronFluidTransformRecipe(RecipeMatch input, @Nullable Fluid fluid, Fluid result, @Nullable Boolean boiling, int maxLevel) {
		super(input, fluid, ItemStack.EMPTY, boiling);
		this.result = CauldronState.fluid(result);
		this.maxLevel = maxLevel;
	}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return level > 0 && level <= maxLevel && super.matches(stack, boiling, level, state);
	}

	@Override
	public int getLevel(int level) {
		return level;
	}

	@Override
	public CauldronState getState(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return result;
	}

	@Override
	public Object getState() {
		return result.getFluid();
	}

	@Override
	public int getInputLevel() {
		return maxLevel;
	}

	@Override
	public String toString() {
		return String.format("CauldronFluidTransformRecipe: %s from %s", result.getFluid().getName(), fluid == null ? "water" : fluid.getFluid().getName());
	}
}
