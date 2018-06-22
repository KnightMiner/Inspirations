package knightminer.inspirations.recipes.recipe;

import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBanner;

public enum BannerClearRecipe implements ICauldronRecipe {
	INSTANCE;

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return state.isWater() && level > 0 && stack.getItem() == Items.BANNER && TileEntityBanner.getPatterns(stack) > 0;
	}

	@Override
	public ItemStack getResult(ItemStack stack, boolean boiling, int level, CauldronState state) {
		ItemStack copy = stack.copy();
		copy.setCount(1);
		TileEntityBanner.removeBannerData(copy);
		return copy;
	}

	@Override
	public int getLevel(int level) {
		return level - 1;
	}
}
