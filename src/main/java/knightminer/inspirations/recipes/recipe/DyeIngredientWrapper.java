package knightminer.inspirations.recipes.recipe;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.recipes.item.ItemSimpleDyedWaterBottle;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.registry.Registry;

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
					.filter(stack->stack.getItem() != InspirationsRecipes.simpleDyedWaterBottle)
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
			for (Map.Entry<DyeColor, ItemSimpleDyedWaterBottle> entry: InspirationsRecipes.simpleDyedWaterBottle.entrySet()) {
				dyedBottle[entry.getKey().colorValue] = Registry.ITEM.getId(entry.getValue());
			}
			this.itemIds = new IntArrayList(oreList);
			this.itemIds.removeAll(new IntArrayList(dyedBottle));
			lastSizeL = oreList.size();
		}

		return this.itemIds;
	}


	@Override
	public boolean test(@Nullable ItemStack input) {
		if (input == null || input.getItem() instanceof ItemSimpleDyedWaterBottle) {
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
