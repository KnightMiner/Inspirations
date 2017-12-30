package knightminer.inspirations.library.recipe.cauldron;

import java.util.List;

import knightminer.inspirations.library.Util;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import slimeknights.mantle.util.RecipeMatch;

public class CauldronFluidRecipe implements ISimpleCauldronRecipe {
	private RecipeMatch input;
	private ItemStack result;
	private Boolean boiling;
	private Fluid fluid;

	public CauldronFluidRecipe(RecipeMatch input, Fluid fluid, ItemStack result, Boolean boiling) {
		this.input = input;
		this.result = result;
		this.boiling = boiling;
		this.fluid = fluid;
	}
	public CauldronFluidRecipe(RecipeMatch input, ItemStack result, Boolean boiling) {
		this(input, FluidRegistry.WATER, result, boiling);
	}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		// if boiling is required, ensure it is set
		if(level == 0 || state.getFluid() != fluid || (this.boiling != null && boiling != this.boiling.booleanValue())) {
			return false;
		}

		return this.input.matches(Util.createNonNullList(stack)).isPresent();
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
		return level - 1;
	}

	@Override
	public boolean isBoiling() {
		return boiling == Boolean.TRUE;
	}

	@Override
	public Object getInputState() {
		return fluid;
	}
}
