package knightminer.inspirations.library.recipe.cauldron;

import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import knightminer.inspirations.library.Util;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.FluidRegistry;
import slimeknights.mantle.util.RecipeMatch;

/**
 * Recipe to transform an item stack using a fluid
 */
@ParametersAreNonnullByDefault
public class CauldronPotionRecipe implements ISimpleCauldronRecipe {
	private RecipeMatch input;
	private ItemStack result;
	protected PotionType potion;
	private int levels;
	@Nullable
	private Boolean boiling;

	/**
	 * @param input    Input recipe match entry
	 * @param potion   Required potion in the cauldron
	 * @param result   Resulting item stack
	 * @param levels   Amount of levels consumed by the recipe, also determines how many levels are required to perform the recipe
	 * @param boiling  If true, the cauldron must be above fire, if false it must not be above fire. Set to null to ignore fire
	 */
	public CauldronPotionRecipe(RecipeMatch input, PotionType potion, ItemStack result, int levels, @Nullable Boolean boiling) {
		this.input = input;
		this.result = result;
		this.potion = potion;
		this.levels = levels;
		this.boiling = boiling;
	}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		// if boiling is required, ensure it is set
		if(level < levels || state.getPotion() != potion || (this.boiling != null && boiling != this.boiling.booleanValue())) {
			return false;
		}

		return this.input.matches(Util.createNonNullList(stack)).isPresent();
	}

	@Override
	public ItemStack transformInput(ItemStack stack, boolean boiling, int level, CauldronState state) {
		stack.shrink(input.amountNeeded);
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
		return potion == PotionTypes.WATER ? FluidRegistry.WATER : potion;
	}

	/**
	 * Gets the sound to play when performing this recipe
	 * @return  Sound event
	 */
	@Override
	public SoundEvent getSound(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return SoundEvents.ENTITY_BOBBER_SPLASH;
	}

	@Override
	public String toString() {
		return String.format("CauldronFluidRecipe: %s from %s", result.toString(), potion.getRegistryName());
	}
}
