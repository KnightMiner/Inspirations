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
import net.minecraftforge.oredict.OreDictionary;

public abstract class PotionWrapper implements ICauldronRecipeWrapper {

	protected static List<List<PotionIngredient>> potions = ImmutableList.of(PotionIngredientHelper.ALL_POTIONS);
	protected List<List<ItemStack>> filled;
	private ItemStack item;

	public PotionWrapper(ItemStack item) {
		this.item = item;
		this.filled = ImmutableList.of(PotionIngredientHelper.ALL_POTIONS.stream()
				.map(potion -> PotionUtils.addPotionToItemStack(item.copy(), potion.getPotion()))
				.collect(Collectors.toList()));
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

	public static class Fill extends PotionWrapper {
		private List<List<ItemStack>> bottles;
		public Fill(ItemStack item, String bottle) {
			super(item);
			bottles = ImmutableList.of(OreDictionary.getOres(bottle, false));
		}

		public Fill(ItemStack item, ItemStack bottle) {
			super(item);
			bottles = ImmutableList.of(ImmutableList.of(bottle));
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setInputLists(ItemStack.class, bottles);
			ingredients.setOutputLists(ItemStack.class, filled);
			ingredients.setInputLists(PotionIngredient.class, potions);
		}

		@Override
		public int getInputLevel() {
			return 1;
		}

		@Override
		public int getOutputLevel() {
			return 1;
		}
	}

	public static class Empty extends PotionWrapper {
		private ItemStack bottle;
		public Empty(ItemStack item, ItemStack bottle) {
			super(item);
			this.bottle = bottle;
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setOutput(ItemStack.class, bottle);
			ingredients.setInputLists(ItemStack.class, filled);
			ingredients.setOutputLists(PotionIngredient.class, potions);
		}

		@Override
		public int getInputLevel() {
			return 0;
		}

		@Override
		public int getOutputLevel() {
			return 1;
		}
	}
}
