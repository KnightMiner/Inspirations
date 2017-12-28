package knightminer.inspirations.tweaks.recipe;

import java.util.Optional;

import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.tweaks.InspirationsTweaks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.oredict.DyeUtils;

public class DyeCauldronWater implements ICauldronRecipe {

	public static final DyeCauldronWater INSTANCE = new DyeCauldronWater();
	private DyeCauldronWater() {}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		// special case water bottles
		if(stack.getItem() == InspirationsTweaks.dyedWaterBottle) {
			return false;
		}

		CauldronContents type = state.getType();
		Optional<EnumDyeColor> color = DyeUtils.colorFromStack(stack);
		// type must be water or dye
		// input must be a dye with a different color
		return (type == CauldronContents.WATER || type == CauldronContents.DYE)
				&& color.isPresent() && color.get().getColorValue() != state.getColor();
	}

	@Override
	public CauldronState getState(ItemStack stack, boolean boiling, int level, CauldronState state) {
		int newColor = DyeUtils.colorFromStack(stack).get().getColorValue();
		int color = state.getColor();
		if(color > -1) {
			color = Util.combineColors(newColor, color, level);
		} else {
			color = newColor;
		}

		return CauldronState.dye(color);
	}

	@Override
	public SoundEvent getSound(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return null;
	}
}
