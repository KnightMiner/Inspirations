package knightminer.inspirations.library.recipe.cauldron;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import slimeknights.mantle.util.RecipeMatch;

public class CauldronFluidTransformRecipe extends CauldronFluidRecipe {

	private CauldronState result;
	private int maxLevel;
	public CauldronFluidTransformRecipe(RecipeMatch input, Fluid fluid, Fluid result, Boolean boiling) {
		this(input, fluid, result, boiling, 3);
	}

	public CauldronFluidTransformRecipe(RecipeMatch input, Fluid fluid, Fluid result, Boolean boiling, int maxLevel) {
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
}
