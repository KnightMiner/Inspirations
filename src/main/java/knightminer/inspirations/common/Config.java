package knightminer.inspirations.common;

import knightminer.inspirations.Inspirations;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.function.BooleanSupplier;

@SuppressWarnings("WeakerAccess")
public class Config {
  /** Config for anything that affects gameplay */
  public static final ForgeConfigSpec SERVER_SPEC;
  /** Config for anything that is visual only */
  public static final ForgeConfigSpec CLIENT_SPEC;
  /** Config for things affecting gameplay that must load before registration */
  public static final ForgeConfigSpec COMMON_SPEC;

  // modules
  public static final BooleanValue buildingModule;
  public static final BooleanValue utilityModule;
  public static final BooleanValue toolsModule;
  public static final BooleanValue tweaksModule;
  public static final BooleanValue cauldronsModule;

  // general
  public static final BooleanValue showAllVariants;

  // building
  public static final BooleanSupplier enableRope;
  public static final BooleanSupplier enableRopeLadder;

  public static final BooleanSupplier enableGlassDoor;
  public static final BooleanSupplier enableMulch;
  public static final BooleanSupplier enablePath;
  public static final BooleanSupplier enableFlowers;
  public static final BooleanSupplier enableEnlightenedBush;
  // OVERRIDE
  public static final BooleanValue climbableIronBars;


  public static final BooleanSupplier enableBookshelf;
  public static final BooleanSupplier enableColoredBooks;
  public static final BooleanSupplier bookshelvesBoostEnchanting;
  public static final ConfigValue<Double> defaultEnchantingPower;

  // utility
  public static final BooleanSupplier enableTorchLever;
  public static final BooleanSupplier enableRedstoneBook;
  public static final BooleanSupplier enableCarpetedTrapdoor;
  public static final BooleanSupplier enableCarpetedPressurePlate;
  public static final BooleanSupplier enableCollector;
  public static final BooleanSupplier enablePipe;
  public static final BooleanValue pipeUpwards;

  // recipes
  public static final BooleanSupplier enableMilkBottles;
  // vanilla
  public static final BooleanSupplier cauldronConcrete;
  public static final BooleanSupplier cauldronCleanStickyPiston;
  public static final BooleanSupplier cauldronWetSponge;
  public static final BooleanSupplier cauldronWashWool;
  public static final BooleanSupplier replaceVanillaCauldrons;
  // contents
  public static final BooleanSupplier enableCauldronMilk;
  public static final BooleanSupplier enableCauldronHoney;
  public static final BooleanSupplier enableCauldronSoups;
  // dyes
  public static final BooleanSupplier enableCauldronDyeing;
  public static final BooleanSupplier extraBottleRecipes;
  // potions
  public static final BooleanSupplier enableCauldronPotions;
  public static final BooleanSupplier brewPotionBottles;
  public static final BooleanSupplier cauldronBrewing;
  public static final BooleanSupplier cauldronTipArrows;

  // tools
  public static final BooleanSupplier enableLock;
  public static final BooleanSupplier enableRedstoneCharger;
  public static final BooleanSupplier enableChargedArrow;
  public static final BooleanSupplier harvestHangingVines;
  public static final BooleanSupplier shearsReclaimMelons;
  public static final BooleanSupplier enableNorthCompass;
  public static final BooleanSupplier enableBarometer;
  public static final BooleanSupplier enablePhotometer;

  // waypoint compass
  public static final BooleanSupplier enableDimensionCompass;

  // enchantments
  public static final BooleanSupplier moreShieldEnchantments;
  public static final BooleanSupplier shieldEnchantmentTable;

  public static final BooleanSupplier fixShieldTooltip;
  public static final BooleanSupplier axeWeaponEnchants;
  public static final BooleanSupplier axeEnchantmentTable;

  // tweaks
  public static final BooleanSupplier enablePigDesaddle;
  public static final BooleanSupplier enableFittedCarpets;
  public static final BooleanSupplier lilypadBreakFall;
  public static final BooleanSupplier unstackableRecipeAlts;
  public static final BooleanSupplier dispensersPlaceAnvils;
  public static final BooleanValue milkCooldown;
  public static final ConfigValue<Integer> milkCooldownTime;
  public static final BooleanSupplier waterlogHopper;

  // client
  public static final BooleanValue betterCauldronItem;
  public static final BooleanValue coloredEnchantedRibbons;
  public static final BooleanValue coloredFireworkItems;
  public static final BooleanValue customPortalColor;

  // heartbeet
  public static final BooleanSupplier enableHeartbeet;
  public static final BooleanSupplier brewHeartbeet;

  // seeds
  public static final BooleanSupplier smoothBlockCropGrowth;
  public static final BooleanValue bonemealBlockCrop;
  public static final BooleanValue nerfCactusFarms;
  // bonemeal
  public static final BooleanSupplier bonemealMushrooms;
  public static final BooleanSupplier bonemealDeadBush;
  public static final BooleanSupplier bonemealGrassSpread;
  public static final BooleanSupplier bonemealMyceliumSpread;

  public static final BooleanSupplier caveSpiderDrops;
  public static final BooleanSupplier skeletonSkull;

  static {
    Builder server = new Builder();
    Builder client = new Builder();
    Builder common = new Builder();

    // modules root config, needs to be common as it affects recipes
    common.push("modules");
    {
      buildingModule = common.comment("Building blocks to improve decoration").worldRestart().define("building", true);
      utilityModule = common.comment("Adds tools for automation and redstone interaction").worldRestart().define("utility", true);
      tweaksModule = common.comment("Contains tweaks to vanilla features").worldRestart().define("tweaks", true);
      toolsModule = common.comment("Includes new tools to reduce dependency on debug features").worldRestart().define("tools", true);
      cauldronsModule = common.comment("Expands interactions available to the cauldron and adds new variants").worldRestart().define("cauldrons", true);
    }
    common.pop();

    /*
     * General stuff
     */
    showAllVariants = server.comment("Shows all variants for dynamically textured blocks, like bookshelves. If false just the first will be shown").define("general.showAllVariants", true);

    /*
     * Building module
     */

    common.push("building");
    server.push("building");
    {
      // bookshelves
      enableBookshelf = and(buildingModule, common.comment("Enables the shelf: a decorative block to display books and other items").worldRestart().define("bookshelf.enable", true));
      enableColoredBooks = and(enableBookshelf, common.comment("Enables colored books: basically colored versions of the vanilla book to decorate shelves").worldRestart().define("bookshelf", true));
      bookshelvesBoostEnchanting = and(buildingModule, server.comment("If true, shelves will increase enchanting table power.").define("bookshelf.boostEnchanting", true));
      defaultEnchantingPower = server.comment("Default power for a book for enchanting, can be overridden in the book overrides.").defineInRange("bookshelf.defaultEnchanting", 1.5f, 0.0f, 15.0f);

      // rope
      enableRope = and(buildingModule, common.comment("Enables rope: can be climbed like ladders and extended with additional rope").worldRestart().define("rope", true));
      enableRopeLadder = and(enableRope, server.comment("Enables rope ladders: right click ropes with sticks to extend the hitbox").define("ropeLadder", true));
      climbableIronBars = common.comment("Makes iron bars climbable if a rope is below them, requires block override on minecraft:iron_bars.").worldRestart().define("climbableBars", true);

      // glass door
      enableGlassDoor = and(buildingModule, common.comment("Enables glass doors and trapdoors: basically doors, but made of glass. Not sure what you would expect.").worldRestart().define("glassDoor", true));
      // mulch
      enableMulch = and(buildingModule, common.comment("Enables mulch: a craftable falling block which supports plants such as flowers").worldRestart().define("mulch", true));
      // path
      enablePath = and(buildingModule, common.comment("Enables stone paths: a carpet like decorative block for making decorative paths").worldRestart().define("path", true));

      // flowers
      enableFlowers = and(buildingModule, common.comment("Enables additional flowers from breaking double flowers with shears.").worldRestart().define("flowers", true));

      // enlightenedBush
      enableEnlightenedBush = and(buildingModule, common.comment("Enables enlightened bushes: bushes with lights.").worldRestart().define("enlightenedBush", true));
    }
    common.pop();
    server.pop();

    /*
     * Utility module
     */
    server.push("utility");
    common.push("utility");
    {
      // building addon - redstone books
      enableRedstoneBook = and(enableBookshelf, utilityModule, common.comment("Enables the trapped book: will emit redstone power when placed in a bookshelf. Requires bookshelf.").worldRestart().define("redstoneBook", true));

      // torch lever
      enableTorchLever = and(utilityModule, common.comment("Enables the torch lever: basically a lever which looks like a torch").worldRestart().define("torchLever", true));

      // bricks button
			/*
			enableBricksButton = builder
					.comment("Enables button blocks disguised as a full bricks or nether bricks block")
					.worldRestart()
					.define("bricksButton", true);
			 */

      // carpeted trapdoor
      enableCarpetedTrapdoor = and(utilityModule, common.comment("Enables carpeted trapdoors: a trapdoor which appears to be a carpet when closed").worldRestart().define("carpetedTrapdoor", true));
      // carpeted pressure plate
      enableCarpetedPressurePlate = and(utilityModule, server.comment("Allows placing a carpet on a stone pressure plate to hide it").define("carpetedPressurePlate", true));

      // collector
      enableCollector = and(utilityModule, common.comment("Enables the collector: extracts items from inventories or the world similar to a hopper, but can face in all 6 directions and cannot place items in inventories").worldRestart().define("collector", true));
      // pipe
      enablePipe = and(utilityModule, common.comment("Enables pipes: a more economical hopper that only outputs items, does not pull from inventories. Both cheaper and better for performance.").worldRestart().define("pipe", true));
      pipeUpwards = server.comment("Allows pipes to output upwards. This removes a limitation on not being able to pipe items up without dropper elevators, but should be balanced alongside modded pipes.").define("pipeUpwards", true);

      // dispenser fluid containers
			/*
			enableDispenserFluidTanks = builder
					.comment("Allows dispensers to fill and empty fluid tanks using fluid containers")
					.worldRestart()
					.define("dispenserFluidTanks", true);
			*/
    }
    server.pop();
    common.pop();

    // cauldron
    common.push("cauldron");
		{
      enableMilkBottles = and(cauldronsModule, common.comment("Allows filling glass bottles with milk from a cow. Note moving milk from bucket to bottles requires milk cauldrons").define("milkBottles", true));
      replaceVanillaCauldrons = and(cauldronsModule, common.comment("If true, improves vanilla water cauldrons by including boiling particles, requires a block substitution on water cauldrons. More functionality coming to this option later.").worldRestart().define("replaceWaterCauldron", true));

      // vanilla recipes
      common.push("vanilla");
      {
        cauldronConcrete          = and(cauldronsModule, common.comment("Allows concrete to be made a cauldron filled with water").worldRestart().define("concrete", true));
        cauldronCleanStickyPiston = and(cauldronsModule, common.comment("Lets you clean sticky pistons in the cauldron").worldRestart().define("cleanStickyPiston", true));
        cauldronWetSponge         = and(cauldronsModule, common.comment("Lets you wet sponges in the cauldron").worldRestart().define("wetSponge", true));
        cauldronWashWool          = and(cauldronsModule, common.comment("Lets you wash various wool created items in a water filled cauldron to turn it white").worldRestart().define("washWool", true));
      }
      common.pop();

      // potions
      common.push("contents");
      {
        enableCauldronMilk  = and(cauldronsModule, common.comment("Allows the cauldron to be filled with milk").define("milk", true));
        enableCauldronHoney = and(cauldronsModule, common.comment("Allows the cauldron to be filled with honey").define("honey", true));
        enableCauldronSoups = and(cauldronsModule, common.comment("Allows the cauldron to be filled with soups, including mushroom, potato, rabbit, suspicious, and beetroot").define("soup", true));
      }
      common.pop();

      // potions
      common.push("dyes");
      {
        enableCauldronDyeing = and(cauldronsModule, common.comment("Allows cauldrons to be filled with dyes and dye items using cauldrons").define("enable", true));
        extraBottleRecipes   = and(enableCauldronDyeing, common.comment("Adds extra dyed bottle recipes to craft green and brown").define("extraBottleRecipes", true));
      }
      common.pop();

      // potions
      common.push("potions");
      {
        enableCauldronPotions = and(cauldronsModule,  common.comment("Allows cauldrons to be filled with potions").define("enable", true));
        brewPotionBottles     = and(enableCauldronPotions, common.comment("Allows brewing glass bottles into splash and lingering bottles in a brewing stand").define("brewBottles", true));
        cauldronTipArrows     = and(enableCauldronPotions, common.comment("Allows cauldrons to tip arrows with potions").define("tipArrows", true));
        cauldronBrewing       = and(enableCauldronPotions, common.comment("Allows cauldrons to perform brewing recipes").define("brewing", true));
      }
      common.pop();
		}
    common.pop();

    /*
     * Tools module
     */
    server.push("tools");
    common.push("tools");
    {
      // redstone charge
      enableRedstoneCharger = and(toolsModule, common.comment("Enables the redstone charger: a quick pulse created with a flint and steel like item").worldRestart().define("redstoneCharger", true));
      enableChargedArrow    = and(toolsModule, common.comment("Enables the charged arrow: places a redstone pulse where it lands").worldRestart().define("chargedArrow", true));

      // lock
      enableLock = and(toolsModule, common.comment("Enables locks and keys: an item allowing you to lock a tile entity to only open for a special named item").worldRestart().define("lock", true));

      // harvest tweaks
      harvestHangingVines = and(toolsModule, server.comment("When shearing vines, any supported vines will also be sheared instead of just broken").define("harvestHangingVines", true));
      shearsReclaimMelons = and(toolsModule, server.comment("Breaking a melon block with shears will always return 9 slices").define("reclaimMelons", true));

      // tools
      enableNorthCompass = and(toolsModule, common.comment("Enables the north compass: a cheaper compass that always points north. Intended to either allow packs to replace the compass or as an alternative for F3 navigation").worldRestart().define("northCompass", true));
      enableBarometer    = and(toolsModule, common.comment("Enables the barometer: a tool to measure the player's height in world.").worldRestart().define("barometer", true));
      enablePhotometer   = and(toolsModule, common.comment("Enables the photometer: a tool to measure light in world. Can be pointed at a block to measure the light level of that block.").worldRestart().define("photometer", true));
      enableDimensionCompass = and(toolsModule, server.comment("Enables the dimension compass: a compass which points towards the place you entered a dimension.", "When used on a lodestone, works across dimensions taking nether coordinates into account").worldRestart().define("dimensionCompass", true));

      // enchantments
      fixShieldTooltip = and(toolsModule, server.comment("If true, fixes the tooltip on shield items so it looks better with both patterns and enchantments").worldRestart().define("fixShieldTooltip", true));

      common.push("enchantments");
      {
        moreShieldEnchantments = and(toolsModule,            common.comment("If true, shields can now be enchanted with enchantments such as protection, fire aspect, knockback, and thorns. This requires replacing these enchantments.").worldRestart().define("moreShield", true));
        shieldEnchantmentTable = and(moreShieldEnchantments, common.comment("If true, shields can be enchanted in an enchantment table. Does not support modded shields as it requires a registry substitution").worldRestart().define("shieldTable", true));
        axeWeaponEnchants      = and(toolsModule,            common.comment("If true, axes will be able to be enchanted with weapon enchants such as looting, fire aspect, and knockback").worldRestart().define("axeWeapon", true));
        axeEnchantmentTable    = and(toolsModule,            common.comment("If true, axes can receive available weapon enchantments at the enchantment table").worldRestart().define("axeTable", true));
      }
      common.pop();
    }
    common.pop();
    server.pop();

    /*
     * Tweaks module
     */
    server.push("tweaks");
    common.push("tweaks");
    {
      // pig desaddle
      enablePigDesaddle = and(tweaksModule, server.comment("Allows pigs to be desaddled by shift-right click with an empty hand").define("desaddlePig", true));
      // fitted carpets
      enableFittedCarpets = and(tweaksModule, common.comment("Replace carpet blocks, allowing them to fit to stairs below them. Requires a registry sub on all vanilla carpet blocks.").worldRestart().define("fittedCarpets", true));
      // waterloggable hoppers
      waterlogHopper = and(tweaksModule, common.comment("Replace hopper blocks, allowing them to be waterlogged. Requires a registry sub on hoppers").worldRestart().define("waterlogHoppers", true));

      // bonemeal
      server.push("bonemeal");
      {
        bonemealMushrooms      = and(tweaksModule, server.comment("Bonemeal can be used on mycelium to produce mushrooms").define("mushrooms", true));
        bonemealDeadBush       = and(tweaksModule, server.comment("Bonemeal can be used on sand to produce dead bushes").define("deadBush", true));
        bonemealGrassSpread    = and(tweaksModule, server.comment("Bonemeal can be used on dirt to produce grass if adjecent to grass").define("grassSpread", true));
        bonemealMyceliumSpread = and(tweaksModule, server.comment("Bonemeal can be used on dirt to produce mycelium if adjecent to mycelium").define("myceliumSpread", true));
      }
      server.pop();

      // heartroot
      server.push("heartbeet");
      {
        enableHeartbeet = and(tweaksModule, server.comment("Enables heartbeets: a rare drop from beetroots which can be eaten to restore a bit of health").worldRestart().define("enable", true));
        brewHeartbeet = and(enableHeartbeet, server.comment("Allows heartbeets to be used as an alternative to ghast tears in making potions of regeneration").worldRestart().define("brewRegeneration", true));
      }
      server.pop();

      // dispensers place anvils
      dispensersPlaceAnvils = and(tweaksModule, server.comment("Dispensers will place anvils instead of dropping them. Plays well with anvil smashing.").worldRestart().define("dispensersPlaceAnvils", true));
      // lilypad fall breaking
      lilypadBreakFall = and(tweaksModule, server.comment("Lily pads prevent fall damage, but break in the process").define("lilypadBreakFall", true));
      // stackable alternative recipes
      unstackableRecipeAlts = and(tweaksModule, common.comment("Adds stackable recipes to some vanilla or Inspriations items that require unstackable items to craft").worldRestart().define("unstackableRecipeAlts", true));

      // drops - loot tables run the condition after config loads
      caveSpiderDrops = and(tweaksModule, server.comment("If true, cave spiders will rarely drop webs, giving them an advantage to farm over regular spiders").define("caveSpiderWeb", true));
      skeletonSkull = and(tweaksModule, server.comment("If true, skeletons will rarely drop their skull for consistency with wither skeletons. Does not affect creeper or zombie heads.").define("skeletonSkull", true));

      // milk cooldown
      server.push("milkCooldown");
      {
        milkCooldown     = server.comment("Adds a cooldown to milking cows, prevents practically infinite milk in modded worlds where milk is more useful.").define("enable", false);
        milkCooldownTime = server.comment("Delay in seconds after milking a cow before it can be milked again.").defineInRange("time", 600, 1, Short.MAX_VALUE);
      }
      server.pop();

      // seeds
      server.push("smoothGrowth");
      {
        smoothBlockCropGrowth = and(tweaksModule, server.comment("If true, cactus and sugar cane will grow in 2 pixel increments using the block crops").define("enable", true));
        bonemealBlockCrop = server.comment("If true, allows bonemeal to be used to speed block crop growth").define("bonemeal", false);
        nerfCactusFarms = server
            .comment("If false, cactus seeds planted on cactus have fewer restrictions.",
                     "Setting to true means cactus seeds are broken by neighboring blocks, meaning classic cactus farms will drop cactus seeds instead of full cactus.")
            .define("nerfCactusFarms", false);
      }
      server.pop();
    }
    server.pop();
    common.pop();

    /*
     * client properties
     */
    client.push("general");
    {
      // better cauldron item
      betterCauldronItem = client.comment("Replaces the flat cauldron sprite with the 3D cauldron block model").define("betterCauldronItemModel", true);
      // colored fireworks
      coloredFireworkItems = client.comment("Colors the fireworks item based on the colors of the stars").worldRestart().define("coloredFireworkItems", true);
      // colored enchanted book ribbons
      coloredEnchantedRibbons = client.comment("The ribbon on enchanted books colors based on the enchantment rarity").worldRestart().define("coloredEnchantedRibbons", true);
      // portal color
      customPortalColor = client.comment("Allows the portal color to be changed by placing colored blocks under the portal. Any block that tints a beacon beam will work for the color.").worldRestart().define("customPortalColor", true);
    }
    client.pop();

    // build all specs
    SERVER_SPEC = server.build();
    CLIENT_SPEC = client.build();
    COMMON_SPEC = common.build();
  }


  /* Utility functions */

  /** Boolean to keep track of whether the config has loaded yet */
  private static boolean loaded = false;

  /**
   * To avoid classloading, the function to call to update JEI for config changes.
   * If non-null, this will be {@link knightminer.inspirations.plugins.jei.JEIPlugin updateHiddenItems()}.
   */
  private static Runnable updateJEI = null;

  /**
   * Sets the runnable used to update JEI
   * @param runnable  JEI update runnable
   */
  public static void setJEIUpdateRunnable(Runnable runnable) {
    updateJEI = runnable;
  }

  /**
   * Checks if the server config is loaded
   * @return  True if the config loaded
   */
  public static boolean isLoaded() {
    return loaded;
  }

  /**
   * Function called when the config changes to update internal properties
   * @param configEvent  Event
   */
  public static void configChanged(final ModConfigEvent configEvent) {
    ModConfig config = configEvent.getConfig();
    if (config.getModId().equals(Inspirations.modID)) {
      IConfigSpec<?> spec = config.getSpec();
      if (spec == Config.SERVER_SPEC) {
        loaded = true;
        // If we have JEI, this will be set. It needs to run on the main thread...
        if (updateJEI != null) {
          DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().submitAsync(updateJEI));
        }
      }
    }
  }


  /* Helpers */

  /**
   * Creates a cached config value by anding two config values
   * @param first   First config value, typically a module
   * @param second  Property config value
   * @return  Cached config value
   */
  private static BooleanSupplier and(BooleanValue first, BooleanValue second) {
    return () -> first.get() && second.get();
  }

  /**
   * Creates a cached config value by anding two config values
   * @param first   First config value, typically a module
   * @param second  Property config value
   * @return  Cached config value
   */
  private static BooleanSupplier and(BooleanSupplier first, BooleanValue second) {
    return () -> first.getAsBoolean() && second.get();
  }

  /**
   * Creates a cached config value by anding two config values
   * @param first   First config value, typically a module
   * @param second  Property config value
   * @return  Cached config value
   */
  private static BooleanSupplier and(BooleanSupplier first, BooleanSupplier second) {
    return () -> first.getAsBoolean() && second.getAsBoolean();
  }

  /**
   * Creates a cached config value by anding three config values
   * @param first   First config value, typically a module
   * @param second  Second config value
   * @param third   Property config value
   * @return  Cached config value
   */
  private static BooleanSupplier and(BooleanValue first, BooleanValue second, BooleanValue third) {
    return () -> first.get() && second.get() && third.get();
  }

  /**
   * Creates a cached config value by anding three config values
   * @param first   First config value, typically a module
   * @param second  Second config value
   * @param third   Property config value
   * @return  Cached config value
   */
  private static BooleanSupplier and(BooleanSupplier first, BooleanValue second, BooleanValue third) {
    return () -> first.getAsBoolean() && second.get() && third.get();
  }
}
