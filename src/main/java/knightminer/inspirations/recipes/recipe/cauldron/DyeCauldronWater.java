package knightminer.inspirations.recipes.recipe.cauldron;

import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.cauldron.ISimpleCauldronRecipe;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DyeCauldronWater implements ISimpleCauldronRecipe {

	private DyeColor color;
	private Ingredient dye;
	public DyeCauldronWater(DyeColor color) {
		this.color = color;
		this.dye = Ingredient.fromTag(ItemTags.getCollection().getOrCreate(
				new ResourceLocation("forge", "dyes/" + color.getName()
		)));
	}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		// special case water bottles
		if(level == 0 || stack.getItem().isIn(InspirationsTags.Items.DYE_BOTTLES)) {
			return false;
		}

		// type must be water or dye
		// input must not be the same color as the original dye
		return (state.isWater() || state.getColor() > -1)
				&& dye.test(stack) && color.colorValue != state.getColor();
	}

	@Override
	public List<ItemStack> getInput() {
		// we want to ignore the dyed water bottle as that has special behavior
		return Arrays.stream(dye.getMatchingStacks())
				.filter(stack->!stack.getItem().isIn(InspirationsTags.Items.DYE_BOTTLES))
				.collect(Collectors.toList());
	}

	@Override
	public CauldronState getState(ItemStack stack, boolean boiling, int level, CauldronState state) {
		int newColor = color.colorValue;
		int color = state.getColor();
		if(color > -1) {
			color = Util.combineColors(newColor, color, level);
		} else {
			color = newColor;
		}

		return CauldronState.dye(color);
	}

	@Override
	public Object getState() {
		return color;
	}

	@Override
	public SoundEvent getSound(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return null;
	}
}
