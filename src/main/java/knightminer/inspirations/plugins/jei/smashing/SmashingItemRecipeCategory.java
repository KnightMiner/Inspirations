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
		background = guiHelper.createDrawable(BACKGROUND_LOC, 0, -14, 160, 50, 0, 4, 0, 0);
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

	private static final int ABOVE_Y = -6;

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, SmashingItemRecipeWrapper recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup items = recipeLayout.getItemStacks();

		int slot = 0;

		// Input
		items.init(slot++, true, 43, ABOVE_Y);
		int numAdditionalInputs = ingredients.getInputs(ItemStack.class).size() - 1;
		for(int i = 0; i < Math.min(numAdditionalInputs, 3); i++) {
			items.init(slot++, true, 43 - 20*(i+1), ABOVE_Y);
		}
		items.set(ingredients);

		int finalSlot = slot;
		slot += recipeWrapper.getRecipe().getState().map(state -> {
			items.init(finalSlot, true, 43, 32);
			items.set(finalSlot, new ItemStack(state.getBlock()));
			return 1;
		}).orElse(0);

		// Output
		items.init(slot++, false, 97, ABOVE_Y);
		int numAdditionalOutputs = ingredients.getOutputs(ItemStack.class).size() - 1;
		for(int i = 0; i < Math.min(numAdditionalOutputs, 3); i++) {
			items.init(slot++, false, 97 + 20*(i+1), ABOVE_Y);
		}
		items.set(ingredients);
	}

	@Override
	public String getModName() {
		return Inspirations.modName;
	}
}
