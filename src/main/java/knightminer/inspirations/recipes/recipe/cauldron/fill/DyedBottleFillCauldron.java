package knightminer.inspirations.recipes.recipe.cauldron.fill;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.recipes.item.MixedDyedBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

@Deprecated
public enum DyedBottleFillCauldron implements ICauldronRecipe {
	INSTANCE;

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		if(level == Config.getCauldronMax()) {
			return false;
		}

		return (state.isWater() || state.getColor() > -1)
				&& stack.getItem().isIn(InspirationsTags.Items.DYE_BOTTLES);
	}

	@Override
	public CauldronState getState(ItemStack stack, boolean boiling, int level, CauldronState state) {
		int newColor = MixedDyedBottleItem.dyeFromBottle(stack);
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
	public ItemStack getResult(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return stack.getItem().getContainerItem(stack);
	}

	@Override
	public SoundEvent getSound(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return SoundEvents.ITEM_BOTTLE_EMPTY;
	}

	@Override
	public ItemStack getContainer(ItemStack stack) {
		return ItemStack.EMPTY;
	}
}

