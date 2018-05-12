package knightminer.inspirations.plugins.jei.cauldron;

import java.util.List;
import java.util.stream.Collectors;
import com.google.common.collect.ImmutableList;

import knightminer.inspirations.plugins.jei.cauldron.ingredient.PotionIngredient;
import knightminer.inspirations.plugins.jei.cauldron.ingredient.PotionIngredientHelper;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron.CauldronContents;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;

public class PotionFillWrapper implements ICauldronRecipeWrapper {

	private static List<List<PotionIngredient>> potions = ImmutableList.of(PotionIngredientHelper.ALL_POTIONS);
	private List<List<ItemStack>> filled;
	private ItemStack item, bottle;
	private boolean fill;

	public PotionFillWrapper(ItemStack item, ItemStack bottle, boolean fill) {
		this.item = item;
		this.filled = ImmutableList.of(PotionIngredientHelper.ALL_POTIONS.stream()
				.map(potion -> PotionUtils.addPotionToItemStack(item.copy(), potion.getPotion()))
				.collect(Collectors.toList()));
		this.bottle = bottle;
		this.fill = fill;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		if(fill) {
			ingredients.setInput(ItemStack.class, bottle);
			ingredients.setOutputLists(ItemStack.class, filled);
			ingredients.setInputLists(PotionIngredient.class, potions);
		} else {
			ingredients.setOutput(ItemStack.class, bottle);
			ingredients.setInputLists(ItemStack.class, filled);
			ingredients.setOutputLists(PotionIngredient.class, potions);
		}
	}

	@Override
	public ItemStack getPotionItem() {
		return item.copy();
	}

	@Override
	public CauldronContents getInputType() {
		return CauldronContents.POTION;
	}

	@Override
	public CauldronContents getOutputType() {
		return CauldronContents.POTION;
	}

	@Override
	public int getInputLevel() {
		return fill ? 1 : 0;
	}

	@Override
	public int getOutputLevel() {
		return fill ? 0 : 1;
	}
}
