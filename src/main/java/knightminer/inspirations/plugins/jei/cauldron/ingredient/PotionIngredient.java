package knightminer.inspirations.plugins.jei.cauldron.ingredient;

import net.minecraft.potion.Potion;

public class PotionIngredient {
	private Potion potion;
	public PotionIngredient(Potion potion) {
		this.potion = potion;
	}

	public Potion getPotion() {
		return potion;
	}
}
