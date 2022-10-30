package knightminer.inspirations.common.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.shared.InspirationsShared;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BooleanSupplier;

// Reuse code for both a recipe and loot table condition.
public class ConfigEnabledCondition implements ICondition, LootItemCondition {
  private static final ResourceLocation ID = Inspirations.getResource("config");
  /* Map of config names to condition cache */
  private static final Map<String,ConfigEnabledCondition> PROPS = new HashMap<>();

  private final String configName;
  private final BooleanSupplier supplier;

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

  @Override
  public LootItemConditionType getType() {
    return InspirationsShared.lootConfig;
  }

  public static class ConditionSerializer implements Serializer<ConfigEnabledCondition>, IConditionSerializer<ConfigEnabledCondition> {
    @Override
    public ResourceLocation getID() {
      return ID;
    }

    @Override
    public void write(JsonObject json, ConfigEnabledCondition value) {
      json.addProperty("prop", value.configName);
    }

    @Override
    public ConfigEnabledCondition read(JsonObject json) {
      String prop = GsonHelper.getAsString(json, "prop");
      ConfigEnabledCondition config = PROPS.get(prop.toLowerCase(Locale.ROOT));
      if (config == null) {
        throw new JsonSyntaxException("Invalid property name '" + prop + "'");
      }
      return config;
    }

    @Override
    public void serialize(JsonObject json, ConfigEnabledCondition condition, JsonSerializationContext context) {
      write(json, condition);
    }

    @Override
    public ConfigEnabledCondition deserialize(JsonObject json, JsonDeserializationContext context) {
      return read(json);
    }
  }

  /**
   * Adds a condition
   * @param prop     Property name
   * @param supplier Boolean supplier
   * @return Added condition
   */
  private static ConfigEnabledCondition add(String prop, BooleanSupplier supplier) {
    ConfigEnabledCondition conf = new ConfigEnabledCondition(prop, supplier);
    PROPS.put(prop.toLowerCase(Locale.ROOT), conf);
    return conf;
  }

  /**
   * Adds a condition
   * @param prop     Property name
   * @param supplier Boolean supplier
   * @return Added condition
   */
  private static ConfigEnabledCondition add(String prop, BooleanValue supplier) {
    return add(prop, supplier::get);
  }

  /* Config conditions available */

  // modules
  public static final ConfigEnabledCondition MODULE_BUILDING = add("building_module", Config.buildingModule);
  public static final ConfigEnabledCondition MODULE_UTILITY = add("utility_module", Config.utilityModule);
  public static final ConfigEnabledCondition MODULE_TOOLS = add("tools_module", Config.toolsModule);
  public static final ConfigEnabledCondition MODULE_TWEAKS = add("tweaks_module", Config.tweaksModule);
  public static final ConfigEnabledCondition MODULE_RECIPES = add("recipes_module", Config.recipesModule);

  // building
  public static final ConfigEnabledCondition BOOKSHELF = add("bookshelf", Config.enableBookshelf);
  public static final ConfigEnabledCondition COLORED_BOOKS = add("colored_books", Config.enableColoredBooks);
  public static final ConfigEnabledCondition ENLIGHTENED_BUSH = add("enlightened_bush", Config.enableEnlightenedBush);
  public static final ConfigEnabledCondition FLOWERS = add("flowers", Config.enableFlowers);
  public static final ConfigEnabledCondition GLASS_DOOR = add("glass_door", Config.enableGlassDoor);
  public static final ConfigEnabledCondition MULCH = add("mulch", Config.enableMulch);
  public static final ConfigEnabledCondition PATH = add("path", Config.enablePath);
  public static final ConfigEnabledCondition ROPE = add("rope", Config.enableRope);

  // utility
  public static final ConfigEnabledCondition BRICKS_BUTTON = add("bricks_button", () -> false/*TODO: Config.enableBricksButton*/);
  public static final ConfigEnabledCondition CARPETED_TRAPDOOR = add("carpeted_trapdoor", Config.enableCarpetedTrapdoor);
  public static final ConfigEnabledCondition COLLECTOR = add("collector", Config.enableCollector);
  public static final ConfigEnabledCondition PIPE = add("pipe", Config.enablePipe);
  public static final ConfigEnabledCondition REDSTONE_BOOK = add("redstone_book", Config.enableRedstoneBook);
  public static final ConfigEnabledCondition TORCH_LEVER = add("torch_lever", Config.enableTorchLever);

  // tools
  public static final ConfigEnabledCondition BAROMETER = add("barometer", Config.enableBarometer);
  public static final ConfigEnabledCondition CHARGED_ARROW = add("charged_arrow", Config.enableChargedArrow);
  public static final ConfigEnabledCondition DIMENSION_COMPASS = add("dimension_compass", Config.enableDimensionCompass);
  public static final ConfigEnabledCondition LOCK = add("lock", Config.enableLock);
  public static final ConfigEnabledCondition NORTH_COMPASS = add("north_compass", Config.enableNorthCompass);
  public static final ConfigEnabledCondition PHOTOMETER = add("photometer", Config.enablePhotometer);
  public static final ConfigEnabledCondition REDSTONE_CHARGER = add("redstone_charger", Config.enableRedstoneCharger);

  // tweaks
  public static final ConfigEnabledCondition CROP_BLOCKS = add("block_crops", Config.enableBlockCrops);
  public static final ConfigEnabledCondition UNSTACKABLE_ALTS = add("unstackable_alts", Config.unstackableRecipeAlts);
  public static final ConfigEnabledCondition SKELETON_SKULL = add("skeleton_skull", Config.skeletonSkull);
  public static final ConfigEnabledCondition CAVE_SPIDER_WEB = add("cave_spider_web", Config.caveSpiderDrops);
  public static final ConfigEnabledCondition HEARTBEETS = add("heartbeets", Config.enableHeartbeet);

  // recipes
  public static final ConfigEnabledCondition CAULDRON_RECIPES = add("cauldron_recipes", Config.cauldronRecipes);
  public static final ConfigEnabledCondition CAULDRON_ICE = add("cauldron_ice", Config.cauldronIce);
  // dyes
  public static final ConfigEnabledCondition CAULDRON_DYEING = add("cauldron_dyeing", Config.enableCauldronDyeing);
  public static final ConfigEnabledCondition EXTRA_BOTTLE_RECIPES = add("extra_bottle_recipes", Config.extraBottleRecipes);
  // potions
  public static final ConfigEnabledCondition CAULDRON_POTIONS = add("cauldron_potions", Config.enableCauldronPotions);
  public static final ConfigEnabledCondition CAULDRON_BREWING = add("cauldron_brewing", Config.enableCauldronBrewing);
  public static final ConfigEnabledCondition CAULDRON_TIP_ARROWS = add("cauldron_tip_arrows", Config.cauldronTipArrows);

}
