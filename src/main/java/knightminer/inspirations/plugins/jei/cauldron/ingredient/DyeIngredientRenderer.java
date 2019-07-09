package knightminer.inspirations.plugins.jei.cauldron.ingredient;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.client.ClientUtil;
import knightminer.inspirations.plugins.jei.cauldron.CauldronRecipeCategory;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public enum DyeIngredientRenderer implements IIngredientRenderer<DyeIngredient> {
	INVENTORY,
	LEVEL_1,
	LEVEL_2,
	LEVEL_3,
	LEVEL_4,
	INVALID;

	private int level;
	DyeIngredientRenderer() {
		this.level = ordinal();
	}

	public static DyeIngredientRenderer forLevel(int level) {
		if(level < 1 || level > 4 || (level == 4 && !Config.enableBiggerCauldron)) {
			return INVALID;
		}

		return values()[level];
	}

	public static final ResourceLocation DYE_TEXTURE = Util.getResource("blocks/fluid_colorless");

	@Override
	public void render(Minecraft minecraft, int x, int y, DyeIngredient dye) {
		// level of 5 is the invalid state
		if(dye == null || level == INVALID.level) {
			return;
		}

		float[] color = dye.getDye().getColorComponentValues();
		ClientUtil.renderJEICauldronFluid(minecraft, x, y, DYE_TEXTURE, color, level);
	}

	@Override
	public List<String> getTooltip(Minecraft minecraft, DyeIngredient ingredient, ITooltipFlag tooltipFlag) {
		List<String> tooltip = new ArrayList<>();
		tooltip.add(Util.translateFormatted("gui.jei.cauldron.color", Util.translate("item.fireworksCharge.%s", ingredient.getDye().getUnlocalizedName())));
		CauldronRecipeCategory.addLevelTooltip(level, tooltip);
		return tooltip;
	}
}
