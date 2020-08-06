package knightminer.inspirations.common;

import knightminer.inspirations.common.config.CachedBoolean;
import knightminer.inspirations.common.config.CachedValue;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import static net.minecraftforge.common.ForgeConfigSpec.Builder;
import static net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

@SuppressWarnings("WeakerAccess")
public class Config {
  /** List of all cached config values, for cache clearing */
  private static final List<CachedValue<?>> CACHED_VALUES = new ArrayList<>();

  // TODO: add client config
  public static Builder BUILDER;
  public static ForgeConfigSpec SPEC;
  public static Builder BUILDER_OVERRIDE;
  public static ForgeConfigSpec SPEC_OVERRIDE;

  // modules
  public static CachedBoolean buildingModule;
  public static CachedBoolean utilityModule;
  public static CachedBoolean toolsModule;
  public static CachedBoolean tweaksModule;

  // general
  public static CachedBoolean showAllVariants;

  // building
  public static CachedBoolean enableRope;
  public static CachedBoolean enableRopeLadder;

  public static CachedBoolean enableGlassDoor;
  public static CachedBoolean enableMulch;
  public static CachedBoolean enablePath;
  public static CachedBoolean enableFlowers;
  public static CachedBoolean enableEnlightenedBush;
  // OVERRIDE
  public static CachedBoolean climbableIronBars;


  public static CachedBoolean enableBookshelf;
  public static CachedBoolean enableColoredBooks;
  public static CachedBoolean bookshelvesBoostEnchanting;
  public static CachedValue<Double> defaultEnchantingPower;

  public static CachedValue<String> bookKeywords;
  private static final String BOOK_KEYWORD_DEFAULTS = "almanac, atlas, book, catalogue, concordance, dictionary, directory, encyclopedia, guide, journal, lexicon, manual, thesaurus, tome";

  // utility
  public static CachedBoolean enableTorchLever;
  public static CachedBoolean enableRedstoneBook;
  public static CachedBoolean enableBricksButton;
  public static CachedBoolean enableCarpetedTrapdoor;
  public static CachedBoolean enableCarpetedPressurePlate;
  public static CachedBoolean enableCollector;
  public static CachedBoolean enablePipe;
  public static CachedBoolean pipeUpwards;
  //public static BooleanValue enableDispenserFluidTanks;
  //public static BooleanValue milkSquids;
  //public static IntValue milkSquidCooldown;

  // recipes

  // cauldron
  public static boolean enableCauldronRecipes() {
    return false;
  }
  public static boolean enableExtendedCauldron() {
    return false;
  }
  public static boolean enableBiggerCauldron() { return false; }
  public static int getCauldronMax()           { return enableBiggerCauldron() ? 4 : 3; }
  public static boolean fasterCauldronRain()   { return false; }
  public static boolean canSpongeEmptyFullOnly() {
    return false;
  }
  public static boolean enableCauldronDyeing() {
    return false;
  }
  public static boolean enableCauldronPotions() {
    return false;
  }
  public static boolean expensiveCauldronBrewing() {
    return false;
  }

  // anvil smashing
  public static CachedBoolean enableAnvilSmashing;

  // tools
  public static CachedBoolean enableLock;
  public static CachedBoolean enableRedstoneCharger;
  public static CachedBoolean enableChargedArrow;
  public static CachedBoolean harvestHangingVines;
  public static CachedBoolean shearsReclaimMelons;
  public static CachedBoolean enableNorthCompass;
  public static CachedBoolean enableBarometer;
  public static CachedBoolean enablePhotometer;

  // waypoint compass
  public static CachedBoolean enableWaypointCompass;
  public static CachedBoolean dyeWaypointCompass;
  public static CachedBoolean craftWaypointCompass;
  public static CachedBoolean copyWaypointCompass;
  public static CachedBoolean waypointCompassAdvTooltip;
  public static CachedBoolean waypointCompassCrossDimension;

  // enchantments
  public static CachedBoolean moreShieldEnchantments;
  public static CachedBoolean shieldEnchantmentTable;

  public static CachedBoolean fixShieldTooltip;
  public static CachedBoolean axeWeaponEnchants;
  public static CachedBoolean axeEnchantmentTable;

  // tweaks
  public static CachedBoolean enablePigDesaddle;
  public static CachedBoolean enableFittedCarpets;
  public static CachedBoolean coloredEnchantedRibbons;
  public static CachedBoolean coloredFireworkItems;
  public static CachedBoolean lilypadBreakFall;
  public static CachedBoolean betterCauldronItem;
  public static CachedBoolean unstackableRecipeAlts;
  public static CachedBoolean dispensersPlaceAnvils;
  public static CachedBoolean milkCooldown;
  public static CachedValue<Integer> milkCooldownTime;
  public static CachedBoolean customPortalColor;
  public static CachedBoolean waterlogHopper;

  // heartbeet
  public static CachedBoolean enableHeartbeet;
  private static CachedBoolean brewHeartbeet;

  public static boolean brewHeartbeet() {
    return brewHeartbeet.get() && enableHeartbeet.get();
  }

  public static CachedValue<Integer> heartbeetChance;

  // seeds
  public static CachedBoolean enableBlockCrops;
  public static CachedBoolean smoothBlockCropGrowth;
  public static CachedBoolean bonemealBlockCrop;

  public static CachedBoolean nerfCactusFarms;
  //  public static BooleanValue enableMoreSeeds;
  //	private static BooleanValue addGrassDrops;
  //	private static BooleanValue nerfCarrotPotatoDrops;
  //	public static boolean addGrassDrops() {
  //		return addGrassDrops.get() && enableMoreSeeds.get();
  //	}
  //	public static boolean nerfCarrotPotatoDrops() {
  //		return nerfCarrotPotatoDrops.get() && enableMoreSeeds.get();
  //	}
  // bonemeal
  public static CachedBoolean bonemealMushrooms;
  public static CachedBoolean bonemealDeadBush;
  public static CachedBoolean bonemealGrassSpread;
  public static CachedBoolean bonemealMyceliumSpread;

  public static CachedBoolean caveSpiderDrops;
  public static CachedBoolean skeletonSkull;

  static {
    BUILDER = new Builder();
    BUILDER_OVERRIDE = new Builder();
    configure(BUILDER, BUILDER_OVERRIDE);
    SPEC = BUILDER.build();
    SPEC_OVERRIDE = BUILDER_OVERRIDE.build();
  }

  private static void configure(Builder builder, Builder builder_override) {

    builder.push("modules");
    {
      buildingModule = cached(builder
          .comment("Building blocks to improve decoration")
          .worldRestart()
          .define("building", true));

      utilityModule = cached(builder
          .comment("Adds tools for automation and redstone interaction")
          .worldRestart()
          .define("utility", true));

      tweaksModule = cached(builder
          .comment("Contains tweaks to vanilla features")
          .worldRestart()
          .define("tweaks", true));

      toolsModule = cached(builder
          .comment("Includes new tools to reduce dependency on debug features")
          .worldRestart()
          .define("tools", true));
    }
    builder.pop();

    showAllVariants = cached(builder
        .comment("Shows all variants for dynamically textured blocks, like bookshelves. If false just the first will be shown")
        .define("general.showAllVariants", true));

    builder.push("building");
    {
      // bookshelves
      enableBookshelf = and(buildingModule, builder
          .comment("Enables the bookshelf: a decorative block to display books")
          .worldRestart()
          .define("bookshelf.enable", true));
      enableColoredBooks = and(enableBookshelf, builder
          .comment("Enables colored books: basically colored versions of the vanilla book to decorate bookshelves")
          .worldRestart()
          .define("bookshelf.coloredBooks", true));
      bookshelvesBoostEnchanting = and(buildingModule, builder
          .comment("If true, bookshelves will increase enchanting table power.")
          .define("bookshelf.boostEnchanting", true));
      defaultEnchantingPower = cached(builder
          .comment("Default power for a book for enchanting, can be overridden in the book overrides.")
          .defineInRange("bookshelf.defaultEnchanting", 1.5f, 0.0f, 15.0f));
      bookKeywords = cached(builder
          .comment("List of keywords for valid books, used to determine valid books in the bookshelf. Separate each by commas.")
          .define("bookshelf.bookKeywords", BOOK_KEYWORD_DEFAULTS));


      // rope
      enableRope = and(buildingModule, builder
          .comment("Enables rope: can be climbed like ladders and extended with additional rope")
          .worldRestart()
          .define("rope", true));
      enableRopeLadder = and(enableRope, builder
          .comment("Enables rope ladders: right click ropes with sticks to extend the hitbox")
          .define("ropeLadder", true));
      climbableIronBars = override(builder_override
          .comment("Makes iron bars climbable if a rope is below them.")
          .worldRestart()
          .define("rope.climbableBars", true));

      // glass door
      enableGlassDoor = and(buildingModule, builder
          .comment("Enables glass doors and trapdoors: basically doors, but made of glass. Not sure what you would expect.")
          .worldRestart()
          .define("glassDoor", true));

      // mulch
      enableMulch = and(buildingModule, builder
          .comment("Enables mulch: a craftable falling block which supports plants such as flowers")
          .worldRestart()
          .define("mulch", true));

      // path
      enablePath = and(buildingModule, builder
          .comment("Enables stone paths: a carpet like decorative block for making decorative paths")
          .worldRestart()
          .define("path", true));

      // flowers
      enableFlowers = and(buildingModule, builder
          .comment("Enables additional flowers from breaking double flowers with shears.")
          .worldRestart()
          .define("flowers", true));

      // enlightenedBush
      enableEnlightenedBush = and(buildingModule, builder
          .comment("Enables enlightened bushes: bushes with lights.")
          .worldRestart()
          .define("enlightenedBush", true));
    }
    builder.pop();

    builder.push("utility");
    {
      enableRedstoneBook = and(utilityModule, enableBookshelf, builder
          .comment("Enables the trapped book: will emit redstone power when placed in a bookshelf. Requires bookshelf.")
          .worldRestart()
          .define("redstoneBook", true));

      // torch lever
      enableTorchLever = and(utilityModule, builder
          .comment("Enables the torch lever: basically a lever which looks like a torch")
          .worldRestart()
          .define("torchLever", true));

      // bricks button
			/*
			enableBricksButton = builder
					.comment("Enables button blocks disguised as a full bricks or nether bricks block")
					.worldRestart()
					.define("bricksButton", true);
			 */

      // carpeted trapdoor
      enableCarpetedTrapdoor = and(utilityModule, builder
          .comment("Enables carpeted trapdoors: a trapdoor which appears to be a carpet when closed")
          .worldRestart()
          .define("carpetedTrapdoor", true));

      // carpeted pressure plate
      enableCarpetedPressurePlate = and(utilityModule, builder
          .comment("Allows placing a carpet on a stone pressure plate to hide it")
          .worldRestart()
          .define("carpetedPressurePlate", true));

      // collector
      enableCollector = and(utilityModule, builder
          .comment("Enables the collector: extracts items from inventories or the world similar to a hopper, but can face in all 6 directions and cannot place items in inventories")
          .worldRestart()
          .define("collector", true));

      // pipe
      enablePipe = and(utilityModule, builder
          .comment("Enables pipes: a more economical hopper that only outputs items, does not pull from inventories. Both cheaper and better for performance.")
          .worldRestart()
          .define("pipe.enable", true));
      pipeUpwards = cached(builder
          .comment("Allows pipes to output upwards. This removes a limitation on not being able to pipe items up without dropper elevators, but should be balanced alongside modded pipes.")
          .define("pipe.upwards", true));

      // dispenser fluid containers
			/*
			enableDispenserFluidTanks = builder
					.comment("Allows dispensers to fill and empty fluid tanks using fluid containers")
					.worldRestart()
					.define("dispenserFluidTanks", true);
			*/
    }
    builder.pop();

    // recipes
		/*
		builder.push("recipes");
		{
			// anvil smashing
			// configFile.moveProperty("tweaks", "anvilSmashing", "recipes");
			enableAnvilSmashing = builder_override
					.comment("Anvils break glass blocks and transform blocks into other blocks on landing. Uses a block override, so disable if another mod replaces anvils.")
					.worldRestart()
					.define("anvilSmashing", true);

			// cauldron //

			// basic config
			spongeEmptyCauldron = builder
					.comment("Allows sponges to be used to empty the cauldron of dye, water, or potions. Can be 'disabled', 'full' or 'any'. If set to 'full', requires the cauldron to be full, prevents duplicating water but is less useful for removing unwanted fluids.")
					.defineEnum("spongeEmpty", SpongeEmptyCauldron.ANY);

			// extended options
			replaceCauldron = builder_override
					.comment("Replace the cauldron block to allow it to hold other liquids and perform recipes.")
					.worldRestart()
					.define("cauldron", true);
			enableCauldronRecipes = builder
					.comment("Allows additional recipes to be performed in the cauldron. If the block replacement is disabled, functionality will be limited to water in cauldrons.")
					.define("extendCauldron", true);

			builder.push("cauldron");
			{

				enableBiggerCauldron = builder
						.comment("Makes the cauldron hold 4 bottle per bucket instead of 3. Translates better to modded fluids.")
						.worldRestart()
						.define("bigger", false);

				fasterCauldronRain = builder
						.comment("Cauldrons fill faster in the rain than vanilla painfully slow rate.")
						.define("fasterRain", true);
				dropCauldronContents = builder
						.comment("Cauldrons will drop their contents when broken.")
						.define("dropContents", true);


				cauldronObsidian = builder
						.comment("Allows making obsidian in a cauldron by using a lava bucket on a water filled cauldron. Supports modded buckets. If cauldron fluids is enabled, you can also use a water bucket on a lava filled cauldron.")
						.define("obsidian", true);

				// fluids
				enableCauldronFluids = builder
						.comment("Allows cauldrons to be filled with any fluid and use them in recipes")
						.define("fluids.enable", true);
				enableMilk = builder
						.comment("Registers milk as a fluid so it can be used in cauldron recipes.")
						.define("fluids.milk", true);

				// dyeing
				enableCauldronDyeing = builder
						.comment("Allows cauldrons to be filled with dyes and dye items using cauldrons")
						.define("dyeing.enable", true);
				patchVanillaDyeRecipes = builder
						.comment("Makes crafting two dyed water bottles together produce a dyed water bottle. Requires modifying vanilla recipes to prevent a conflict")
						.define("dyeing.patchVanillaRecipes", true);
				extraBottleRecipes = builder
						.comment("Adds extra dyed bottle recipes to craft green and brown")
						.define("dyeing.extraBottleRecipes", true);

				// potions
				enableCauldronPotions = builder
						.comment("Allows cauldrons to be filled with potions and support brewing")
						.define("potions.enable", true);
				enableCauldronBrewing = builder
						.comment("Allows cauldrons to perform brewing recipes.")
						.define("potions.brewing", true);
				expensiveCauldronBrewing = builder
						.comment("Caps brewing at 2 potions per ingredient, requiring 2 ingredients for a full cauldron. Makes the brewing stand still useful and balances better against the bigger cauldron.")
						.define("potions.brewingExpensive", true);
				cauldronTipArrows = builder
						.comment("Allows cauldrons to tip arrows with potions.")
						.define("potions.tippedArrow", true);

				// dispensers
				enableCauldronDispenser = builder
						.comment("Allows dispensers to perform some recipes in the cauldron. Intended to be used for recipes to fill and empty fluid containers as droppers can already be used for recipes")
						.define("dispenser", true);
			}
			builder.pop();
		}
		builder.pop();
		 */

    builder.push("tools");
    {
      // redstone charge
      enableRedstoneCharger = and(toolsModule, builder
          .comment("Enables the redstone charger: a quick pulse created with a flint and steel like item")
          .worldRestart()
          .define("redstoneCharger", true));

      enableChargedArrow = and(toolsModule, builder
          .comment("Enables the charged arrow: places a redstone pulse where it lands")
          .worldRestart()
          .define("chargedArrow", true));

      // lock
      enableLock = and(toolsModule, builder
          .comment("Enables locks and keys: an item allowing you to lock a tile entity to only open for a special named item")
          .worldRestart()
          .define("lock", true));

      // harvest hanging vines
      harvestHangingVines = and(toolsModule, builder
          .comment("When shearing vines, any supported vines will also be sheared instead of just broken")
          .define("shears.harvestHangingVines", true));

      // shears reclaim melons
      shearsReclaimMelons = and(toolsModule, builder
          .comment("Breaking a melon block with shears will always return 9 slices")
          .define("reclaimMelons", true));

      // compass
      enableNorthCompass = and(toolsModule, builder
          .comment("Enables the north compass: a cheaper compass that always points north. Intended to either allow packs to replace the compass or as an alternative for F3 navigation")
          .worldRestart()
          .define("northCompass.enable", true));
			/*
			renameVanillaCompass = builder
					.comment("Renames the vanilla compass to 'origin compass' to help clarify the difference between the two compasses.")
					.worldRestart()
					.define("northCompass.renameVanilla", true);
			 */

      // barometer
      enableBarometer = and(toolsModule, builder
          .comment("Enables the barometer: a tool to measure the player's height in world.")
          .worldRestart()
          .define("barometer", true));

      // photometer
      enablePhotometer = and(toolsModule, builder
          .comment("Enables the photometer: a tool to measure light in world. Can be pointed at a block to measure the light level of that block.")
          .worldRestart()
          .define("photometer", true));

      // waypoint compass
      enableWaypointCompass = and(toolsModule, builder
          .comment("Enables the waypoint compass: a compass which points towards a full beacon.")
          .worldRestart()
          .define("waypointCompass.enable", true));
      dyeWaypointCompass = and(enableWaypointCompass, builder
          .comment("If true, waypoint compasses can be dyed all vanilla colors")
          .worldRestart()
          .define("waypointCompass.dye", true));
      craftWaypointCompass = and(enableWaypointCompass, builder
          .comment("If true, waypoint compasses can be crafted using iron and a blaze rod. If false, they are obtained by using a vanilla compass on a beacon.")
          .worldRestart()
          .define("waypointCompass.craft", true));
      waypointCompassAdvTooltip = cached(builder
          .comment("If true, waypoint compasses show the position target in the advanced item tooltip. Disable for packs that disable coordinates.")
          .define("waypointCompass.advTooltip", true));
      waypointCompassCrossDimension = cached(builder
          .comment("If true, waypoint compasses work across dimensions. The coordinates between the overworld and nether will be adjusted, allowing for portal syncing.")
          .define("waypointCompass.crossDimension", true));
      copyWaypointCompass = and(enableWaypointCompass, builder
          .comment("If true, you can copy the position of one waypoint compass to another in a crafting table, similarly to maps or compasses")
          .worldRestart()
          .define("waypointCompass.copy", true));

      // TODO: consider a way to allow the registry sub, but still have these props set by the server
      // enchantments
      moreShieldEnchantments = cached(builder_override
          .comment("If true, shields can now be enchanted with enchantments such as protection, fire aspect, knockback, and thorns. This requires replacing these enchantments.")
          .worldRestart()
          .define("enchantments.moreShield", true));
      shieldEnchantmentTable = andOverride(moreShieldEnchantments, builder_override
          .comment("If true, shields can be enchanted in an enchantment table. Does not support modded shields as it requires a registry substitution")
          .worldRestart()
          .define("enchantments.shieldTable", true));
      fixShieldTooltip = and(toolsModule, builder
          .comment("If true, fixes the tooltip on shield items so it looks better with both patterns and enchantments")
          .worldRestart()
          .define("enchantments.fixShieldTooltip", true));

      axeWeaponEnchants = cached(builder_override
          .comment("If true, axes will be able to be enchanted with weapon enchants such as looting, fire aspect, and knockback")
          .worldRestart()
          .define("enchantments.axeWeapon", true));
      axeEnchantmentTable = cached(builder_override
          .comment("If true, axes can receive available weapon enchantments at the enchantment table")
          .worldRestart()
          .define("enchantments.axeTable", true));
    }
    builder.pop();

    builder.push("tweaks");
    {
      // pig desaddle
      enablePigDesaddle = and(tweaksModule, builder
          .comment("Allows pigs to be desaddled by shift-right click with an empty hand")
          .define("desaddlePig", true));

      // fitted carpets
      enableFittedCarpets = override(builder_override
          .comment("Replace carpet blocks, allowing them to fit to stairs below them.")
          .worldRestart()
          .define("fittedCarpets", true));

      // waterloggable hoppers
      waterlogHopper = override(builder_override
          .comment("Replace hopper blocks, allowing them to be waterlogged.")
          .worldRestart()
          .define("hopper", true));

      // bonemeal
      builder.push("bonemeal");
      bonemealMushrooms = and(tweaksModule, builder
          .comment("Bonemeal can be used on mycelium to produce mushrooms")
          .define("mushrooms", true));
      bonemealDeadBush = and(tweaksModule, builder
          .comment("Bonemeal can be used on sand to produce dead bushes")
          .define("deadBush", true));
      bonemealGrassSpread = and(tweaksModule, builder
          .comment("Bonemeal can be used on dirt to produce grass if adjecent to grass")
          .define("grassSpread", true));
      bonemealMyceliumSpread = and(tweaksModule, builder
          .comment("Bonemeal can be used on dirt to produce mycelium if adjecent to mycelium")
          .define("myceliumSpread", true));
      builder.pop();

      // heartroot
      enableHeartbeet = and(tweaksModule, builder
          .comment("Enables heartbeets: a rare drop from beetroots which can be eaten to restore a bit of health")
          .worldRestart()
          .define("heartbeet.enable", true));
      brewHeartbeet = and(enableHeartbeet, builder
          .comment("Allows heartbeets to be used as an alternative to ghast tears in making potions of regeneration")
          .worldRestart()
          .define("heartbeet.brewRegeneration", true));  // && enableHeartbeet;
      // TODO: move to loot tables
      heartbeetChance = cached(builder
          .comment("Chance of a heartbeet to drop instead of a normal drop. Formula is two 1 in [chance] chances for it to drop each harvest")
          .defineInRange("heartbeet.chance", 75, 10, 1000));

      // dispensers place anvils
      dispensersPlaceAnvils = and(tweaksModule, builder
          .comment("Dispensers will place anvils instead of dropping them. Plays well with anvil smashing.")
          .worldRestart()
          .define("dispensersPlaceAnvils", true));

      // better cauldron item
      betterCauldronItem = and(tweaksModule, builder
          .comment("Replaces the flat cauldron sprite with the 3D cauldron block model")
          .worldRestart()
          .define("betterCauldronItemModel", true));

      // colored enchanted book ribbons
      coloredEnchantedRibbons = and(tweaksModule, builder
          .comment("The ribbon on enchanted books colors based on the enchantment rarity")
          .worldRestart()
          .define("coloredEnchantedRibbons", true));

      // colored fireworks
      coloredFireworkItems = and(tweaksModule, builder
          .comment("Colors the fireworks item based on the colors of the stars")
          .worldRestart()
          .define("coloredFireworkItems", true));

      // lilypad fall breaking
      lilypadBreakFall = and(tweaksModule, builder
          .comment("Lily pads prevent fall damage, but break in the process")
          .define("lilypadBreakFall", true));

      // stackable alternative recipes
      unstackableRecipeAlts = and(tweaksModule, builder
          .comment("Adds stackable recipes to some vanilla or Inspriations items that require unstackable items to craft")
          .worldRestart()
          .define("unstackableRecipeAlts", true));

      // seeds
      builder.push("seeds");
      {
        builder.push("blockCrops");
        {
          enableBlockCrops = and(tweaksModule, builder
              .comment("If true, adds seeds for cactus and sugar cane, useful for recipes for the crops")
              .worldRestart()
              .define("enable", true));
          smoothBlockCropGrowth = and(enableBlockCrops, builder
              .comment("If true, cactus and sugar cane will grow in 2 pixel increments using the block crops")
              .define("smoothGrowth", true));
          bonemealBlockCrop = and(tweaksModule, builder
              .comment("If true, allows bonemeal to be used to speed block crop growth")
              .define("bonemeal", false));
          nerfCactusFarms = cached(builder
              .comment("If false, cactus seeds planted on cactus have fewer restrictions.",
                       "Setting to true means cactus seeds are broken by neighboring blocks, meaning classic cactus farms will drop cactus seeds instead of full cactus.")
              .define("nerfCactusFarms", false));
        }
        builder.pop();
				/*
				builder.push("veggies");
				{
					enableMoreSeeds = builder
							.comment("Adds seeds for carrots and potatoes.")
							.worldRestart()
							.define("enable", true);
					addGrassDrops = builder
							.comment("Makes carrot and potato seeds drop from grass")
							.define("grassDrops", true);
					nerfCarrotPotatoDrops = builder
							.comment("Makes carrots and potatoes drop their respective seed if not fully grown")
							.define("nerfCarrotPotatoDrops", true);
				}
				builder.pop();
				 */
      }
      builder.pop();

      // milk cooldown
      milkCooldown = cached(builder
          .comment("Adds a cooldown to milking cows, prevents practically infinite milk in modded worlds where milk is more useful.")
          .define("milkCooldown.enable", false));
      milkCooldownTime = cached(builder
          .comment("Delay in seconds after milking a cow before it can be milked again.")
          .defineInRange("milkCooldown.time", 600, 1, Short.MAX_VALUE));

      // milk squids
			/*
			milkSquids = builder
					.comment("Allows milking squids with a glass bottle to get black dyed water.")
					.define("milkSquids.enable", true);
			milkSquidCooldown = builder
					.comment("Delay in seconds after milking a squid before it can be milked again.")
					.defineInRange("milkSquids.cooldown", 300, 1, Short.MAX_VALUE);

			 */

      // portal color
      customPortalColor = and(tweaksModule, builder
          .comment("Allows the portal color to be changed by placing colored blocks under the portal. Any block that tints a beacon beam will work for the color.")
          .define("customPortalColor", true));

      // drops
      caveSpiderDrops = and(tweaksModule, builder
          .comment("If true, cave spiders will rarely drop webs, giving them an advantage to farm over regular spiders")
          .define("caveSpiderWeb", true));
      skeletonSkull = and(tweaksModule, builder
          .comment("If true, skeletons will rarely drop their skull for consistency with wither skeletons. Does not affect creeper or zombie heads.")
          .define("skeletonSkull", true));
    }
    builder.pop();
  }


  /* Helpers */

  /**
   * Creates a cached config value and adds it to the list to be invalidated on reload
   * @param value  Config value
   * @param <T>    Value type
   * @return  Cached config value
   */
  private static <T> CachedValue<T> cached(ConfigValue<T> value) {
    CachedValue<T> cached = new CachedValue<>(value);
    CACHED_VALUES.add(cached);
    return cached;
  }

  /**
   * Creates a cached boolean value and adds it to the list to be invalidated on realod
   * @param value  Boolean config value
   * @return  Cached config value
   */
  private static CachedBoolean cached(BooleanValue value) {
    CachedBoolean cached = new CachedBoolean(value);
    CACHED_VALUES.add(cached);
    return cached;
  }

  /**
   * Creates a cached config value by anding two config values
   * @param first   First config value, typically a module
   * @param second  Property config value
   * @return  Cached config value
   */
  private static CachedBoolean and(CachedBoolean first, BooleanValue second) {
    CachedBoolean cached = new CachedBoolean(() -> first.get() && second.get());
    CACHED_VALUES.add(cached);
    return cached;
  }

  /**
   * Creates a cached config value by anding three config values
   * @param first   First config value, typically a module
   * @param second  Second config value
   * @param third   Property config value
   * @return  Cached config value
   */
  private static CachedBoolean and(CachedBoolean first, CachedBoolean second, BooleanValue third) {
    CachedBoolean cached = new CachedBoolean(() -> first.get() && second.get() && third.get());
    CACHED_VALUES.add(cached);
    return cached;
  }

  /* Override methods */

  /**
   * Creates a cached config value for an override config value. Unlike {@link #cached(BooleanValue)}, override does not invalidate as it does not reload.
   * @param value  Config value
   * @return  Cached config value
   */
  private static CachedBoolean override(BooleanValue value) {
    return new CachedBoolean(value);
  }

  /**
   * Ands two override config properties together, caching the result
   * @param first   First config value
   * @param second  Property config value
   * @return  Cached config value
   */
  private static CachedBoolean andOverride(CachedBoolean first, BooleanValue second) {
    return new CachedBoolean(() -> first.get() && second.get());
  }

  /**
   * Clears the cache of all regular config values. Called during the config loaded event
   */
  public static void clearCache() {
    CACHED_VALUES.forEach(CachedValue::invalidate);
  }
}
