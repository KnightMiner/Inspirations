package knightminer.inspirations.recipes.recipe;

import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;

public class ArmorDyeingCauldronRecipe implements ICauldronRecipe {

	private final ArmorMaterial material;
	public ArmorDyeingCauldronRecipe(ArmorMaterial material) {
		this.material = material;
	}

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
		return armor.getArmorMaterial() == material && armor.getColor(stack) != state.getColor();
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

	@Override
	public ItemStack getContainer(ItemStack stack) {
		return ItemStack.EMPTY;
	}
}
