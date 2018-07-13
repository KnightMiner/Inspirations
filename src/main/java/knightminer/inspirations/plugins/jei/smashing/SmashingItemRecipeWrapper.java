package knightminer.inspirations.plugins.jei.smashing;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.anvil.ISimpleAnvilRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class SmashingItemRecipeWrapper implements IRecipeWrapper {

	protected final ISimpleAnvilRecipe recipe;
	private final List<List<ItemStack>> input;
	private final ImmutableList<ItemStack> output;

	private String heightRequirementString;
	private String blockRequirementString;

	public SmashingItemRecipeWrapper(ISimpleAnvilRecipe recipe) {
		this.recipe = recipe;
		this.input = this.recipe.getInput().stream().map(ImmutableList::of).collect(Collectors.toList());
		this.output = ImmutableList.copyOf(this.recipe.getResult());

		Integer minFallHeight = recipe.getFallHeight();
		this.heightRequirementString =
				minFallHeight != null ? Util.translateFormatted("gui.jei.anvil_smashing.height", minFallHeight) : null;

		Object inputState = recipe.getInputState();
		this.blockRequirementString = inputState instanceof IBlockState ?
				Util.translateFormatted("gui.jei.anvil_smashing.blockstate",
						((IBlockState) inputState).getBlock().getLocalizedName()) :
				null;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, input);
		ingredients.setOutputs(ItemStack.class, output);
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		List<String> tooltip = Lists.newArrayList();
		if(mouseX >= 68 && mouseX <= 90 && mouseY >= 17 && mouseY <= 31) {
			if(isNotEmpty(heightRequirementString)) {
				tooltip.add(heightRequirementString);
			}

			if(isNotEmpty(blockRequirementString)) {
				tooltip.add(blockRequirementString);
			}
		}
		return tooltip;
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		if(isNotEmpty(heightRequirementString) || isNotEmpty(blockRequirementString)) {
			minecraft.fontRenderer.drawString("!", 76, 13, Color.gray.getRGB());
		}
	}

	private boolean isNotEmpty(String string) {
		return string != null && !string.isEmpty();
	}
}
