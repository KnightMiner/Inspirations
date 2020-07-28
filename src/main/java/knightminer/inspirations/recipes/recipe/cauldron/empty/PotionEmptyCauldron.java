package knightminer.inspirations.recipes.recipe.cauldron.empty;

import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tags.ITag;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public class PotionEmptyCauldron implements ICauldronRecipe {

	private ITag<Item> bottle;
	private Item potion;
	public PotionEmptyCauldron(Item potion, ITag<Item> bottle) {
		this.bottle = bottle;
		this.potion = potion;
	}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		if(level == 0) {
			return false;
		}
		return state.getPotion() != null && stack.getItem().isIn(bottle);
	}

	@Override
	public ItemStack getResult(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return PotionUtils.addPotionToItemStack(new ItemStack(potion), state.getPotion());
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
