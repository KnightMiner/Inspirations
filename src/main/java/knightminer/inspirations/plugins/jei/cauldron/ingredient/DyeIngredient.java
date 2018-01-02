package knightminer.inspirations.plugins.jei.cauldron.ingredient;

import net.minecraft.item.EnumDyeColor;

public class DyeIngredient {
	private EnumDyeColor dye;
	public DyeIngredient (EnumDyeColor dye) {
		this.dye = dye;
	}

	public EnumDyeColor getDye() {
		return dye;
	}
}
