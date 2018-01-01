package knightminer.inspirations.recipes.recipe;

import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;

public enum ArmorDyeingCauldronRecipe implements ICauldronRecipe {
	INSTANCE;

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		if(level == 0 || state.getColor() == -1) {
			return false;
		}
		Item item = stack.getItem();
		if(!(item instanceof ItemArmor)) {
			return false;
		}

		// only color leather, and ensure we are changing the color
		ItemArmor armor = (ItemArmor) item;
		return armor.getArmorMaterial() == ArmorMaterial.LEATHER && armor.getColor(stack) != state.getColor();
	}

	@Override
	public ItemStack transformInput(ItemStack stack, boolean boiling, int level, CauldronState state) {
		((ItemArmor) stack.getItem()).setColor(stack, state.getColor());
		return stack;
	}

	@Override
	public int getLevel(int level) {
		return level - 1;
	}
}
