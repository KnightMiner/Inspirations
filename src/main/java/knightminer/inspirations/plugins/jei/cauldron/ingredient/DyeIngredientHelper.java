package knightminer.inspirations.plugins.jei.cauldron.ingredient;

import java.awt.Color;
import java.util.List;

import com.google.common.collect.ImmutableList;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.recipes.InspirationsRecipes;
import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

public enum DyeIngredientHelper implements IIngredientHelper<EnumDyeColor> {
	INSTANCE;

	@Override
	public List<EnumDyeColor> expandSubtypes(List<EnumDyeColor> ingredients) {
		return ingredients;
	}

	@Override
	public EnumDyeColor getMatch(Iterable<EnumDyeColor> ingredients, EnumDyeColor match) {
		for(EnumDyeColor dye : ingredients) {
			if(dye == match) {
				return dye;
			}
		}
		return null;
	}

	@Override
	public String getDisplayName(EnumDyeColor ingredient) {
		return Util.translateFormatted("gui.jei.cauldron.color", Util.translate("item.fireworksCharge.%s", ingredient.getUnlocalizedName()));
	}

	@Override
	public String getUniqueId(EnumDyeColor ingredient) {
		return getResourceId(ingredient);
	}

	@Override
	public String getWildcardId(EnumDyeColor ingredient) {
		return getUniqueId(ingredient);
	}

	@Override
	public String getModId(EnumDyeColor ingredient) {
		return Inspirations.modID;
	}

	@Override
	public Iterable<Color> getColors(EnumDyeColor ingredient) {
		return ImmutableList.of(new Color(ingredient.colorValue));
	}

	@Override
	public String getResourceId(EnumDyeColor ingredient) {
		return ingredient.getName();
	}

	@Override
	public EnumDyeColor copyIngredient(EnumDyeColor ingredient) {
		return ingredient;
	}

	@Override
	public String getErrorInfo(EnumDyeColor ingredient) {
		return ingredient != null ? getResourceId(ingredient) : null;
	}

	@Override
	public ItemStack getCheatItemStack(EnumDyeColor ingredient) {
		return new ItemStack(InspirationsRecipes.dyedWaterBottle, 1, ingredient.getDyeDamage());
	}
}
