package knightminer.inspirations.plugins.jei.cauldron.ingredient;

import java.awt.Color;
import java.util.List;

import com.google.common.collect.ImmutableList;

import knightminer.inspirations.library.Util;
import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;

public enum PotionIngredientHelper implements IIngredientHelper<PotionIngredient> {
	INSTANCE;

	@Override
	public List<PotionIngredient> expandSubtypes(List<PotionIngredient> ingredients) {
		return ingredients;
	}

	@Override
	public PotionIngredient getMatch(Iterable<PotionIngredient> ingredients, PotionIngredient match) {
		for(PotionIngredient potion : ingredients) {
			if(potion.getPotion() == match.getPotion()) {
				return potion;
			}
		}
		return null;
	}

	@Override
	public String getDisplayName(PotionIngredient potion) {
		return Util.translate(potion.getPotion().getNamePrefixed("potion.effect."));
	}

	@Override
	public String getUniqueId(PotionIngredient potion) {
		return potion.getPotion().getRegistryName().toString();
	}

	@Override
	public String getWildcardId(PotionIngredient potion) {
		return getUniqueId(potion);
	}

	@Override
	public String getModId(PotionIngredient potion) {
		return potion.getPotion().getRegistryName().getResourceDomain();
	}

	@Override
	public Iterable<Color> getColors(PotionIngredient potion) {
		return ImmutableList.of(new Color(PotionUtils.getPotionColor(potion.getPotion())));
	}

	@Override
	public String getResourceId(PotionIngredient potion) {
		return potion.getPotion().getRegistryName().getResourcePath();
	}

	@Override
	public PotionIngredient copyIngredient(PotionIngredient potion) {
		return potion;
	}

	@Override
	public String getErrorInfo(PotionIngredient potion) {
		return potion == null || potion.getPotion() == null ? "null" : getUniqueId(potion);
	}

	@Override
	public ItemStack getCheatItemStack(PotionIngredient potion) {
		return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), potion.getPotion());
	}
}
