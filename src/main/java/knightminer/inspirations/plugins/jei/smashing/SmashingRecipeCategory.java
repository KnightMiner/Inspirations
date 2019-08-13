package knightminer.inspirations.plugins.jei.smashing;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.Util;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;

public class SmashingRecipeCategory implements IRecipeCategory<SmashingRecipeWrapper> {

	public static final ResourceLocation CATEGORY = Util.getResource("anvil_smashing");
	public static final ResourceLocation BACKGROUND_LOC = Util.getResource("textures/gui/jei/anvil_smashing.png");

	private final IDrawable background;
	private final IDrawable icon;

	public SmashingRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createDrawable(BACKGROUND_LOC, 0, 0, 160, 33);
		icon = guiHelper.createDrawableIngredient(Ingredient.fromItems(Items.ANVIL));
	}

	@Nonnull
	@Override
	public ResourceLocation getUid() {
		return CATEGORY;
	}

	@Override
	public Class<? extends SmashingRecipeWrapper> getRecipeClass() {
		return SmashingRecipeWrapper.class;
	}

	@Nonnull
	@Override
	public String getTitle() {
		return Util.translate("gui.jei.anvil_smashing.title");
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Nonnull
	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setIngredients(SmashingRecipeWrapper recipe, IIngredients ingredients) {
		ingredients.setInputs(VanillaTypes.ITEM,  recipe.input);
		ingredients.setOutputs(VanillaTypes.ITEM, recipe.output);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, SmashingRecipeWrapper recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup items = recipeLayout.getItemStacks();

		items.init(0, true, 43, 15);
		items.set(ingredients);

		items.init(1, false, 97, 15);
		items.set(ingredients);
	}
}
