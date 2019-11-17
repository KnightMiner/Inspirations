package knightminer.inspirations.library.recipe.cauldron;

import com.google.common.collect.ImmutableList;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Cauldron recipe to transform a potion into another potion. Used primarily with vanilla potion recipes from the brewing registry.
 */
@ParametersAreNonnullByDefault
public class BrewingCauldronRecipe implements ISimpleCauldronRecipe {

	private Ingredient reagent;
	private CauldronState input;
	private CauldronState output;

	/**
	 * @param input   Input potion type
	 * @param reagent Ingredient for transformation
	 * @param output  Resulting potion type
	 */
	public BrewingCauldronRecipe(Potion input, Ingredient reagent, Potion output) {
		this.input = CauldronState.potion(input);
		this.reagent = reagent;
		this.output = CauldronState.potion(output);
	}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		// must have at least one level and be boiling. If 3 or more stack count must be bigger than 1
		return level > 0 && boiling
				// if expensive brewing, level must be less than 3 or two inputs provided
				&& (!InspirationsRegistry.expensiveCauldronBrewing() || level < 3 || stack.getCount() > 1)
				&& state.matches(input) && reagent.test(stack);
	}

	@Override
	public ItemStack transformInput(ItemStack stack, boolean boiling, int level, CauldronState state) {
		stack.shrink(InspirationsRegistry.expensiveCauldronBrewing() && level > 2 ? 2 : 1);
		return stack;
	}

	@Override
	public CauldronState getState(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return output;
	}

	@Override
	public List<ItemStack> getInput() {
		return ImmutableList.copyOf(reagent.getMatchingStacks());
	}

	@Override
	public int getInputLevel() {
		return InspirationsRegistry.expensiveCauldronBrewing() ? 2 : InspirationsRegistry.getCauldronMax();
	}

	@Override
	public Object getInputState() {
		Potion potion = input.getPotion();
		return potion == Potions.WATER ? Fluids.WATER : potion;
	}

	@Override
	public Object getState() {
		Potion potion = output.getPotion();
		return potion == Potions.WATER ? Fluids.WATER : potion;
	}

	@Override
	public boolean isBoiling() {
		return true;
	}

	@Override
	public SoundEvent getSound(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return SoundEvents.BLOCK_BREWING_STAND_BREW;
	}

	@Override
	public String toString() {
		return String.format("BrewingCauldronRecipe: %s from %s",
				output.getPotion().getRegistryName(),
				input.getPotion().getRegistryName());
	}
}
