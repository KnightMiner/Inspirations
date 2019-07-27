package knightminer.inspirations.library.recipe.cauldron;

import java.util.List;

import javax.annotation.Nullable;

import knightminer.inspirations.library.Util;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import slimeknights.mantle.util.RecipeMatch;

public abstract class CauldronRecipeMatchRecipe implements ISimpleCauldronRecipe {

	private RecipeMatch input;
	@Nullable
	protected Boolean boiling;
	private SoundEvent sound;
	private ItemStack result;
	private int levels;

	/**
	 * Base constructor
	 * @param input    Input recipe match entry
	 * @param result   Resulting item stack
	 * @param boiling  If true, the cauldron must be above fire, if false it must not be above fire. Set to null to ignore fire
	 * @param levels   Amount of levels consumed by the recipe, also determines how many levels are required to perform the recipe
	 * @param sound    Sound to play when performing the recipe
	 */
	public CauldronRecipeMatchRecipe(RecipeMatch input, ItemStack result, @Nullable Boolean boiling, int levels, SoundEvent sound) {
		this.input = input;
		this.result = result;
		this.levels = levels;
		this.boiling = boiling;
		this.sound = sound;
	}

	/**
	 * Constructor which defaults sound
	 * @param input    Input recipe match entry
	 * @param result   Resulting item stack
	 * @param boiling  If true, the cauldron must be above fire, if false it must not be above fire. Set to null to ignore fire
	 * @param levels   Amount of levels consumed by the recipe, also determines how many levels are required to perform the recipe
	 */
	public CauldronRecipeMatchRecipe(RecipeMatch input, ItemStack result, @Nullable Boolean boiling, int levels) {
		this(input, result, boiling, levels, SoundEvents.ENTITY_FISHING_BOBBER_SPLASH);
	}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		// if boiling is required, ensure it is set
		if(level < levels || !matches(state) || (this.boiling != null && boiling != this.boiling.booleanValue())) {
			return false;
		}

		return matches(stack);
	}

	/**
	 * Returns true if the state matches the given recipe
	 * @param state  State to check
	 * @return  True if the state matches
	 */
	protected abstract boolean matches(CauldronState state);

	/**
	 * Helper method to check if the recipe match matches the input stack
	 * @param stack  stack to check
	 * @return  true if it matches
	 */
	protected boolean matches(ItemStack stack) {
		return this.input.matches(Util.createNonNullList(stack)).isPresent();
	}

	@Override
	public ItemStack transformInput(ItemStack stack, boolean boiling, int level, CauldronState state) {
		NonNullList<ItemStack> list = Util.createNonNullList(stack);
		RecipeMatch.removeMatch(list, input.matches(list).get());
		return list.get(0);
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
	public SoundEvent getSound(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return sound;
	}
}
