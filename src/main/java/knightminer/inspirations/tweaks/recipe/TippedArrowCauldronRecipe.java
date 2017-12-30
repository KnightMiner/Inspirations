package knightminer.inspirations.tweaks.recipe;

import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;

public enum TippedArrowCauldronRecipe implements ICauldronRecipe {
	INSTANCE;

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return state.getType() == CauldronContents.POTION && stack.getItem() == Items.ARROW;
	}

	@Override
	public ItemStack getResult(ItemStack stack, boolean boiling, int level, CauldronState state) {
		int size = Math.min(stack.getCount(), 8);
		return PotionUtils.addPotionToItemStack(new ItemStack(Items.TIPPED_ARROW, size), state.getPotion());
	}

	@Override
	public ItemStack transformInput(ItemStack stack, boolean boiling, int level, CauldronState state) {
		stack.shrink(Math.min(stack.getCount(), 8));
		return stack;
	}

	@Override
	public int getLevel(int level) {
		return level - 1;
	}

}
