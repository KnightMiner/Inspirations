package knightminer.inspirations.common.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.Util;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BooleanSupplier;

// Reuse code for both a recipe and loot table condition.
public class ConfigEnabledCondition implements ICondition, ILootCondition {
	public static final ResourceLocation ID = Util.getResource("config");

	private final String configName;
	private final BooleanSupplier supplier;

	private static Map<String,ConfigEnabledCondition> PROPS = new HashMap<>();

	private ConfigEnabledCondition(String configName, BooleanSupplier supplier) {
		this.configName = configName;
		this.supplier = supplier;
	}

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	public boolean test() {
		return supplier.getAsBoolean();
	}

	@Override
	public boolean test(LootContext lootContext) {
		return supplier.getAsBoolean();
	}

	public static class Serializer extends AbstractSerializer<ConfigEnabledCondition> implements IConditionSerializer<ConfigEnabledCondition>  {
		public Serializer() {
			super(ID, ConfigEnabledCondition.class);
		}

		@Override
		public void write(JsonObject json, ConfigEnabledCondition value) {
			json.addProperty("prop", value.configName);
		}

		@Override
		public ConfigEnabledCondition read(JsonObject json) {
			String prop = JSONUtils.getString(json, "prop");
			ConfigEnabledCondition config = PROPS.get(prop.toLowerCase(Locale.ROOT));
			if (config == null) {
				throw new JsonSyntaxException("Invalid propertyname '" + prop + "'");
			}
			return config;
		}

		@Override
		public ResourceLocation getID() {
			return ID;
		}

		@Override
		public void serialize(@Nonnull JsonObject json, @Nonnull ConfigEnabledCondition cond, @Nonnull JsonSerializationContext ctx) {
			write(json, cond);
		}

		@Nonnull
		@Override
		public ConfigEnabledCondition deserialize(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext ctx) {
			return read(json);
		}
	}

	// Add all the properties.
	private static ConfigEnabledCondition add(String prop, BooleanSupplier supplier) {
		ConfigEnabledCondition conf = new ConfigEnabledCondition(prop, supplier);
		PROPS.put(prop.toLowerCase(Locale.ROOT), conf);
		return conf;
	}
	private static ConfigEnabledCondition add(String prop, ForgeConfigSpec.BooleanValue option) {
		return add(prop, option::get);
	}

	// building
	public static final ConfigEnabledCondition BOOKSHELF = add("bookshelf", Config.enableBookshelf);
	public static final ConfigEnabledCondition COLORED_BOOKS = add("colored_books", Config::enableColoredBooks);
	public static final ConfigEnabledCondition ENLIGHTENED_BUSH = add("enlightened_bush", Config.enableEnlightenedBush);
	public static final ConfigEnabledCondition FLOWERS = add("flowers", Config.enableFlowers);
	public static final ConfigEnabledCondition GLASS_DOOR = add("glass_door", Config.enableGlassDoor);
	public static final ConfigEnabledCondition MULCH = add("mulch", Config.enableMulch);
	public static final ConfigEnabledCondition PATH = add("path", Config.enablePath);
	public static final ConfigEnabledCondition ROPE = add("rope", Config.enableRope);

	// utility
	public static final ConfigEnabledCondition BRICKS_BUTTON = add("bricks_button", ()->false/*TODO: Config.enableBricksButton*/);
	public static final ConfigEnabledCondition CARPETED_TRAPDOOR = add("carpeted_trapdoor", Config.enableCarpetedTrapdoor);
	public static final ConfigEnabledCondition COLLECTOR = add("collector", Config.enableCollector);
	public static final ConfigEnabledCondition PIPE = add("pipe", Config.enablePipe);
	public static final ConfigEnabledCondition REDSTONE_BOOK = add("redstone_book", Config::enableRedstoneBook);
	public static final ConfigEnabledCondition TORCH_LEVER = add("torch_lever", Config.enableTorchLever);

	// tools
	public static final ConfigEnabledCondition BAROMETER = add("barometer", Config.enableBarometer);
	public static final ConfigEnabledCondition CHARGED_ARROW = add("charged_arrow", Config.enableChargedArrow);
	public static final ConfigEnabledCondition CRAFT_WAYPOINT = add("craft_waypoint_compass", Config::craftWaypointCompass);
	public static final ConfigEnabledCondition COPY_WAYPOINT = add("copy_waypoint_compass", Config::copyWaypointCompass);
	public static final ConfigEnabledCondition DYE_WAYPOINT = add("dye_waypoint_compass", Config::dyeWaypointCompass);
	public static final ConfigEnabledCondition LOCK = add("lock", Config.enableLock);
	public static final ConfigEnabledCondition NORTH_COMPASS = add("north_compass", Config.enableNorthCompass);
	public static final ConfigEnabledCondition PHOTOMETER = add("photometer", Config.enablePhotometer);
	public static final ConfigEnabledCondition REDSTONE_CHARGER = add("redstone_charger", Config.enableRedstoneCharger);

	// tweaks
	public static final ConfigEnabledCondition MORE_SEEDS = add("more_seeds", ()->false/* TODO: Config.enableMoreSeeds */);
	public static final ConfigEnabledCondition UNSTACKABLE_ALTS = add("unstackable_alts", Config.unstackableRecipeAlts);
	public static final ConfigEnabledCondition SKELETON_SKULL = add("skeleton_skull", Config.skeletonSkull);
	public static final ConfigEnabledCondition CAVE_SPIDER_WEB = add("cave_spider_web", Config.caveSpiderDrops);

	// recipes
	public static final ConfigEnabledCondition CAULDRON_DYEING = add("cauldron_dyeing", ()->false/* TODO: Config::enableCauldronDyeing */);
	public static final ConfigEnabledCondition CAULDRON_FLUIDS = add("cauldron_fluids", ()->false/* TODO: Config::enableCauldronFluids */);
	public static final ConfigEnabledCondition CAULDRON_POTIONS = add("cauldron_potions", ()->false/* TODO: Config::enableCauldronPotions */);
	public static final ConfigEnabledCondition EXTRA_DYED_BOTTLE_RECIPES = add("extra_dyed_bottle_recipes", ()->false/* TODO: Config::extraBottleRecipes */);
	public static final ConfigEnabledCondition PATCH_VANILLA_DYE_RECIPES = add("patch_vanilla_dye_recipes", ()->false/* TODO: Config::patchVanillaDyeRecipes */);

}
