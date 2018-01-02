package knightminer.inspirations.plugins.jei.cauldron.ingredient;

import net.minecraft.potion.PotionType;

public class PotionIngredient {
	private PotionType potion;
	public PotionIngredient(PotionType potion) {
		this.potion = potion;
	}

	public PotionType getPotion() {
		return potion;
	}
}
