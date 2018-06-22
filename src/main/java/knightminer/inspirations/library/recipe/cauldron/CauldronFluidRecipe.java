package knightminer.inspirations.library.recipe.cauldron;

import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import knightminer.inspirations.library.Util;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import slimeknights.mantle.util.RecipeMatch;

/**
 * Recipe to transform an item stack using a fluid
 */
@ParametersAreNonnullByDefault
public class CauldronFluidRecipe implements ISimpleCauldronRecipe {
	protected RecipeMatch input;
	private ItemStack result;
	@Nullable
	private Boolean boiling;
	@Nullable
	protected CauldronState fluid;
	private SoundEvent sound;
	private int levels;

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
		this.input = input;
		this.result = result;
		this.boiling = boiling;
		this.fluid = fluid == null ? null : CauldronState.fluid(fluid);
		this.sound = sound;
		this.levels = levels;
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
		this(input, fluid, result, boiling, levels, SoundEvents.ENTITY_BOBBER_SPLASH);
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
		this(input, fluid, result, boiling, SoundEvents.ENTITY_BOBBER_SPLASH);
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
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		// if boiling is required, ensure it is set
		if(level < levels || !stateMatches(state) || (this.boiling != null && boiling != this.boiling.booleanValue())) {
			return false;
		}

		return this.input.matches(Util.createNonNullList(stack)).isPresent();
	}

	protected boolean stateMatches(CauldronState state) {
		return fluid == null ? state.isWater() : fluid.getFluid() == state.getFluid();
	}

	@Override
	public ItemStack transformInput(ItemStack stack, boolean boiling, int level, CauldronState state) {
		NonNullList<ItemStack> list = Util.createNonNullList(stack);
		RecipeMatch.removeMatch(list, input.matches(list).get());
		return stack;
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
	public int getLevel(int level) {
		return level - levels;
	}

	@Override
	public int getInputLevel() {
		return levels == 0 ? 1 : levels;
	}

	@Override
	public boolean isBoiling() {
		return boiling == Boolean.TRUE;
	}

	@Override
	public Object getInputState() {
		return fluid == null ? FluidRegistry.WATER : fluid.getFluid();
	}

	/**
	 * Gets the sound to play when performing this recipe
	 * @return  Sound event
	 */
	@Override
	public SoundEvent getSound(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return sound;
	}

	@Override
	public String toString() {
		return String.format("CauldronFluidRecipe: %s from %s", result.toString(), fluid == null ? "water" : fluid.getFluid().getName());
	}
}
