package knightminer.inspirations.recipes.recipe;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.recipes.item.SimpleDyedBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.stream.Stream;

public class DyeIngredientWrapper extends Ingredient {

	private Ingredient base;
	private int lastSizeA = -1, lastSizeL = -1;
	private ItemStack[] array = null;
	private IntList itemIds = null;
	public DyeIngredientWrapper(Ingredient old) {
		super(Stream.empty());
		this.base = old;
	}

	@Override
	@Nonnull
	public ItemStack[] getMatchingStacks() {
		ItemStack[] oreStacks = base.getMatchingStacks();
		if(array == null || oreStacks.length != lastSizeA) {
			array = Arrays.stream(oreStacks)
					.filter(stack -> InspirationsRecipes.simpleDyedWaterBottle.contains(stack.getItem()))
					.toArray(ItemStack[]::new);
			lastSizeA = oreStacks.length;
		}
		return array;
	}


	@Override
	@Nonnull
	public IntList getValidItemStacksPacked() {
		IntList oreList = base.getValidItemStacksPacked();
		if (this.itemIds == null || this.lastSizeL != oreList.size()) {
			int[] dyedBottle = new int[16];
			// TODO: better way to do this?
			for (SimpleDyedBottleItem item : InspirationsRecipes.simpleDyedWaterBottle.values()) {
				dyedBottle[item.getColor().getId()] = Registry.ITEM.getId(item);
			}
			this.itemIds = new IntArrayList(oreList);
			this.itemIds.removeAll(new IntArrayList(dyedBottle));
			lastSizeL = oreList.size();
		}

		return this.itemIds;
	}


	@Override
	public boolean test(@Nullable ItemStack input) {
		if (input == null || input.getItem() instanceof SimpleDyedBottleItem) {
			return false;
		}
		return base.test(input);
	}

	@Override
	protected void invalidate() {
		super.invalidate();
		array = null;
		itemIds = null;
	}

	@Override
	public boolean isSimple() {
		return base.isSimple();
	}
}
