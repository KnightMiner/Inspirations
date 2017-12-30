package knightminer.inspirations.library.recipe.cauldron;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionType;
import net.minecraftforge.fluids.FluidRegistry;

public class CauldronBrewingRecipe implements ISimpleCauldronRecipe {

	private Ingredient reagent;
	private CauldronState input;
	private CauldronState output;
	public CauldronBrewingRecipe(PotionType input, Ingredient reagent, PotionType output) {
		this.input = CauldronState.potion(input);
		this.reagent = reagent;
		this.output = CauldronState.potion(output);
	}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return boiling && state.matches(input) && reagent.apply(stack);
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
	public Object getInputState() {
		return input.getType() == CauldronContents.WATER ? FluidRegistry.WATER : input.getPotion();
	}

	@Override
	public Object getState() {
		return output.getPotion();
	}

	@Override
	public boolean isBoiling() {
		return true;
	}
}
