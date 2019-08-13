package knightminer.inspirations.plugins.jei.texture;

import com.google.common.collect.ImmutableList;

import knightminer.inspirations.library.recipe.TextureRecipe;
import knightminer.inspirations.library.util.TextureBlockUtil;
import knightminer.inspirations.plugins.jei.JEIPlugin;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.Size2i;
import net.minecraftforge.oredict.OreDictionary;
import java.util.List;

import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.wrapper.ICustomCraftingRecipeWrapper;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;

import javax.annotation.Nullable;

public class TextureRecipeWrapper implements ICraftingCategoryExtension {

	private final TextureRecipe recipe;
	private final int width;
	private final int height;
	private final List<List<ItemStack>> outputs;

	public TextureRecipeWrapper(TextureRecipe recipe) {
		this.recipe = recipe;

		for(Object input : this.recipe.getIngredients()) {
			if(input instanceof ItemStack) {
				ItemStack itemStack = (ItemStack) input;
				if(itemStack.getCount() != 1) {
					itemStack.setCount(1);
				}
			}
		}

		this.width = recipe.getRecipeWidth();
		this.height = recipe.getRecipeHeight();

		// sort the output entries into lists of items
		ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
		Block block = Block.getBlockFromItem(recipe.getRecipeOutput().getItem());
		for(ItemStack stack : recipe.texture.getMatchingStacks()) {
			Block textureBlock = Block.getBlockFromItem(stack.getItem());
			if(stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
				for(ItemStack sub : JEIPlugin.jeiHelpers.getStackHelper().getSubtypes(stack)) {
					builder.add(TextureBlockUtil.createTexturedStack(block, recipe.getRecipeOutput().getItemDamage(), textureBlock, sub.getItemDamage()));
				}
			}
			else {
				builder.add(TextureBlockUtil.createTexturedStack(block, recipe.getRecipeOutput().getItemDamage(), textureBlock, stack.getItemDamage()));
			}
		}
		outputs = ImmutableList.of(builder.build());
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		IStackHelper stackHelper = JEIPlugin.jeiHelpers.getStackHelper();

		List<List<ItemStack>> inputs = stackHelper.expandRecipeItemStackInputs(recipe.getIngredients());
		ingredients.setInputLists(ItemStack.class, inputs);

		//ItemStack recipeOutput = recipe.getRecipeOutput();
		if(!outputs.isEmpty()) {
			ingredients.setOutputLists(ItemStack.class, outputs);
		}
	}

	@Nullable
	@Override
	public Size2i getSize() {
		return new Size2i(width, height);
	}

	private boolean isOutputBlock(ItemStack stack) {
		if(stack.isEmpty()) {
			return false;
		}

		for(ItemStack output : recipe.texture.getMatchingStacks()) {
			// if the item matches the oredict entry, it is an output block
			if(stack.isItemEqual(output)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<ItemStack> outputs = ingredients.getOutputs(ItemStack.class).get(0);

		// determine the focused stack
		IFocus<?> ifocus = recipeLayout.getFocus();
		if(ifocus != null && ifocus.getValue() instanceof ItemStack) {
			IGuiIngredientGroup<ItemStack> guiIngredients = recipeLayout.getIngredientsGroup(ItemStack.class);
			ItemStack focus = (ItemStack) ifocus.getValue();
			IFocus.Mode mode = ifocus.getMode();

			// input means we clicked on an ingredient, make sure it is one that affects the legs
			if(mode == IFocus.Mode.INPUT && isOutputBlock(focus)) {
				// first, get the output recipe
				ItemStack output = recipe.getRecipeOutput();
				Block block = Block.getBlockFromItem(output.getItem());

				// then create a stack with the focus item (which we already validated above)
				ItemStack outputFocus = TextureBlockUtil.createTexturedStack(block, Block.getBlockFromItem(focus.getItem()));

				// and finally, set the focus override for the recipe
				guiIngredients.setOverrideDisplayFocus(JEIPlugin.recipeRegistry.createFocus(IFocus.Mode.OUTPUT, outputFocus));
			}

			// if we clicked the table, remove all items which affect the legs textures that are not the leg item
			else if(mode == IFocus.Mode.OUTPUT) {
				// so determine the legs
				ItemStack legs = TextureBlockUtil.getStackTexture(focus);
				if(!legs.isEmpty()) {
					// and loop through all slots removing leg affecting inputs which don't match
					guiIngredients.setOverrideDisplayFocus(JEIPlugin.recipeRegistry.createFocus(IFocus.Mode.INPUT, legs));
				}
			}
		}

		// add the itemstacks to the grid
		JEIPlugin.craftingGridHelper.setInputs(guiItemStacks, inputs, this.getWidth(), this.getHeight());
		recipeLayout.getItemStacks().set(0, outputs);
	}
}
