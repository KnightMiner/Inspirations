package knightminer.inspirations.library;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import knightminer.inspirations.library.util.ReflectionUtil;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSet;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import knightminer.inspirations.Inspirations;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

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
	@Deprecated
	public static String translate(String key, Object... pars) {
		// translates twice to allow rerouting/alias
		String trans = new TranslationTextComponent(String.format(key, pars)).getUnformattedComponentText().trim();
		return new TranslationTextComponent(trans).getUnformattedComponentText().trim();
	}

	/**
	 * Translate the string, insert parameters into the result of the translation
	 */
	@Deprecated
	public static String translateFormatted(String key, Object... pars) {
		// translates twice to allow rerouting/alias
		String trans = new TranslationTextComponent(key, pars).getUnformattedComponentText().trim();
		return new TranslationTextComponent(trans).getUnformattedComponentText().trim();
	}

	@Deprecated
	public static boolean canTranslate(String key) {
		return true;
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

	// An item with Silk Touch, to make blocks drop their silk touch items if they have any.
	// Using a Stick makes sure it won't be damaged.
	private static ItemStack silkTouchItem = new ItemStack(Items.STICK);
	static {
		silkTouchItem.addEnchantment(Enchantments.SILK_TOUCH, 1);
	}

	/**
	 * Gets an item stack from a block state. Uses Silk Touch drops
	 * @param state  Input state
	 * @return  ItemStack for the state, or ItemStack.EMPTY if a valid item cannot be found
	 */
	public static ItemStack getStackFromState(ServerWorld world, @Nullable BlockState state) {
		if (state == null) {
			return ItemStack.EMPTY;
		}
		Block block = state.getBlock();

		// skip air
		if(block == Blocks.AIR) {
			return ItemStack.EMPTY;
		}

		// Fill a fake context in to get Silk Touch drops.
		// From LootParameterSets.Block,
		// BLOCK_STATE, POSITION and TOOL is required and
		// THIS_ENTITY, BLOCK_ENTITY and EXPLOSION_RADIUS are optional.
		// BLOCK_STATE is provided by getDrops().
		List<ItemStack> drops = state.getDrops(new LootContext.Builder(world)
				.withParameter(LootParameters.POSITION, new BlockPos(0, 0, 64))
				.withParameter(LootParameters.TOOL, silkTouchItem)
		);
		if( drops.size() > 0 ) {
			return drops.get(0);
		}

		// if it fails, do a fallback of item.getItemFromBlock
		InspirationsRegistry.log.error("Failed to get silk touch drop for {}, using fallback", state);

		// fallback, use item dropped
		Item item = Item.getItemFromBlock(block);
		if(item == Items.AIR) {
			return ItemStack.EMPTY;
		}
		return new ItemStack(item);
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
	public static void addPotionTooltip(Potion potionType, List<String> lores) {
		List<EffectInstance> effects = potionType.getEffects();

		if (effects.isEmpty()) {
			String s = translate("effect.none").trim();
			lores.add(TextFormatting.GRAY + s);
			return;
		}

		for (EffectInstance effect : effects) {
			String effectString = translate(effect.getEffectName()).trim();
			Effect potion = effect.getPotion();

			if (effect.getAmplifier() > 0) {
				effectString += " " + translate("potion.potency." + effect.getAmplifier()).trim();
			}
			if (effect.getDuration() > 20) {
				effectString += " (" + EffectUtils.getPotionDurationString(effect, 1.0f) + ")";
			}
			lores.add((potion.isBeneficial() ? TextFormatting.BLUE : TextFormatting.RED) + effectString);
		}
	}

	/**
	 * Gets the dye color for the given color int
	 * @param color  Dye color input
	 * @return  EnumDyeColor matching, or null for no match
	 */
	public static DyeColor getDyeForColor(int color) {
		for(DyeColor dyeColor : DyeColor.values()) {
			if(dyeColor.getId() == color) {
				return dyeColor;
			}
		}
		return null;
	}
}
