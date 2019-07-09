package knightminer.inspirations.plugins.jei.cauldron.ingredient;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.client.ClientUtil;
import knightminer.inspirations.plugins.jei.cauldron.CauldronRecipeCategory;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public enum PotionIngredientRenderer implements IIngredientRenderer<PotionIngredient> {
	INVENTORY,
	LEVEL_1,
	LEVEL_2,
	LEVEL_3,
	LEVEL_4,
	INVALID;

	private int level;
	PotionIngredientRenderer() {
		this.level = ordinal();
	}

	public static PotionIngredientRenderer forLevel(int level) {
		if(level < 1 || level > 4 || (level == 4 && !Config.enableBiggerCauldron)) {
			return INVALID;
		}

		return values()[level];
	}

	public static final ResourceLocation POTION_TEXTURE = Util.getResource("blocks/fluid_potion");

	@Override
	public void render(Minecraft minecraft, int x, int y, PotionIngredient potion) {
		if(potion == null || level == INVALID.level) {
			return;
		}

		float[] color = Util.getColorComponents(PotionUtils.getPotionColor(potion.getPotion()));
		ClientUtil.renderJEICauldronFluid(minecraft, x, y, POTION_TEXTURE, color, level);
	}

	@Override
	public List<String> getTooltip(Minecraft minecraft, PotionIngredient ingredient, ITooltipFlag tooltipFlag) {
		List<String> tooltip = new ArrayList<>();
		PotionType potion = ingredient.getPotion();
		tooltip.add(Util.translate(potion.getNamePrefixed("potion.effect.")));
		Util.addPotionTooltip(potion, tooltip);
		CauldronRecipeCategory.addLevelTooltip(level, tooltip);
		return tooltip;
	}
}
