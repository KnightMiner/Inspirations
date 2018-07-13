package knightminer.inspirations.plugins.jei.smashing;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.Util;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class SmashingItemRecipeCategory implements IRecipeCategory<SmashingItemRecipeWrapper> {

	public static final String CATEGORY = Util.prefix("anvil_smashing_items");
	public static final ResourceLocation BACKGROUND_LOC = Util.getResource("textures/gui/jei/anvil_smashing.png");

	private final IDrawable background;

	public SmashingItemRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createDrawable(BACKGROUND_LOC, 0, 0, 160, 33, 0, 4, 0, 0);
	}

	@Nonnull
	@Override
	public String getUid() {
		return CATEGORY;
	}

	@Nonnull
	@Override
	public String getTitle() {
		return Util.translate("gui.jei.anvil_smashing_items.title");
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, SmashingItemRecipeWrapper recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup items = recipeLayout.getItemStacks();

		int slot = 0;

		// Input
		items.init(slot++, true, 43, 15);
		int numAdditionalInputs = ingredients.getInputs(ItemStack.class).size() - 1;
		for(int i = 0; i < Math.min(numAdditionalInputs, 3); i++) {
			items.init(slot++, true, 43 - 20*(i+1), 15);
		}
		items.set(ingredients);

		// Output
		items.init(slot++, false, 97, 15);
		int numAdditionalOutputs = ingredients.getOutputs(ItemStack.class).size() - 1;
		for(int i = 0; i < Math.min(numAdditionalOutputs, 3); i++) {
			items.init(slot++, false, 97 + 20*(i+1), 15);
		}
		items.set(ingredients);
	}

	@Override
	public String getModName() {
		return Inspirations.modName;
	}
}
