package knightminer.inspirations.recipes.recipe;

import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.SoundEvent;
import slimeknights.mantle.util.RecipeMatch;

public class FillPotionFromCauldron implements ICauldronRecipe {

	private RecipeMatch bottle;
	private Item potion;
	public FillPotionFromCauldron(Item potion, ItemStack bottle) {
		this.bottle = RecipeMatch.of(bottle);
		this.potion = potion;
	}

	public FillPotionFromCauldron(Item potion, String bottle) {
		this.bottle = RecipeMatch.of(bottle);
		this.potion = potion;
	}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		if(level == 0) {
			return false;
		}
		return state.getPotion() != null && bottle.matches(Util.createNonNullList(stack)).isPresent();
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
