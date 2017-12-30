package knightminer.inspirations.plugins.jei.cauldron;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.Util;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;

public class CauldronRecipeCategory implements IRecipeCategory<CauldronRecipeWrapper> {

	public static final String CATEGORY = Util.prefix("cauldron");
	public static final ResourceLocation BACKGROUND_LOC = Util.getResource("textures/gui/jei/cauldron.png");

	private final IDrawable background;
	public final IDrawable fire;
	public final IDrawable[] dye;
	public final IDrawable[] potion;

	public CauldronRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createDrawable(BACKGROUND_LOC, 0, 0, 160, 60, 0, 0, 0, 0);

		fire = guiHelper.createDrawable(BACKGROUND_LOC, 160, 0, 14, 14);
		dye = new IDrawable[]{
				guiHelper.createDrawable(BACKGROUND_LOC, 160, 20, 10,  4),
				guiHelper.createDrawable(BACKGROUND_LOC, 160, 17, 10,  7),
				guiHelper.createDrawable(BACKGROUND_LOC, 160, 14, 10, 10)
		};
		potion = new IDrawable[]{
				guiHelper.createDrawable(BACKGROUND_LOC, 160, 30, 10,  4),
				guiHelper.createDrawable(BACKGROUND_LOC, 160, 27, 10,  7),
				guiHelper.createDrawable(BACKGROUND_LOC, 160, 24, 10, 10)
		};
	}

	@Nonnull
	@Override
	public String getUid() {
		return CATEGORY;
	}

	@Nonnull
	@Override
	public String getTitle() {
		return Util.translate("gui.jei.cauldron.title");
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CauldronRecipeWrapper recipe, IIngredients ingredients) {
		IGuiItemStackGroup items = recipeLayout.getItemStacks();

		items.init(0, true, 43, 6);
		items.set(ingredients);
		items.init(1, false, 97, 6);
		items.set(ingredients);

		IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
		if(recipe.hasInputFluid()) {
			fluids.init(0, true, 47, 25, 10, 10, 1000, false, null);
			fluids.set(ingredients);
		}
		if(recipe.hasOutputFluid()) {
			fluids.init(1, false, 101, 25, 10, 10, 1000, false, null);
			fluids.set(ingredients);
		}
	}

	@Override
	public String getModName() {
		return Inspirations.modName;
	}
}
