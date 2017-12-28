package knightminer.inspirations.tweaks.recipe;

import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.tweaks.InspirationsTweaks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;

public class FillCauldronFromDyedBottle implements ICauldronRecipe {

	public static final FillCauldronFromDyedBottle INSTANCE = new FillCauldronFromDyedBottle();
	private FillCauldronFromDyedBottle() {}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
		if(level == 3) {
			return false;
		}

		CauldronContents type = state.getType();
		return (type == CauldronContents.WATER || type == CauldronContents.DYE)
				&& stack.getItem() == InspirationsTweaks.dyedWaterBottle;
	}

	@Override
	public CauldronState getState(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
		int newColor = InspirationsTweaks.dyedWaterBottle.getColor(stack);
		if(level == 0) {
			return CauldronState.dye(newColor);
		}

		int color = state.getColor();
		// if color is unchanged, use the old state
		if(newColor == color) {
			return state;
		}

		// otherwise combine colors
		if(color == -1) {
			color = 0x888888;
		}

		return CauldronState.dye(Util.combineColors(newColor, color, level));
	}

	@Override
	public int getLevel(int level) {
		return level + 1;
	}

	@Override
	public ItemStack transformInput(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return stack.getItem().getContainerItem(stack);
	}

	@Override
	public SoundEvent getSound(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return SoundEvents.ITEM_BOTTLE_EMPTY;
	}

	@Override
	public float getVolume() {
		return 1.0f;
	}
}

