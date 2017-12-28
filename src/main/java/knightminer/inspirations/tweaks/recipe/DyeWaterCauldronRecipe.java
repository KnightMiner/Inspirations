package knightminer.inspirations.tweaks.recipe;

import java.util.Optional;

import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.DyeUtils;

public class DyeWaterCauldronRecipe implements ICauldronRecipe {

	public static final DyeWaterCauldronRecipe INSTANCE = new DyeWaterCauldronRecipe();
	private DyeWaterCauldronRecipe() {}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
		ICauldronRecipe.CauldronContents type = state.getType();
		Optional<EnumDyeColor> color = DyeUtils.colorFromStack(stack);
		// type must be water or dye
		// input must be a dye with a different color
		return (type == ICauldronRecipe.CauldronContents.WATER || type == ICauldronRecipe.CauldronContents.DYE)
				&& color.isPresent() && color.get().getColorValue() != state.getColor();
	}

	@Override
	public ICauldronRecipe.CauldronState getState(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
		int newColor = DyeUtils.colorFromStack(stack).get().getColorValue();
		int color = state.getColor();
		if(color > -1) {
			color = Util.combineColors(newColor, color, level);
		} else {
			color = newColor;
		}

		return CauldronState.dye(color);
	}
}
