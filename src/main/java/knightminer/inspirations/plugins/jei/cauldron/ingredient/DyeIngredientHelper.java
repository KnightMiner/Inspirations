package knightminer.inspirations.plugins.jei.cauldron.ingredient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.google.common.collect.ImmutableList;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.recipes.InspirationsRecipes;
import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;

public enum DyeIngredientHelper implements IIngredientHelper<DyeIngredient> {
	INSTANCE;

	public static final List<DyeIngredient> ALL_DYES;
	static {
		ALL_DYES = Arrays.stream(DyeColor.values())
				.map(DyeIngredient::new)
				.collect(Collectors.toList());
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
		return Util.translateFormatted("gui.jei.cauldron.color", Util.translate("item.fireworksCharge.%s", ingredient.getDye().getName()));
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
	public Iterable<Integer> getColors(DyeIngredient ingredient) {
		return ImmutableList.of(ingredient.getDye().colorValue);
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
		return new ItemStack(InspirationsRecipes.simpleDyedWaterBottle.get(ingredient.getDye()));
	}
}
