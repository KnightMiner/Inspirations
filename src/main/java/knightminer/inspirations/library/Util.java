package knightminer.inspirations.library;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import knightminer.inspirations.Inspirations;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@SuppressWarnings("deprecation")
public class Util {
	public static String resource(String name) {
		return String.format("%s:%s", Inspirations.modID, name.toLowerCase(Locale.US));
	}
	public static String prefix(String name) {
		return String.format("%s.%s", Inspirations.modID, name.toLowerCase(Locale.US));
	}

	public static ResourceLocation getResource(String res) {
		return new ResourceLocation(Inspirations.modID, res);
	}

	/**
	 * Translate the string, insert parameters into the translation key
	 */
	public static String translate(String key, Object... pars) {
		// translates twice to allow rerouting/alias
		return I18n.translateToLocal(I18n.translateToLocal(String.format(key, pars)).trim()).trim();
	}

	/**
	 * Translate the string, insert parameters into the result of the translation
	 */
	public static String translateFormatted(String key, Object... pars) {
		// translates twice to allow rerouting/alias
		return I18n.translateToLocal(I18n.translateToLocalFormatted(key, pars).trim()).trim();
	}

	public static Logger getLogger(String type) {
		String log = Inspirations.modID;

		return LogManager.getLogger(log + "-" + type);
	}

	public static boolean clickedAABB(AxisAlignedBB aabb, float hitX, float hitY, float hitZ) {
		return aabb.minX <= hitX && hitX <= aabb.maxX
				&& aabb.minY <= hitY && hitY <= aabb.maxY
				&& aabb.minZ <= hitZ && hitZ <= aabb.maxZ;
	}

	/**
	 * Gets an item stack from a block state. Uses Block::getSilkTouchDrop
	 * @param state  Input state
	 * @return  ItemStack for the state, or ItemStack.EMPTY if a valid item cannot be found
	 */
	public static ItemStack getStackFromState(IBlockState state) {
		Block block = state.getBlock();
		// skip air
		if(block == Blocks.AIR) {
			return ItemStack.EMPTY;
		}

		// first try getSilkTouchDrop, which just has to be protected
		try {
			Object stack = ReflectionHelper.findMethod(Block.class, "getSilkTouchDrop", "func_180643_i", IBlockState.class).invoke(block, state);
			if(stack instanceof ItemStack) {
				return (ItemStack) stack;
			}
		} catch (Exception e) {
			InspirationsRegistry.log.error(e);
		}

		// if it fails, do a fallback of damageDropped and item.getItemFromBlock
		InspirationsRegistry.log.error("Failed to get silk touch drop for {}, using fallback", state);

		// fallback, use item and damage dropped
		Item item = Item.getItemFromBlock(block);
		if(item == Items.AIR) {
			return ItemStack.EMPTY;
		}
		int meta = block.damageDropped(state);
		return new ItemStack(item, 1, meta);
	}

	/**
	 * Creates a NonNullList from the specified elements, using the class as the type
	 * @param elements  Elements for the list
	 * @return  New NonNullList
	 */
	@SafeVarargs
	public static <E> NonNullList<E> createNonNullList(E... elements) {
		NonNullList<E> list = NonNullList.create();
		list.addAll(Arrays.asList(elements));
		return list;
	}

	/**
	 * Combines two colors
	 * @param color1  First color
	 * @param color2  Second color
	 * @param scale  Determines how many times color2 is applied
	 * @return  Combined color
	 */
	public static int combineColors(int color1, int color2, int scale) {
		if(scale == 0) {
			return color1;
		}
		int a = color1 >> 24 & 0xFF;
		int r = color1 >> 16 & 0xFF;
		int g = color1 >> 8 & 0xFF;
		int b = color1 & 0xFF;
		int a2 = color2 >> 24 & 0xFF;
		int r2 = color2 >> 16 & 0xFF;
		int g2 = color2 >> 8 & 0xFF;
		int b2 = color2 & 0xFF;

		for(int i = 0; i < scale; i++) {
			a = (int) Math.sqrt(a * a2);
			r = (int) Math.sqrt(r * r2);
			g = (int) Math.sqrt(g * g2);
			b = (int) Math.sqrt(b * b2);
		}
		return a << 24 | r << 16 | g << 8 | b;
	}

	/**
	 * Splits a hex color integer into three float color components between 0 and 1
	 * @param color  Input color
	 * @return  Floats for the color
	 */
	public static float[] getColorComponents(int color) {
		int i = (color & 0xFFFFFF) >> 16;
		int j = (color & 0xFFFF) >> 8;
		int k = (color & 0xFF);
		return new float[] {i / 255.0f, j / 255.0f, k / 255.0f};
	}

	/**
	 * Adds the tooltips for the potion type into the given string list
	 * @param potionType  Potion type input
	 * @param lores       List to add the tooltips into
	 */
	public static void addPotionTooltip(PotionType potionType, List<String> lores) {
		List<PotionEffect> effects = potionType.getEffects();

		if (effects.isEmpty()) {
			String s = translate("effect.none").trim();
			lores.add(TextFormatting.GRAY + s);
			return;
		}

		for (PotionEffect effect : effects) {
			String effectString = translate(effect.getEffectName()).trim();
			Potion potion = effect.getPotion();

			if (effect.getAmplifier() > 0) {
				effectString += " " + translate("potion.potency." + effect.getAmplifier()).trim();
			}
			if (effect.getDuration() > 20) {
				effectString += " (" + Potion.getPotionDurationString(effect, 1.0f) + ")";
			}
			lores.add((potion.isBadEffect() ? TextFormatting.RED : TextFormatting.BLUE) + effectString);
		}
	}
}
