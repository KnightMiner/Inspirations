package knightminer.inspirations.plugins.jei;

import com.google.common.collect.ImmutableList;
import knightminer.inspirations.library.recipe.crafting.TextureRecipe;
import knightminer.inspirations.library.util.TextureBlockUtil;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICustomCraftingCategoryExtension;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Size2i;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class TextureRecipeExtension implements ICraftingCategoryExtension, ICustomCraftingCategoryExtension {

	private final TextureRecipe recipe;
	private final Size2i size;
	/** List of all possible outputs, for the sake of recipe lookups */
	private final List<List<ItemStack>> allOutputs;
	/** List of all textured variants, fallback for JEI display */
	private final List<ItemStack> displayOutputs;

	public TextureRecipeExtension(TextureRecipe recipe) {
		this.recipe = recipe;

		this.size = new Size2i(recipe.getRecipeWidth(), recipe.getRecipeHeight());

		// gets the outputs of this recipe
		ItemStack output = this.recipe.getRecipeOutput();
		// fetch all stacks from the ingredient, note any variants that are not blocks will get a blank shelf
		List<ItemStack> displayVariants = Arrays.stream(recipe.texture.getMatchingStacks())
		                                        .map((s) -> TextureBlockUtil.setStackTexture(output.copy(), Block.getBlockFromItem(s.getItem())))
		                                        .collect(Collectors.toList());
		// all variants needs blank specifically added so recipe lookup works right
		ImmutableList.Builder<ItemStack> builder = new ImmutableList.Builder<>();
		builder.addAll(displayVariants);
		builder.add(output);
		// we have all variants done
		List<ItemStack> allVariants = builder.build();
		this.allOutputs = ImmutableList.of(allVariants);
		// empty display means the tag found nothing
		this.displayOutputs = displayVariants.isEmpty() ? allVariants : displayVariants;
	}

	@Override
	@Nullable
	public ResourceLocation getRegistryName() {
		return this.recipe.getId();
	}

	@Override
	public void setIngredients(IIngredients ingredients) {
		ingredients.setInputIngredients(this.recipe.getIngredients());
		// use all list so textureless works for the lookup
		ingredients.setOutputLists(VanillaTypes.ITEM, allOutputs);
	}

	@Override
	public Size2i getSize() {
		return size;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
		List<ItemStack> outputs = displayOutputs;

		// determine the focused stack
		IFocus<?> ifocus = recipeLayout.getFocus();
		if(ifocus != null && ifocus.getValue() instanceof ItemStack) {
			IGuiIngredientGroup<ItemStack> guiIngredients = recipeLayout.getIngredientsGroup(VanillaTypes.ITEM);
			ItemStack focus = (ItemStack) ifocus.getValue();
			IFocus.Mode mode = ifocus.getMode();

			// input means we clicked on an ingredient, so if it affects the texture set the output texture
			if(mode == IFocus.Mode.INPUT && recipe.texture.test(focus)) {
				outputs = ImmutableList.of(TextureBlockUtil.setStackTexture(recipe.getRecipeOutput().copy(), Block.getBlockFromItem(focus.getItem())));
			}

			// if we clicked the textured block, remove all items which affect the texture that are not the proper texture
			else if(mode == IFocus.Mode.OUTPUT) {
				// the focus might not be the same count as the output
				ItemStack output = focus.copy();
				output.setCount(recipe.getRecipeOutput().getCount());
				outputs = ImmutableList.of(output);

				// focus texture may be undefined for the mixed planks bookshelf or missing NBT
				Block textureBlock = TextureBlockUtil.getTextureBlock(focus);
				if(textureBlock != Blocks.AIR) {
					guiIngredients.setOverrideDisplayFocus(JEIPlugin.recipeManager.createFocus(IFocus.Mode.INPUT, new ItemStack(textureBlock)));
				}
			}
		}

		// add the itemstacks to the grid
		JEIPlugin.vanillaCraftingHelper.setInputs(guiItemStacks, inputs, size.width, size.height);
		guiItemStacks.set(0, outputs);

		// reimplemeting JEI logic
		ResourceLocation registryName = this.getRegistryName();
		if (registryName != null) {
			guiItemStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
				if (slotIndex == 0) {
					if (JEIPlugin.modIdHelper.isDisplayingModNameEnabled()) {
						String recipeModId = registryName.getNamespace();
						boolean modIdDifferent = false;
						ResourceLocation itemRegistryName = ingredient.getItem().getRegistryName();
						if (itemRegistryName != null) {
							String itemModId = itemRegistryName.getNamespace();
							modIdDifferent = !recipeModId.equals(itemModId);
						}

						if (modIdDifferent) {
							String modName = JEIPlugin.modIdHelper.getFormattedModNameForModId(recipeModId);
							tooltip.add(new TranslationTextComponent("jei.tooltip.recipe.by", modName).mergeStyle(TextFormatting.GRAY));
						}
					}

					boolean showAdvanced = Minecraft.getInstance().gameSettings.advancedItemTooltips || Screen.hasShiftDown();
					if (showAdvanced) {
						tooltip.add(new TranslationTextComponent("jei.tooltip.recipe.id", registryName).mergeStyle(TextFormatting.DARK_GRAY));
					}
				}
			});
		}
	}
}
