package knightminer.inspirations.recipes.recipe;

import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;

public enum ArmorClearRecipe implements ICauldronRecipe {
	INSTANCE;

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		if(level == 0 || !state.isWater()) {
			return false;
		}
		Item item = stack.getItem();
		if(!(item instanceof ItemArmor)) {
			return false;
		}

		// only color leather, and ensure we are changing the color
		ItemArmor armor = (ItemArmor) item;
		return armor.getArmorMaterial() == ArmorMaterial.LEATHER && armor.hasColor(stack);
	}

	@Override
	public ItemStack getResult(ItemStack stack, boolean boiling, int level, CauldronState state) {
		stack = stack.copy();
		((ItemArmor) stack.getItem()).removeColor(stack);
		return stack;
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
