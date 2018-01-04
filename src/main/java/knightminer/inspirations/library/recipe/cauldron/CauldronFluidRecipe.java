package knightminer.inspirations.library.recipe.cauldron;

import java.util.List;

import knightminer.inspirations.library.Util;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import slimeknights.mantle.util.RecipeMatch;

public class CauldronFluidRecipe implements ISimpleCauldronRecipe {
	private RecipeMatch input;
	private ItemStack result;
	private Boolean boiling;
	private Fluid fluid;
	private SoundEvent sound;

	public CauldronFluidRecipe(RecipeMatch input, Fluid fluid, ItemStack result, Boolean boiling, SoundEvent sound) {
		this.input = input;
		this.result = result;
		this.boiling = boiling;
		this.fluid = fluid;
		this.sound = sound;
	}

	public CauldronFluidRecipe(RecipeMatch input, Fluid fluid, ItemStack result, Boolean boiling) {
		this(input, fluid, result, boiling, SoundEvents.ENTITY_BOBBER_SPLASH);
	}

	public CauldronFluidRecipe(RecipeMatch input, ItemStack result, Boolean boiling) {
		this(input, null, result, boiling);
	}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		// if boiling is required, ensure it is set
		if(level == 0 || !stateMatches(state) || (this.boiling != null && boiling != this.boiling.booleanValue())) {
			return false;
		}

		return this.input.matches(Util.createNonNullList(stack)).isPresent();
	}

	private boolean stateMatches(CauldronState state) {
		return fluid == null ? state.isWater() : fluid == state.getFluid();
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
		return level - 1;
	}

	@Override
	public int getInputLevel() {
		return 1;
	}

	@Override
	public boolean isBoiling() {
		return boiling == Boolean.TRUE;
	}

	@Override
	public Object getInputState() {
		return fluid == null ? FluidRegistry.WATER : fluid;
	}

	/**
	 * Gets the sound to play when performing this recipe
	 * @return  Sound event
	 */
	@Override
	public SoundEvent getSound(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return sound;
	}
}
