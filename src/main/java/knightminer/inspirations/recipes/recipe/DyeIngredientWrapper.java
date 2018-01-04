package knightminer.inspirations.recipes.recipe;

import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class DyeIngredientWrapper extends Ingredient {

	private Ingredient base;
	private int lastSizeA = -1, lastSizeL = -1;
	private ItemStack[] array = null;
	private IntList itemIds = null;
	public DyeIngredientWrapper(Ingredient old) {
		super(0);
		this.base = old;
	}

	@Override
	@Nonnull
	public ItemStack[] getMatchingStacks() {
		ItemStack[] oreStacks = base.getMatchingStacks();
		if(array == null || oreStacks.length != lastSizeA) {
			array = Arrays.stream(oreStacks)
					.filter(stack->stack.getItem() != InspirationsRecipes.dyedWaterBottle)
					.toArray(ItemStack[]::new);
		}
		return array;
	}


	@Override
	@Nonnull
	public IntList getValidItemStacksPacked() {
		IntList oreList = base.getValidItemStacksPacked();
		if (this.itemIds == null || this.lastSizeL != oreList.size()) {
			int dyedBottle = Item.REGISTRY.getIDForObject(InspirationsRecipes.dyedWaterBottle);
			this.itemIds = new IntArrayList(oreList);
			this.itemIds.removeIf(i->(i>>16)==dyedBottle);
		}

		return this.itemIds;
	}


	@Override
	public boolean apply(@Nullable ItemStack input) {
		if (input == null || input.getItem() == InspirationsRecipes.dyedWaterBottle) {
			return false;
		}
		return base.apply(input);
	}

	@Override
	protected void invalidate() {
		array = null;
		itemIds = null;
	}

	@Override
	public boolean isSimple() {
		return base.isSimple();
	}
}
