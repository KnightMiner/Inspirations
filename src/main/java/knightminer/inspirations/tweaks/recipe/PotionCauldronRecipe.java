package knightminer.inspirations.tweaks.recipe;

import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.util.SoundEvent;

public class PotionCauldronRecipe implements ICauldronRecipe {

	public static final PotionCauldronRecipe INSTANCE = new PotionCauldronRecipe();
	private PotionCauldronRecipe() {}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		if(!boiling || level == 0) {
			return false;
		}
		CauldronContents type = state.getType();
		return (type == CauldronContents.WATER || type == CauldronContents.POTION)
				&& hasRecipe(state.getPotion(), stack);
	}

	@Override
	public CauldronState getState(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return CauldronState.potion(getResult(state.getPotion(), stack));
	}


	@Override
	public SoundEvent getSound(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return SoundEvents.BLOCK_BREWING_STAND_BREW;
	}


	/* Helpers */

	private static boolean hasRecipe(PotionType potion, ItemStack reagent) {
		return getResult(potion, reagent) != null;
	}

	private static PotionType getResult(PotionType potion, ItemStack reagent) {
		for(PotionHelper.MixPredicate<PotionType> recipe : PotionHelper.POTION_TYPE_CONVERSIONS) {
			if(recipe.input == potion && recipe.reagent.apply(reagent)) {
				return recipe.output;
			}
		}

		return null;
	}
}
