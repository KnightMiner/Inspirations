package knightminer.inspirations.library.recipe.cauldron;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import slimeknights.mantle.util.RecipeMatch;

public class CauldronFluidTransformRecipe extends CauldronFluidRecipe {

	private CauldronState result;
	public CauldronFluidTransformRecipe(RecipeMatch input, Fluid fluid, Fluid result, Boolean boiling) {
		super(input, fluid, ItemStack.EMPTY, boiling);
		this.result = CauldronState.fluid(result);
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
}
