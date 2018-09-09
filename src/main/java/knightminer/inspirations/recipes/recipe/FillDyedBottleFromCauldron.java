package knightminer.inspirations.recipes.recipe;

import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;

public enum FillDyedBottleFromCauldron implements ICauldronRecipe {
	INSTANCE;

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return level != 0 && state.getColor() > -1 && stack.getItem() == Items.GLASS_BOTTLE;
	}

	@Override
	public ItemStack getResult(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return InspirationsRecipes.dyedWaterBottle.getStackWithColor(state.getColor());
	}

	@Override
	public int getLevel(int level) {
		return level - 1;
	}

	@Override
	public SoundEvent getSound(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return SoundEvents.ITEM_BOTTLE_FILL;
	}
	
	@Override
	public ItemStack getContainer(ItemStack stack) {
		return ItemStack.EMPTY;
	}
}

