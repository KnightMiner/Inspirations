package knightminer.inspirations.recipes.recipe;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.cauldron.ISimpleCauldronRecipe;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.shared.InspirationsOredict;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.oredict.OreIngredient;

public class DyeCauldronWater implements ISimpleCauldronRecipe {

	private EnumDyeColor color;
	private Ingredient dye;
	public DyeCauldronWater(EnumDyeColor color) {
		this.color = color;
		this.dye = new OreIngredient(InspirationsOredict.dyeNameFor(color));
	}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		// special case water bottles
		if(level == 0 || stack.getItem() == InspirationsRecipes.dyedWaterBottle) {
			return false;
		}

		// type must be water or dye
		// input must not be the same color as the original dye
		return (state.isWater() || state.getColor() > -1)
				&& dye.apply(stack) && color.colorValue != state.getColor();
	}

	@Override
	public List<ItemStack> getInput() {
		// we want to ignore the dyed water bottle as that has special behavior
		return Arrays.stream(dye.getMatchingStacks())
				.filter(stack->stack.getItem() != InspirationsRecipes.dyedWaterBottle)
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
