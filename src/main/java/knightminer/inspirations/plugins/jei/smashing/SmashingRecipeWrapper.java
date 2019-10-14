package knightminer.inspirations.plugins.jei.smashing;

import com.google.common.collect.ImmutableList;

import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.List;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import slimeknights.mantle.client.CreativeTab;

public class SmashingRecipeWrapper implements IRecipeWrapper {

	protected final ItemStack input;
	protected final ItemStack output;

	public SmashingRecipeWrapper(ItemStack input, ItemStack output) {
		this.input = input;
		this.output = output;
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
}
