package knightminer.inspirations.recipes.recipe.cauldron;

import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BannerTileEntity;

public enum BannerClearingCauldronRecipe implements ICauldronRecipe {
	INSTANCE;

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return state.isWater() && level > 0 && stack.getItem() instanceof BannerItem && BannerTileEntity.getPatterns(stack) > 0;
	}

	@Override
	public ItemStack getResult(ItemStack stack, boolean boiling, int level, CauldronState state) {
		ItemStack copy = stack.copy();
		copy.setCount(1);
		BannerTileEntity.removeBannerData(copy);
		return copy;
	}

	@Override
	public int getLevel(int level) {
		return level - 1;
	}

	@Override
	public ItemStack getContainer(ItemStack stack) {
		return ItemStack.EMPTY;
	}
}
