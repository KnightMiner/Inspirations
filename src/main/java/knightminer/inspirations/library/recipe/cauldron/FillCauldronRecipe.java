package knightminer.inspirations.library.recipe.cauldron;

import knightminer.inspirations.library.Util;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.Fluid;
import slimeknights.mantle.util.RecipeMatch;

public class FillCauldronRecipe extends CauldronFluidRecipe {

	private RecipeMatch input;
	private CauldronState fluid;
	private int amount;
	private SoundEvent sound;
	public FillCauldronRecipe(RecipeMatch input, Fluid fluid, int amount, ItemStack container, SoundEvent sound) {
		super(input, fluid, container, null);
		this.input = input;
		this.fluid = CauldronState.fluid(fluid);
		this.amount = amount;
		this.sound = sound;
	}

	public FillCauldronRecipe(RecipeMatch input, Fluid fluid, int amount, ItemStack container) {
		this(input, fluid, amount, container, SoundEvents.ITEM_BOTTLE_EMPTY);
	}

	public FillCauldronRecipe(RecipeMatch input, Fluid fluid, int amount) {
		this(input, fluid, amount, ItemStack.EMPTY);
	}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		if(level == 3 || (level > 0 && !state.matches(fluid))) {
			return false;
		}

		return this.input.matches(Util.createNonNullList(stack)).isPresent();
	}

	@Override
	public CauldronState getState(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return fluid;
	}

	@Override
	public int getLevel(int level) {
		return level + amount;
	}

	@Override
	public int getInputLevel() {
		return 0;
	}

	@Override
	public SoundEvent getSound(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return sound;
	}
}
