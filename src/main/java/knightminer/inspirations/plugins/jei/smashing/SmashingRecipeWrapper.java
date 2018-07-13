package knightminer.inspirations.plugins.jei.smashing;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.Util;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.mantle.client.CreativeTab;

public class SmashingRecipeWrapper implements IRecipeWrapper {

	protected final List<List<ItemStack>> input;
	protected final List<ItemStack> output;

	private String heightRequirementString;

	public SmashingRecipeWrapper(ItemStack input, ItemStack output) {
		this.input = ImmutableList.of(ImmutableList.of(input));
		this.output = ImmutableList.of(output);
	}

	public SmashingRecipeWrapper(List<ItemStack> inputs, List<ItemStack> output, Integer minFallHeight) {
		this.input = inputs.stream().map(ImmutableList::of).collect(Collectors.toList());
		this.output = ImmutableList.copyOf(output);
		this.heightRequirementString = minFallHeight != null ? Util.translateFormatted("gui.jei.anvil_smashing.height", minFallHeight) : null;
	}

	@SuppressWarnings("deprecation")
	public SmashingRecipeWrapper(Block input, ItemStack output) {
		NonNullList<ItemStack> inputList = NonNullList.create();
		input.getSubBlocks(CreativeTab.SEARCH, inputList);
		// remove anything that has a specific state from the general list
		inputList.removeIf((stack) -> {
			return InspirationsRegistry.hasAnvilSmashStateResult(input.getStateFromMeta(stack.getMetadata()));
		});
		this.input = ImmutableList.of(inputList);
		this.output = ImmutableList.of(output);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, input);
		ingredients.setOutputs(ItemStack.class, output);
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		List<String> tooltip = Lists.newArrayList();
		if (mouseX >= 68 && mouseX <= 90 && mouseY >= 17 && mouseY <= 31) {
			if (isNotEmpty(heightRequirementString)) {
				tooltip.add(heightRequirementString);
			}
		}
		return tooltip;
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		if (isNotEmpty(heightRequirementString)) {
			minecraft.fontRenderer.drawString("!", 76, 13, Color.gray.getRGB());
		}
	}

	private boolean isNotEmpty(String string) {
		return string != null && !string.isEmpty();
	}
}
