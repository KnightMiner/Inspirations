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
	private static void add(String prop, BooleanSupplier supplier) {
		PROPS.put(prop.toLowerCase(Locale.ROOT), new ConfigEnabledCondition(prop, supplier));
	}
	private static void add(String prop, ForgeConfigSpec.BooleanValue option) {
		PROPS.put(prop.toLowerCase(Locale.ROOT), new ConfigEnabledCondition(prop, option::get));
	}
	static {
		// building
		add("bookshelf", Config.enableBookshelf);
		add("colored_books", Config::enableColoredBooks);
		add("enlightened_bush", Config.enableEnlightenedBush);
		add("flowers", Config.enableFlowers);
		add("glass_door", Config.enableGlassDoor);
		add("mulch", Config.enableMulch);
		add("path", Config.enablePath);
		add("rope", Config.enableRope);

		// utility
		add("bricks_button", Config.enableBricksButton);
		add("carpeted_trapdoor", Config.enableCarpetedTrapdoor);
		add("collector", Config.enableCollector);
		add("pipe", Config.enablePipe);
		add("redstone_barrel", Config.enableRedstoneBarrel);
		add("redstone_book", Config::enableRedstoneBook);
		add("redstone_torch_lever", Config.enableRedstoneTorchLever);
		add("torch_lever", Config.enableTorchLever);

		// tools
		add("barometer", Config.enableBarometer);
		add("charged_arrow", Config.enableChargedArrow);
		add("craft_waypoint_compass", Config::craftWaypointCompass);
		add("copy_waypoint_compass", Config::copyWaypointCompass);
		add("dye_waypoint_compass", Config::dyeWaypointCompass);
		add("lock", Config.enableLock);
		add("north_compass", Config.enableNorthCompass);
		add("photometer", Config.enablePhotometer);
		add("redstone_charge", Config.enableRedstoneCharge);

		// tweaks
		add("more_seeds", Config.enableMoreSeeds);
		add("unstackable_alts", Config.unstackableRecipeAlts);
		add("skeleton_skull", Config.skeletonSkull);
		add("cave_spider_web", Config.caveSpiderDrops);

		// recipes
		add("cauldron_dyeing", Config::enableCauldronDyeing);
		add("cauldron_fluids", Config::enableCauldronFluids);
		add("cauldron_potions", Config::enableCauldronPotions);
		add("extra_dyed_bottle_recipes", Config::extraBottleRecipes);
		add("patch_vanilla_dye_recipes", Config::patchVanillaDyeRecipes);
	}
}
