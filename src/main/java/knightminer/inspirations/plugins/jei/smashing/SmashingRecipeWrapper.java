package knightminer.inspirations.plugins.jei.smashing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.recipes.recipe.anvil.AnvilSmashingItemRecipe;
import knightminer.inspirations.recipes.recipe.anvil.conditions.ExactBlockStateCondition;
import knightminer.inspirations.recipes.recipe.anvil.IBlockStateCondition;
import knightminer.inspirations.recipes.recipe.anvil.conditions.OreDictBlockStateCondition;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.mantle.client.CreativeTab;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SmashingRecipeWrapper implements IRecipeWrapper {

	protected final List<List<ItemStack>> input;
	protected final List<ItemStack> output;
	private String heightRequirementString;
	private String blockRequirementString;

	public SmashingRecipeWrapper(ItemStack input, ItemStack output) {
		this.input = ImmutableList.of(ImmutableList.of(input));
		this.output = ImmutableList.of(output);
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

	public SmashingRecipeWrapper(AnvilSmashingItemRecipe recipe) {
		NonNullList<List<ItemStack>> inputList = NonNullList.create();
		ItemStack stackInput = recipe.getStackInput();
		IBlockStateCondition blockInput = recipe.getBlockInput();
		if (!stackInput.isEmpty()) {
				inputList.add(ImmutableList.of(stackInput));
		}
		if (blockInput instanceof ExactBlockStateCondition) {
				List<ItemStack> blocks = Arrays.stream(
								((ExactBlockStateCondition) blockInput).getStateExpected())
								.map(Util::getStackFromState)
								.filter(Objects::nonNull)
								.collect(Collectors.toList());
				inputList.add(blocks);
		} else if (blockInput instanceof OreDictBlockStateCondition) {
				inputList.add(((OreDictBlockStateCondition) blockInput).getBlocks());
		} else if (blockInput != null) {
				this.blockRequirementString = blockInput.getTooltip();
		}

		NonNullList<ItemStack> outputList = NonNullList.create();
		ItemStack stackOutput = recipe.getStackOutput();
		IBlockState blockOutput = recipe.getBlockOutput();
		if (stackOutput != null) {
				outputList.add(stackOutput);
		}
		if (blockOutput != null) {
				outputList.add(Util.getStackFromState(blockOutput));
		}

		this.input = inputList;
		this.output = outputList;

		if (recipe.getMinFallHeight() > 0 && recipe.getMaxFallHeight() < Integer.MAX_VALUE) {
				// both
				this.heightRequirementString =
								Util.translateFormatted("gui.jei.anvil_smashing.height.both", recipe.getMinFallHeight(), recipe.getMaxFallHeight());
		} else if (recipe.getMinFallHeight() > 0) {
				this.heightRequirementString =
								Util.translateFormatted("gui.jei.anvil_smashing.height.min", recipe.getMinFallHeight());
		} else if (recipe.getMaxFallHeight() < Integer.MAX_VALUE) {
				this.heightRequirementString =
								Util.translateFormatted("gui.jei.anvil_smashing.height.max", recipe.getMaxFallHeight());
		}
	}

	public int getInputCount() {
		return this.input.size();
	}

	public int getOutputCount() {
		return this.output.size();
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		List<String> tooltip = Lists.newArrayList();
		if (mouseX >= 68 && mouseX <= 90 && mouseY >= 17 && mouseY <= 31) {
				if (isNotEmpty(heightRequirementString)) {
						tooltip.add(heightRequirementString);
				}
				if (isNotEmpty(blockRequirementString)) {
						tooltip.add(blockRequirementString);
				}
		}
		return tooltip;
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		if (isNotEmpty(heightRequirementString) || isNotEmpty(blockRequirementString)) {
				minecraft.fontRenderer.drawString("!", 76, 13, Color.gray.getRGB());
		}
	}

	private boolean isNotEmpty(String string) {
		return string != null && !string.isEmpty();
	}
}
