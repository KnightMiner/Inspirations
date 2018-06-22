package knightminer.inspirations.plugins.jei.cauldron.ingredient;

import java.util.ArrayList;
import java.util.List;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.client.ClientUtil;
import knightminer.inspirations.plugins.jei.cauldron.CauldronRecipeCategory;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.ResourceLocation;

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
			level = 5;
		}

		return values()[level];
	}

	public static final ResourceLocation DYE_TEXTURE = Util.getResource("blocks/fluid_colorless");

	@Override
	public void render(Minecraft minecraft, int x, int y, DyeIngredient dye) {
		// level of 4 is the invalid state
		if(dye == null || level == 4) {
			return;
		}
		GlStateManager.enableBlend();
		minecraft.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		float[] color = dye.getDye().getColorComponentValues();
		GlStateManager.color(color[0], color[1], color[2]);
		// 0 means JEI ingredient list
		TextureAtlasSprite sprite = ClientUtil.getSprite(DYE_TEXTURE);
		if(level == 0) {
			ClientUtil.renderFilledSprite(sprite, x, y, 16, 16);
		} else {
			int height = ((10 * level) / InspirationsRegistry.getCauldronMax());
			ClientUtil.renderFilledSprite(sprite, x, y, 10, height);
		}
		GlStateManager.color(1, 1, 1);
		GlStateManager.disableBlend();
	}

	@Override
	public List<String> getTooltip(Minecraft minecraft, DyeIngredient ingredient, ITooltipFlag tooltipFlag) {
		List<String> tooltip = new ArrayList<>();
		tooltip.add(Util.translateFormatted("gui.jei.cauldron.color", Util.translate("item.fireworksCharge.%s", ingredient.getDye().getUnlocalizedName())));
		CauldronRecipeCategory.addLevelTooltip(level, tooltip);
		return tooltip;
	}
}
