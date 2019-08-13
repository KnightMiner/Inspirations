package knightminer.inspirations.plugins.jei.cauldron.ingredient;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.collect.ImmutableList;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.Util;
import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nonnull;

public enum PotionIngredientHelper implements IIngredientHelper<PotionIngredient> {
	INSTANCE;

	public static final List<PotionIngredient> ALL_POTIONS;
	static {
		ALL_POTIONS = StreamSupport.stream(Registry.POTION.spliterator(), false)
				.filter(type->type != Potions.EMPTY && type != Potions.WATER).map(PotionIngredient::new)
				.collect(Collectors.toList());
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
		return potion.getPotion().getRegistryName().getNamespace();
	}

	@Override
	public Iterable<Integer> getColors(PotionIngredient potion) {
		return ImmutableList.of(PotionUtils.getPotionColor(potion.getPotion()));
	}

	@Override
	public String getResourceId(PotionIngredient potion) {
		return potion.getPotion().getRegistryName().getPath();
	}

	@Nonnull
	@Override
	public PotionIngredient copyIngredient(@Nonnull PotionIngredient potion) {
		return potion;
	}

	@Override
	public String getErrorInfo(PotionIngredient potion) {
		return potion == null || potion.getPotion() == null ? "null" : getUniqueId(potion);
	}

	@Override
	public ItemStack getCheatItemStack(PotionIngredient potion) {
		return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), potion.getPotion());
	}
}
