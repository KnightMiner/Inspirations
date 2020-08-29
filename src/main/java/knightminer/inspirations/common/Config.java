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
  private static final List<CachedValue<?>> SERVER_VALUES = new ArrayList<>();
  /** List of all cached client config values, for cache clearing */
  private static final List<CachedValue<?>> CLIENT_VALUES = new ArrayList<>();

  // TODO: add client config
  public static final ForgeConfigSpec SERVER_SPEC;
  public static final ForgeConfigSpec CLIENT_SPEC;
  public static final ForgeConfigSpec OVERRIDE_SPEC;

  // modules
  public static final CachedBoolean buildingModule;
  public static final CachedBoolean utilityModule;
  public static final CachedBoolean toolsModule;
  public static final CachedBoolean tweaksModule;

  // general
  public static final CachedBoolean showAllVariants;

  // building
  public static final CachedBoolean enableRope;
  public static final CachedBoolean enableRopeLadder;

  public static final CachedBoolean enableGlassDoor;
  public static final CachedBoolean enableMulch;
  public static final CachedBoolean enablePath;
  public static final CachedBoolean enableFlowers;
  public static final CachedBoolean enableEnlightenedBush;
  // OVERRIDE
  public static final CachedBoolean climbableIronBars;


  public static final CachedBoolean enableBookshelf;
  public static final CachedBoolean enableColoredBooks;
  public static final CachedBoolean bookshelvesBoostEnchanting;
  public static final CachedValue<Double> defaultEnchantingPower;

  public static final CachedValue<String> bookKeywords;
  private static final String BOOK_KEYWORD_DEFAULTS = "almanac, atlas, book, catalogue, concordance, dictionary, directory, encyclopedia, guide, journal, lexicon, manual, thesaurus, tome";

  // utility
  public static final CachedBoolean enableTorchLever;
  public static final CachedBoolean enableRedstoneBook;
  public static final CachedBoolean enableCarpetedTrapdoor;
  public static final CachedBoolean enableCarpetedPressurePlate;
  public static final CachedBoolean enableCollector;
  public static final CachedBoolean enablePipe;
  public static final CachedBoolean pipeUpwards;
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

  // tools
  public static final CachedBoolean enableLock;
  public static final CachedBoolean enableRedstoneCharger;
  public static final CachedBoolean enableChargedArrow;
  public static final CachedBoolean harvestHangingVines;
  public static final CachedBoolean shearsReclaimMelons;
  public static final CachedBoolean enableNorthCompass;
  public static final CachedBoolean enableBarometer;
  public static final CachedBoolean enablePhotometer;

  // waypoint compass
  public static final CachedBoolean enableWaypointCompass;
  public static final CachedBoolean dyeWaypointCompass;
  public static final CachedBoolean craftWaypointCompass;
  public static final CachedBoolean copyWaypointCompass;
  public static final CachedBoolean waypointCompassAdvTooltip;
  public static final CachedBoolean waypointCompassCrossDimension;

  // enchantments
  public static final CachedBoolean moreShieldEnchantments;
  public static final CachedBoolean shieldEnchantmentTable;

  public static final CachedBoolean fixShieldTooltip;
  public static final CachedBoolean axeWeaponEnchants;
  public static final CachedBoolean axeEnchantmentTable;

  // tweaks
  public static final CachedBoolean enablePigDesaddle;
  public static final CachedBoolean enableFittedCarpets;
  public static final CachedBoolean lilypadBreakFall;
  public static final CachedBoolean unstackableRecipeAlts;
  public static final CachedBoolean dispensersPlaceAnvils;
  public static final CachedBoolean milkCooldown;
  public static final CachedValue<Integer> milkCooldownTime;
  public static final CachedBoolean waterlogHopper;

  // client
  public static final CachedBoolean betterCauldronItem;
  public static final CachedBoolean coloredEnchantedRibbons;
  public static final CachedBoolean coloredFireworkItems;
  public static final CachedBoolean customPortalColor;

  // heartbeet
  public static final CachedBoolean enableHeartbeet;
  public static final CachedBoolean brewHeartbeet;

  public static final CachedValue<Integer> heartbeetChance;

  // seeds
  public static final CachedBoolean enableBlockCrops;
  public static final CachedBoolean smoothBlockCropGrowth;
  public static final CachedBoolean bonemealBlockCrop;

  public static final CachedBoolean nerfCactusFarms;
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
  public static final CachedBoolean bonemealMushrooms;
  public static final CachedBoolean bonemealDeadBush;
  public static final CachedBoolean bonemealGrassSpread;
  public static final CachedBoolean bonemealMyceliumSpread;

  public static final CachedBoolean caveSpiderDrops;
  public static final CachedBoolean skeletonSkull;

  static {
    Builder server = new Builder();
    Builder client = new Builder();
    Builder override = new Builder();

    // modules root config
    server.push("modules");
    {
      buildingModule = server(server
          .comment("Building blocks to improve decoration")
          .worldRestart()
          .define("building", true));

      utilityModule = server(server
          .comment("Adds tools for automation and redstone interaction")
          .worldRestart()
          .define("utility", true));

      tweaksModule = server(server
          .comment("Contains tweaks to vanilla features")
          .worldRestart()
          .define("tweaks", true));

      toolsModule = server(server
          .comment("Includes new tools to reduce dependency on debug features")
          .worldRestart()
          .define("tools", true));
    }
    server.pop();

    showAllVariants = server(server
        .comment("Shows all variants for dynamically textured blocks, like bookshelves. If false just the first will be shown")
        .define("general.showAllVariants", true));

    /*
     * Building module
     */

    server.push("building");
    {
      // bookshelves
      enableBookshelf = and(buildingModule, server
          .comment("Enables the bookshelf: a decorative block to display books")
          .worldRestart()
          .define("bookshelf.enable", true));
      enableColoredBooks = and(enableBookshelf, server
          .comment("Enables colored books: basically colored versions of the vanilla book to decorate bookshelves")
          .worldRestart()
          .define("bookshelf.coloredBooks", true));
      bookshelvesBoostEnchanting = and(buildingModule, server
          .comment("If true, bookshelves will increase enchanting table power.")
          .define("bookshelf.boostEnchanting", true));
      defaultEnchantingPower = server(server
          .comment("Default power for a book for enchanting, can be overridden in the book overrides.")
          .defineInRange("bookshelf.defaultEnchanting", 1.5f, 0.0f, 15.0f));
      bookKeywords = server(server
          .comment("List of keywords for valid books, used to determine valid books in the bookshelf. Separate each by commas.")
          .define("bookshelf.bookKeywords", BOOK_KEYWORD_DEFAULTS));


      // rope
      enableRope = and(buildingModule, server
          .comment("Enables rope: can be climbed like ladders and extended with additional rope")
          .worldRestart()
          .define("rope", true));
      enableRopeLadder = and(enableRope, server
          .comment("Enables rope ladders: right click ropes with sticks to extend the hitbox")
          .define("ropeLadder", true));
      climbableIronBars = override(override
          .comment("Makes iron bars climbable if a rope is below them.")
          .worldRestart()
          .define("climbableBars", true));

      // glass door
      enableGlassDoor = and(buildingModule, server
          .comment("Enables glass doors and trapdoors: basically doors, but made of glass. Not sure what you would expect.")
          .worldRestart()
          .define("glassDoor", true));

      // mulch
      enableMulch = and(buildingModule, server
          .comment("Enables mulch: a craftable falling block which supports plants such as flowers")
          .worldRestart()
          .define("mulch", true));

      // path
      enablePath = and(buildingModule, server
          .comment("Enables stone paths: a carpet like decorative block for making decorative paths")
          .worldRestart()
          .define("path", true));

      // flowers
      enableFlowers = and(buildingModule, server
          .comment("Enables additional flowers from breaking double flowers with shears.")
          .worldRestart()
          .define("flowers", true));

      // enlightenedBush
      enableEnlightenedBush = and(buildingModule, server
          .comment("Enables enlightened bushes: bushes with lights.")
          .worldRestart()
          .define("enlightenedBush", true));
    }
    server.pop();

    /*
     * Utility module
     */
    server.push("utility");
    {
      enableRedstoneBook = and(utilityModule, enableBookshelf, server
          .comment("Enables the trapped book: will emit redstone power when placed in a bookshelf. Requires bookshelf.")
          .worldRestart()
          .define("redstoneBook", true));

      // torch lever
      enableTorchLever = and(utilityModule, server
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
      enableCarpetedTrapdoor = and(utilityModule, server
          .comment("Enables carpeted trapdoors: a trapdoor which appears to be a carpet when closed")
          .worldRestart()
          .define("carpetedTrapdoor", true));

      // carpeted pressure plate
      enableCarpetedPressurePlate = and(utilityModule, server
          .comment("Allows placing a carpet on a stone pressure plate to hide it")
          .worldRestart()
          .define("carpetedPressurePlate", true));

      // collector
      enableCollector = and(utilityModule, server
          .comment("Enables the collector: extracts items from inventories or the world similar to a hopper, but can face in all 6 directions and cannot place items in inventories")
          .worldRestart()
          .define("collector", true));

      // pipe
      enablePipe = and(utilityModule, server
          .comment("Enables pipes: a more economical hopper that only outputs items, does not pull from inventories. Both cheaper and better for performance.")
          .worldRestart()
          .define("pipe.enable", true));
      pipeUpwards = server(server
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
    server.pop();

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

    /*
     * Tools module
     */
    server.push("tools");
    {
      // redstone charge
      enableRedstoneCharger = and(toolsModule, server
          .comment("Enables the redstone charger: a quick pulse created with a flint and steel like item")
          .worldRestart()
          .define("redstoneCharger", true));

      enableChargedArrow = and(toolsModule, server
          .comment("Enables the charged arrow: places a redstone pulse where it lands")
          .worldRestart()
          .define("chargedArrow", true));

      // lock
      enableLock = and(toolsModule, server
          .comment("Enables locks and keys: an item allowing you to lock a tile entity to only open for a special named item")
          .worldRestart()
          .define("lock", true));

      // harvest hanging vines
      harvestHangingVines = and(toolsModule, server
          .comment("When shearing vines, any supported vines will also be sheared instead of just broken")
          .define("shears.harvestHangingVines", true));

      // shears reclaim melons
      shearsReclaimMelons = and(toolsModule, server
          .comment("Breaking a melon block with shears will always return 9 slices")
          .define("reclaimMelons", true));

      // compass
      enableNorthCompass = and(toolsModule, server
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
      enableBarometer = and(toolsModule, server
          .comment("Enables the barometer: a tool to measure the player's height in world.")
          .worldRestart()
          .define("barometer", true));

      // photometer
      enablePhotometer = and(toolsModule, server
          .comment("Enables the photometer: a tool to measure light in world. Can be pointed at a block to measure the light level of that block.")
          .worldRestart()
          .define("photometer", true));

      // waypoint compass
      enableWaypointCompass = and(toolsModule, server
          .comment("Enables the waypoint compass: a compass which points towards a full beacon.")
          .worldRestart()
          .define("waypointCompass.enable", true));
      dyeWaypointCompass = and(enableWaypointCompass, server
          .comment("If true, waypoint compasses can be dyed all vanilla colors")
          .worldRestart()
          .define("waypointCompass.dye", true));
      craftWaypointCompass = and(enableWaypointCompass, server
          .comment("If true, waypoint compasses can be crafted using iron and a blaze rod. If false, they are obtained by using a vanilla compass on a beacon.")
          .worldRestart()
          .define("waypointCompass.craft", true));
      waypointCompassAdvTooltip = server(server
          .comment("If true, waypoint compasses show the position target in the advanced item tooltip. Disable for packs that disable coordinates.")
          .define("waypointCompass.advTooltip", true));
      waypointCompassCrossDimension = server(server
          .comment("If true, waypoint compasses work across dimensions. The coordinates between the overworld and nether will be adjusted, allowing for portal syncing.")
          .define("waypointCompass.crossDimension", true));
      copyWaypointCompass = and(enableWaypointCompass, server
          .comment("If true, you can copy the position of one waypoint compass to another in a crafting table, similarly to maps or compasses")
          .worldRestart()
          .define("waypointCompass.copy", true));

      // TODO: consider a way to allow the registry sub, but still have these props set by the server
      // enchantments
      moreShieldEnchantments = server(override
          .comment("If true, shields can now be enchanted with enchantments such as protection, fire aspect, knockback, and thorns. This requires replacing these enchantments.")
          .worldRestart()
          .define("enchantments.moreShield", true));
      shieldEnchantmentTable = andOverride(moreShieldEnchantments, override
          .comment("If true, shields can be enchanted in an enchantment table. Does not support modded shields as it requires a registry substitution")
          .worldRestart()
          .define("enchantments.shieldTable", true));
      fixShieldTooltip = and(toolsModule, server
          .comment("If true, fixes the tooltip on shield items so it looks better with both patterns and enchantments")
          .worldRestart()
          .define("enchantments.fixShieldTooltip", true));

      axeWeaponEnchants = server(override
          .comment("If true, axes will be able to be enchanted with weapon enchants such as looting, fire aspect, and knockback")
          .worldRestart()
          .define("enchantments.axeWeapon", true));
      axeEnchantmentTable = server(override
          .comment("If true, axes can receive available weapon enchantments at the enchantment table")
          .worldRestart()
          .define("enchantments.axeTable", true));
    }
    server.pop();

    /*
     * Tweaks module
     */
    server.push("tweaks");
    {
      // pig desaddle
      enablePigDesaddle = and(tweaksModule, server
          .comment("Allows pigs to be desaddled by shift-right click with an empty hand")
          .define("desaddlePig", true));

      // fitted carpets
      enableFittedCarpets = override(override
          .comment("Replace carpet blocks, allowing them to fit to stairs below them.")
          .worldRestart()
          .define("fittedCarpets", true));

      // waterloggable hoppers
      waterlogHopper = override(override
          .comment("Replace hopper blocks, allowing them to be waterlogged.")
          .worldRestart()
          .define("waterlogHoppers", true));

      // bonemeal
      server.push("bonemeal");
      bonemealMushrooms = and(tweaksModule, server
          .comment("Bonemeal can be used on mycelium to produce mushrooms")
          .define("mushrooms", true));
      bonemealDeadBush = and(tweaksModule, server
          .comment("Bonemeal can be used on sand to produce dead bushes")
          .define("deadBush", true));
      bonemealGrassSpread = and(tweaksModule, server
          .comment("Bonemeal can be used on dirt to produce grass if adjecent to grass")
          .define("grassSpread", true));
      bonemealMyceliumSpread = and(tweaksModule, server
          .comment("Bonemeal can be used on dirt to produce mycelium if adjecent to mycelium")
          .define("myceliumSpread", true));
      server.pop();

      // heartroot
      enableHeartbeet = and(tweaksModule, server
          .comment("Enables heartbeets: a rare drop from beetroots which can be eaten to restore a bit of health")
          .worldRestart()
          .define("heartbeet.enable", true));
      brewHeartbeet = and(enableHeartbeet, server
          .comment("Allows heartbeets to be used as an alternative to ghast tears in making potions of regeneration")
          .worldRestart()
          .define("heartbeet.brewRegeneration", true));

      // TODO: move to loot tables
      heartbeetChance = server(server
          .comment("Chance of a heartbeet to drop instead of a normal drop. Formula is two 1 in [chance] chances for it to drop each harvest")
          .defineInRange("heartbeet.chance", 75, 10, 1000));

      // dispensers place anvils
      dispensersPlaceAnvils = and(tweaksModule, server
          .comment("Dispensers will place anvils instead of dropping them. Plays well with anvil smashing.")
          .worldRestart()
          .define("dispensersPlaceAnvils", true));

      // lilypad fall breaking
      lilypadBreakFall = and(tweaksModule, server
          .comment("Lily pads prevent fall damage, but break in the process")
          .define("lilypadBreakFall", true));

      // stackable alternative recipes
      unstackableRecipeAlts = and(tweaksModule, server
          .comment("Adds stackable recipes to some vanilla or Inspriations items that require unstackable items to craft")
          .worldRestart()
          .define("unstackableRecipeAlts", true));

      // drops
      caveSpiderDrops = and(tweaksModule, server
          .comment("If true, cave spiders will rarely drop webs, giving them an advantage to farm over regular spiders")
          .define("caveSpiderWeb", true));
      skeletonSkull = and(tweaksModule, server
          .comment("If true, skeletons will rarely drop their skull for consistency with wither skeletons. Does not affect creeper or zombie heads.")
          .define("skeletonSkull", true));

      // milk cooldown
      milkCooldown = server(server
          .comment("Adds a cooldown to milking cows, prevents practically infinite milk in modded worlds where milk is more useful.")
          .define("milkCooldown.enable", false));
      milkCooldownTime = server(server
          .comment("Delay in seconds after milking a cow before it can be milked again.")
          .defineInRange("milkCooldown.time", 600, 1, Short.MAX_VALUE));

      // seeds
      server.push("seeds");
      {
        server.push("blockCrops");
        {
          enableBlockCrops = and(tweaksModule, server
              .comment("If true, adds seeds for cactus and sugar cane, useful for recipes for the crops")
              .worldRestart()
              .define("enable", true));
          smoothBlockCropGrowth = and(enableBlockCrops, server
              .comment("If true, cactus and sugar cane will grow in 2 pixel increments using the block crops")
              .define("smoothGrowth", true));
          bonemealBlockCrop = and(tweaksModule, server
              .comment("If true, allows bonemeal to be used to speed block crop growth")
              .define("bonemeal", false));
          nerfCactusFarms = server(server
              .comment("If false, cactus seeds planted on cactus have fewer restrictions.",
                       "Setting to true means cactus seeds are broken by neighboring blocks, meaning classic cactus farms will drop cactus seeds instead of full cactus.")
              .define("nerfCactusFarms", false));
        }
        server.pop();
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
      server.pop();

      // milk squids
			/*
			milkSquids = builder
					.comment("Allows milking squids with a glass bottle to get black dyed water.")
					.define("milkSquids.enable", true);
			milkSquidCooldown = builder
					.comment("Delay in seconds after milking a squid before it can be milked again.")
					.defineInRange("milkSquids.cooldown", 300, 1, Short.MAX_VALUE);

			 */
    }
    server.pop();

    /*
     * client properties
     */
    {
      // better cauldron item
      betterCauldronItem = client(client
          .comment("Replaces the flat cauldron sprite with the 3D cauldron block model")
          .define("betterCauldronItemModel", true));

      // colored fireworks
      coloredFireworkItems = client(client
          .comment("Colors the fireworks item based on the colors of the stars")
          .worldRestart()
          .define("coloredFireworkItems", true));

      // colored enchanted book ribbons
      coloredEnchantedRibbons = client(client
          .comment("The ribbon on enchanted books colors based on the enchantment rarity")
          .worldRestart()
          .define("coloredEnchantedRibbons", true));

      // portal color
      customPortalColor = client(client
          .comment("Allows the portal color to be changed by placing colored blocks under the portal. Any block that tints a beacon beam will work for the color.")
          .worldRestart()
          .define("customPortalColor", true));
    }

    // build all specs
    SERVER_SPEC = server.build();
    CLIENT_SPEC = client.build();
    OVERRIDE_SPEC = override.build();
  }


  /* Helpers */

  /**
   * Creates a cached config value and adds it to the list to be invalidated on reload
   * @param value  Config value
   * @param <T>    Value type
   * @return  Cached config value
   */
  private static <T> CachedValue<T> server(ConfigValue<T> value) {
    CachedValue<T> cached = new CachedValue<>(value);
    SERVER_VALUES.add(cached);
    return cached;
  }

  /**
   * Creates a cached boolean value and adds it to the list to be invalidated on reload
   * @param value  Boolean config value
   * @return  Cached config value
   */
  private static CachedBoolean server(BooleanValue value) {
    CachedBoolean cached = new CachedBoolean(value);
    SERVER_VALUES.add(cached);
    return cached;
  }

  /**
   * Creates a cached boolean value for the client and adds it to the list to be invalidated on reload
   * @param value  Boolean config value
   * @return  Cached config value
   */
  private static CachedBoolean client(BooleanValue value) {
    CachedBoolean cached = new CachedBoolean(value);
    CLIENT_VALUES.add(cached);
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
    SERVER_VALUES.add(cached);
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
    SERVER_VALUES.add(cached);
    return cached;
  }

  /* Override methods */

  /**
   * Creates a cached config value for an override config value. Unlike {@link #server(BooleanValue)}, override does not invalidate as it does not reload.
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
   * @param spec
   */
  public static void clearCache(ForgeConfigSpec spec) {
    if (spec == SERVER_SPEC) {
      SERVER_VALUES.forEach(CachedValue::invalidate);
    } else if (spec == CLIENT_SPEC) {
      CLIENT_VALUES.forEach(CachedValue::invalidate);
    }
  }
}
