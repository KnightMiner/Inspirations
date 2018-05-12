package knightminer.inspirations.plugins.jei.cauldron;

import java.util.ArrayList;
import java.util.List;

import knightminer.inspirations.library.Util;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron.CauldronContents;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

public interface ICauldronRecipeWrapper extends IRecipeWrapper {
	/**
	 * Gets the input cauldron fluid type for the recipe
	 * @return  Input cauldron fluid type
	 */
	CauldronContents getInputType();
	/**
	 * Gets the output cauldron fluid type for the recipe
	 * @return  Output cauldron fluid type
	 */
	CauldronContents getOutputType();
	/**
	 * Gets the input cauldron fluid level for the recipe
	 * @return  Input cauldron fluid level
	 */
	int getInputLevel();
	/**
	 * Gets the output cauldron fluid level for the recipe
	 * @return  Output cauldron fluid level
	 */
	int getOutputLevel();

	/**
	 * Gets the item to be used for a focus for potion ingredients
	 * @return ItemStack for a potion focus, or EMPTY if not a potion recipe
	 */
	default ItemStack getPotionItem() {
		return ItemStack.EMPTY;
	}

	@Override
	default List<String> getTooltipStrings(int mouseX, int mouseY) {
		List<String> tooltip = new ArrayList<>();
		addStringTooltip(tooltip, getInputLevel(), 47, 19, mouseX, mouseY);
		addStringTooltip(tooltip, getOutputLevel(), 101, 19, mouseX, mouseY);

		return tooltip;
	}

	/**
	 * Adds the empty string for the input or output if relevant
	 */
	public static void addStringTooltip(List<String> tooltips, int level, int x, int y, int mouseX, int mouseY) {
		if(level == 0 && mouseX >= x && mouseX < x + 10 && mouseY >= y && mouseY < y + 10) {
			tooltips.add(Util.translateFormatted("gui.jei.cauldron.level.empty"));
		}
	}
}
