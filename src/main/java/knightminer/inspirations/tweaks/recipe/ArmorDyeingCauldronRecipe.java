package knightminer.inspirations.tweaks.recipe;

import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;

public class ArmorDyeingCauldronRecipe implements ICauldronRecipe {

	public static final ArmorDyeingCauldronRecipe INSTANCE = new ArmorDyeingCauldronRecipe();
	private ArmorDyeingCauldronRecipe() {}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		if(level == 0 || state.getType() != CauldronContents.DYE) {
			return false;
		}
		if(!(stack.getItem() instanceof ItemArmor)) {
			return false;
		}

		// only color leather, and ensure we are changing the color
		ItemArmor armor = (ItemArmor) stack.getItem();
		return armor.getArmorMaterial() == ArmorMaterial.LEATHER && armor.getColor(stack) != state.getColor();
	}

	@Override
	public ItemStack getResult(ItemStack stack, boolean boiling, int level, CauldronState state) {
		stack = stack.copy();
		((ItemArmor) stack.getItem()).setColor(stack, state.getColor());
		return stack;
	}

	@Override
	public int getLevel(int level) {
		return level - 1;
	}
}
