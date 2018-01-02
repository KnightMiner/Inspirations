package knightminer.inspirations.plugins.jei.cauldron.ingredient;

import java.awt.Color;
import java.util.List;

import com.google.common.collect.ImmutableList;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.recipes.InspirationsRecipes;
import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.item.ItemStack;

public enum DyeIngredientHelper implements IIngredientHelper<DyeIngredient> {
	INSTANCE;

	@Override
	public List<DyeIngredient> expandSubtypes(List<DyeIngredient> ingredients) {
		return ingredients;
	}

	@Override
	public DyeIngredient getMatch(Iterable<DyeIngredient> ingredients, DyeIngredient match) {
		for(DyeIngredient dye : ingredients) {
			if(dye.getDye() == match.getDye()) {
				return dye;
			}
		}
		return null;
	}

	@Override
	public String getDisplayName(DyeIngredient ingredient) {
		return Util.translateFormatted("gui.jei.cauldron.color", Util.translate("item.fireworksCharge.%s", ingredient.getDye().getUnlocalizedName()));
	}

	@Override
	public String getUniqueId(DyeIngredient ingredient) {
		return getResourceId(ingredient);
	}

	@Override
	public String getWildcardId(DyeIngredient ingredient) {
		return getUniqueId(ingredient);
	}

	@Override
	public String getModId(DyeIngredient ingredient) {
		return Inspirations.modID;
	}

	@Override
	public Iterable<Color> getColors(DyeIngredient ingredient) {
		return ImmutableList.of(new Color(ingredient.getDye().colorValue));
	}

	@Override
	public String getResourceId(DyeIngredient ingredient) {
		return ingredient.getDye().getName();
	}

	@Override
	public DyeIngredient copyIngredient(DyeIngredient ingredient) {
		return ingredient;
	}

	@Override
	public String getErrorInfo(DyeIngredient ingredient) {
		return ingredient != null && ingredient.getDye() != null ? getResourceId(ingredient) : null;
	}

	@Override
	public ItemStack getCheatItemStack(DyeIngredient ingredient) {
		return new ItemStack(InspirationsRecipes.dyedWaterBottle, 1, ingredient.getDye().getDyeDamage());
	}
}
