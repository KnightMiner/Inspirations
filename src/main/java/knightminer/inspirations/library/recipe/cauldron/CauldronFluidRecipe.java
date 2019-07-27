package knightminer.inspirations.library.recipe.cauldron;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import slimeknights.mantle.util.RecipeMatch;

/**
 * Recipe to transform an item stack using a fluid
 */
@ParametersAreNonnullByDefault
public class CauldronFluidRecipe extends CauldronRecipeMatchRecipe {

	@Nullable
	protected CauldronState fluid;

	/**
	 * Full constructor
	 * @param input    Input recipe match entry
	 * @param fluid    Required fluid in the cauldron
	 * @param result   Resulting item stack
	 * @param boiling  If true, the cauldron must be above fire, if false it must not be above fire. Set to null to ignore fire
	 * @param levels   Amount of levels consumed by the recipe, also determines how many levels are required to perform the recipe
	 * @param sound    Sound to play when performing the recipe
	 */
	public CauldronFluidRecipe(RecipeMatch input, @Nullable Fluid fluid, ItemStack result, @Nullable Boolean boiling, int levels, SoundEvent sound) {
		super(input, result, boiling, levels, sound);
		this.fluid = fluid == null ? null : CauldronState.fluid(fluid);
	}

	/**
	 * Constructor with default sound
	 * @param input    Input recipe match entry
	 * @param fluid    Required fluid in the cauldron. If null, any "water" fluid is valid
	 * @param result   Resulting item stack
	 * @param boiling  If true, the cauldron must be above fire, if false it must not be above fire. Set to null to ignore fire
	 * @param levels   Amount of levels consumed by the recipe, also determines how many levels are required to perform the recipe
	 */
	public CauldronFluidRecipe(RecipeMatch input, @Nullable Fluid fluid, ItemStack result, @Nullable Boolean boiling, int levels) {
		this(input, fluid, result, boiling, levels, SoundEvents.ENTITY_FISHING_BOBBER_SPLASH);
	}

	/**
	 * Constructor with default levels consumed of 1
	 * @param input    Input recipe match entry
	 * @param fluid    Required fluid in the cauldron
	 * @param result   Resulting item stack
	 * @param boiling  If true, the cauldron must be above fire, if false it must not be above fire. Set to null to ignore fire
	 * @param sound    Sound to play when performing the recipe
	 */
	public CauldronFluidRecipe(RecipeMatch input, @Nullable Fluid fluid, ItemStack result, @Nullable Boolean boiling, SoundEvent sound) {
		this(input, fluid, result, boiling, 1, sound);
	}

	/**
	 * Constructor with default sound and default levels consumed
	 * @param input    Input recipe match entry
	 * @param fluid    Required fluid in the cauldron. If null, any "water" fluid is valid
	 * @param result   Resulting item stack
	 * @param boiling  If true, the cauldron must be above fire, if false it must not be above fire. Set to null to ignore fire
	 */
	public CauldronFluidRecipe(RecipeMatch input, @Nullable Fluid fluid, ItemStack result, @Nullable Boolean boiling) {
		this(input, fluid, result, boiling, SoundEvents.ENTITY_FISHING_BOBBER_SPLASH);
	}

	/**
	 * Constructor with default sound, levels consumed, and defaulting input fluid to any water
	 * @param input    Input recipe match entry
	 * @param result   Resulting item stack
	 * @param boiling  If true, the cauldron must be above fire, if false it must not be above fire. Set to null to ignore fire
	 */
	public CauldronFluidRecipe(RecipeMatch input, ItemStack result, @Nullable Boolean boiling) {
		this(input, null, result, boiling);
	}

	@Override
	protected boolean matches(CauldronState state) {
		return fluid == null ? state.isWater() : fluid.getFluid() == state.getFluid();
	}

	@Override
	public Object getInputState() {
		return fluid == null ? FluidRegistry.WATER : fluid.getFluid();
	}

	@Override
	public String toString() {
		return String.format("CauldronFluidRecipe: %s from %s", getResult().toString(), fluid == null ? "water" : fluid.getFluid().getName());
	}
}
