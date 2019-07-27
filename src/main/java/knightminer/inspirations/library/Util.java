package knightminer.inspirations.library;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.util.ReflectionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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

	public static boolean canTranslate(String key) {
		return I18n.canTranslate(key);
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
	public static ItemStack getStackFromState(@Nullable IBlockState state) {
		if (state == null) {
			return ItemStack.EMPTY;
		}
		Block block = state.getBlock();
		// skip air
		if(block == Blocks.AIR) {
			return ItemStack.EMPTY;
		}

		// first try getSilkTouchDrop, which just has to be protected
		ItemStack drop = ReflectionUtil.invokeGetSilkTouchDrop(block, state);
		if( drop != null ) { // stack is null if reflection fails
			return drop;
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
	 * Splits a hex color integer into three float color components between 0 and 1
	 * @param component  float color component array, must be length 3
	 * @return  Color integer value
	 */
	public static int getColorInteger(@Nonnull float[] component) {
		return ((int)(component[0] * 255) & 0xFF) << 16
					 | ((int)(component[1] * 255) & 0xFF) << 8
					 | ((int)(component[2] * 255) & 0xFF);
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

	/**
	 * Gets the dye color for the given color int
	 * @param color  Dye color input
	 * @return  EnumDyeColor matching, or null for no match
	 */
	public static EnumDyeColor getDyeForColor(int color) {
		for(EnumDyeColor dyeColor : EnumDyeColor.values()) {
			if(dyeColor.colorValue == color) {
				return dyeColor;
			}
		}
		return null;
	}

	/**
	 * Returns the closest raytrace result from a list
	 * @param list  List of ray traces
	 * @param end   Ending vector of the trace
	 * @return  Cloest result
	 */
	public static RayTraceResult closestResult(List<RayTraceResult> list, Vec3d end) {
		RayTraceResult closest = null;
		double max = 0.0D;
		for(RayTraceResult raytraceresult : list) {
			if(raytraceresult != null) {
				double distance = raytraceresult.hitVec.squareDistanceTo(end);
				if(distance > max) {
					closest = raytraceresult;
					max = distance;
				}
			}
		}

		return closest;
	}

	/** Checks if the given stack matches the given oredict name */
	public static boolean oreMatches(ItemStack stack, @Nonnull String ore) {
		if (stack.isEmpty()) {
			return false;
		}
		for (int id : OreDictionary.getOreIDs(stack)) {
			if (ore.equals(OreDictionary.getOreName(id))) {
				return true;
			}
		}
		return false;
	}
}
