package knightminer.inspirations.plugins.jei.cauldron;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.plugins.jei.cauldron.ingredient.DyeIngredient;
import knightminer.inspirations.plugins.jei.cauldron.ingredient.DyeIngredientRenderer;
import knightminer.inspirations.plugins.jei.cauldron.ingredient.PotionIngredient;
import knightminer.inspirations.plugins.jei.cauldron.ingredient.PotionIngredientRenderer;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron.CauldronContents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;

public class CauldronRecipeCategory implements IRecipeCategory<CauldronRecipeWrapper> {

	public static final String CATEGORY = Util.prefix("cauldron");
	public static final ResourceLocation BACKGROUND_LOC = Util.getResource("textures/gui/jei/cauldron.png");

	private final IDrawable background;
	public final IDrawable fire;

	public CauldronRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createDrawable(BACKGROUND_LOC, 0, 0, 160, 50, 0, 0, 0, 0);

		fire = guiHelper.createDrawable(BACKGROUND_LOC, 160, 0, 14, 14);
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

		items.init(0, true, 43, 0);
		items.set(ingredients);
		items.init(1, false, 97, 0);
		items.set(ingredients);

		IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
		fluids.addTooltipCallback(CauldronRecipeCategory::onFluidTooltip);
		IGuiIngredientGroup<DyeIngredient> dyes = recipeLayout.getIngredientsGroup(DyeIngredient.class);
		IGuiIngredientGroup<PotionIngredient> potions = recipeLayout.getIngredientsGroup(PotionIngredient.class);

		init(fluids, dyes, potions, ingredients, true, 47, 19, recipe.getInputType(), recipe.getInputLevel());
		init(fluids, dyes, potions, ingredients, false, 101, 19, recipe.getOutputType(), recipe.getOutputLevel());
	}

	/**
	 * Helper method to call init on the relevant GUI group
	 */
	private static void init(IGuiFluidStackGroup fluids, IGuiIngredientGroup<DyeIngredient> dyes, IGuiIngredientGroup<PotionIngredient> potions,
			IIngredients ingredients, boolean input, int x, int y, CauldronContents type, int level) {
		if(type == null) {
			return;
		}
		int index = input ? 0 : 1;
		switch(type) {
			case FLUID:
				fluids.init(index, input, x, y, 10, 10, 3, false, null);
				fluids.set(ingredients);
				break;
			case DYE:
				dyes.init(index, input, DyeIngredientRenderer.forLevel(level), x, y, 10, 10, 0, 0);
				dyes.set(ingredients);
				break;
			case POTION:
				potions.init(index, input, PotionIngredientRenderer.forLevel(level), x, y, 10, 10, 0, 0);
				potions.set(ingredients);
				break;
		}
	}

	private static void onFluidTooltip(int slotIndex, boolean input, FluidStack ingredient, List<String> tooltip) {
		String modName = tooltip.get(tooltip.size() - 1);
		tooltip.clear();
		tooltip.add(ingredient.getLocalizedName());
		addLevelTooltip(ingredient.amount, tooltip);
		tooltip.add(modName);
	}

	public static void addLevelTooltip(int level, List<String> tooltip) {
		if(level == 1) {
			tooltip.add(TextFormatting.GRAY + Util.translateFormatted("gui.jei.cauldron.level.singular"));
		} else if(level > 1) {
			tooltip.add(TextFormatting.GRAY + Util.translateFormatted("gui.jei.cauldron.level", level));
		}
	}

	@Override
	public String getModName() {
		return Inspirations.modName;
	}
}
