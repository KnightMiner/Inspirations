package knightminer.inspirations.plugins.jei.cauldron.ingredient;

import java.awt.Color;
import java.util.List;

import com.google.common.collect.ImmutableList;

import knightminer.inspirations.library.Util;
import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;

public enum PotionIngredientHelper implements IIngredientHelper<PotionType> {
	INSTANCE;

	@Override
	public List<PotionType> expandSubtypes(List<PotionType> ingredients) {
		return ingredients;
	}

	@Override
	public PotionType getMatch(Iterable<PotionType> ingredients, PotionType match) {
		for(PotionType potion : ingredients) {
			if(potion == match) {
				return potion;
			}
		}
		return null;
	}

	@Override
	public String getDisplayName(PotionType potion) {
		return Util.translate(potion.getNamePrefixed("potion.effect."));
	}

	@Override
	public String getUniqueId(PotionType potion) {
		return potion.getRegistryName().toString();
	}

	@Override
	public String getWildcardId(PotionType potion) {
		return getUniqueId(potion);
	}

	@Override
	public String getModId(PotionType potion) {
		return potion.getRegistryName().getResourceDomain();
	}

	@Override
	public Iterable<Color> getColors(PotionType potion) {
		return ImmutableList.of(new Color(PotionUtils.getPotionColor(potion)));
	}

	@Override
	public String getResourceId(PotionType potion) {
		return potion.getRegistryName().getResourcePath();
	}

	@Override
	public PotionType copyIngredient(PotionType potion) {
		return potion;
	}

	@Override
	public String getErrorInfo(PotionType potion) {
		return potion == null ? null : getUniqueId(potion);
	}

	@Override
	public ItemStack getCheatItemStack(PotionType potion) {
		return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), potion);
	}
}
