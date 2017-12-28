package knightminer.inspirations.tweaks.recipe;

import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.oredict.OreDictionary;

public class FillPotionFromCauldron implements ICauldronRecipe {

	private ItemStack bottle;
	private Item potion;
	public FillPotionFromCauldron(Item potion, ItemStack bottle) {
		this.bottle = bottle;
		this.potion = potion;
	}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		if(level == 0) {
			return false;
		}
		CauldronContents type = state.getType();
		return (type == CauldronContents.WATER || type == CauldronContents.POTION)
				&& OreDictionary.itemMatches(stack, bottle, true);
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
	public float getVolume() {
		return 1.0f;
	}
}
