package knightminer.inspirations.plugins.jei.cauldron.ingredient;

import net.minecraft.item.DyeColor;

public class DyeIngredient {
	private DyeColor dye;
	public DyeIngredient (DyeColor dye) {
		this.dye = dye;
	}

	public DyeColor getDye() {
		return dye;
	}
}
