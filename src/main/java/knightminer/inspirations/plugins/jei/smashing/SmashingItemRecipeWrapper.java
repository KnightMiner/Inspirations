package knightminer.inspirations.plugins.jei.smashing;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.anvil.ISimpleAnvilRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SmashingItemRecipeWrapper implements IRecipeWrapper {

	protected final ISimpleAnvilRecipe recipe;
	private final List<List<ItemStack>> input;
	private final ImmutableList<ItemStack> output;

	private String heightRequirementString;

	public SmashingItemRecipeWrapper(ISimpleAnvilRecipe recipe) {
		this.recipe = recipe;
		this.input = recipe.getInput();
		this.output = ImmutableList.copyOf(this.recipe.getResult());

		Optional<Integer> minFallHeight = recipe.getFallHeight();
		this.heightRequirementString = minFallHeight
				.map(integer -> Util.translateFormatted("gui.jei.anvil_smashing.height", integer)).orElse(null);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, input);
		ingredients.setOutputs(ItemStack.class, output);
	}

	public ISimpleAnvilRecipe getRecipe() {
		return recipe;
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		if(heightRequirementString != null) {
			if(inRange(65, 90, mouseX) && inRange(20, 50, mouseY)) {
				return Collections.singletonList(Util.translateFormatted("gui.jei.anvil_smashing.height.text"));
			}
		}
		return Collections.emptyList();
	}

	private static boolean inRange(int from, int to, int value) {
		return value >= from && value <= to;
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		if(heightRequirementString != null) {
			int width = minecraft.fontRenderer.getStringWidth(this.heightRequirementString);
			minecraft.fontRenderer.drawString(this.heightRequirementString, 78 - (width / 2), 20, Color.gray.getRGB());
		}
	}
}
