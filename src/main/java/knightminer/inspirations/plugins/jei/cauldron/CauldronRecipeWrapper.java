package knightminer.inspirations.plugins.jei.cauldron;

import com.google.common.collect.ImmutableList;

import knightminer.inspirations.library.recipe.cauldron.ISimpleCauldronRecipe;
import knightminer.inspirations.plugins.jei.JEIPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import java.util.List;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;

public class CauldronRecipeWrapper implements IRecipeWrapper {

	protected final List<List<ItemStack>> input;
	protected final List<ItemStack> output;
	protected final boolean boiling;


	public CauldronRecipeWrapper(ISimpleCauldronRecipe recipe) {
		this.input = ImmutableList.of(recipe.getInput());
		this.output = ImmutableList.of(recipe.getResult());
		this.boiling = recipe.isBoiling();
	}

	public CauldronRecipeWrapper(ItemStack input, ItemStack output, boolean boiling) {
		this.input = ImmutableList.of(ImmutableList.of(input));
		this.output = ImmutableList.of(output);
		this.boiling = boiling;
	}

	public CauldronRecipeWrapper(List<ItemStack> input, ItemStack output, boolean boiling) {
		this.input = ImmutableList.of(input);
		this.output = ImmutableList.of(output);
		this.boiling = boiling;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, input);
		ingredients.setOutputs(ItemStack.class, output);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		if(boiling) {
			JEIPlugin.cauldron.fire.draw(minecraft, 45, 42);
		}
	}
}
