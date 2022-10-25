package knightminer.inspirations.common;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.InspirationsRegistry;
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

import java.util.Arrays;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class Config {
  /** Config for anything that affects gameplay */
  public static final ForgeConfigSpec SERVER_SPEC;
  /** Config for anything that is visual only */
  public static final ForgeConfigSpec CLIENT_SPEC;
  /** Config for things affecting gameplay that must load before registration */
  public static final ForgeConfigSpec OVERRIDE_SPEC;

  // modules
  public static final BooleanValue buildingModule;
  public static final BooleanValue utilityModule;
  public static final BooleanValue toolsModule;
  public static final BooleanValue tweaksModule;
  public static final BooleanValue recipesModule;

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

  @Deprecated
  private static final ConfigValue<String> bookKeywords;
  private static final String BOOK_KEYWORD_DEFAULTS = "almanac, atlas, book, catalogue, concordance, dictionary, directory, encyclopedia, guide, journal, lexicon, manual, thesaurus, tome";

  // utility
  public static final BooleanSupplier enableTorchLever;
  public static final BooleanSupplier enableRedstoneBook;
  public static final BooleanSupplier enableCarpetedTrapdoor;
  public static final BooleanSupplier enableCarpetedPressurePlate;
  public static final BooleanSupplier enableCollector;
  public static final BooleanSupplier enablePipe;
  public static final BooleanValue pipeUpwards;
  //public static BooleanValue enableDispenserFluidTanks;
  //public static BooleanValue milkSquids;
  //public static IntValue milkSquidCooldown;

  // recipes

  // cauldron
  public static final BooleanSupplier cauldronRecipes;
  public static final BooleanSupplier cauldronConcrete;
  // extended
  public static final BooleanValue extendedCauldron;
  public static final BooleanSupplier extendedCauldronRecipes;
  public static final BooleanSupplier cauldronIce;
  // fluids
  public static final BooleanSupplier enableCauldronFluids;
  // dyes
  public static final BooleanSupplier enableCauldronDyeing;
  public static final BooleanSupplier extraBottleRecipes;
  // potions
  public static final BooleanSupplier enableCauldronPotions;
  public static final BooleanSupplier enableCauldronBrewing;
  public static final BooleanSupplier cauldronTipArrows;
  // misc
  public static final BooleanSupplier fasterCauldronRain;

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
  public static final BooleanValue moreShieldEnchantments;
  public static final BooleanSupplier shieldEnchantmentTable;

  public static final BooleanSupplier fixShieldTooltip;
  public static final BooleanValue axeWeaponEnchants;
  public static final BooleanValue axeEnchantmentTable;

  // tweaks
  public static final BooleanSupplier enablePigDesaddle;
  public static final BooleanValue enableFittedCarpets;
  public static final BooleanSupplier lilypadBreakFall;
  public static final BooleanSupplier unstackableRecipeAlts;
  public static final BooleanSupplier dispensersPlaceAnvils;
  public static final BooleanValue milkCooldown;
  public static final ConfigValue<Integer> milkCooldownTime;
  public static final BooleanValue waterlogHopper;

  // client
  public static final BooleanValue betterCauldronItem;
  public static final BooleanValue coloredEnchantedRibbons;
  public static final BooleanValue coloredFireworkItems;
  public static final BooleanValue customPortalColor;

  // heartbeet
  public static final BooleanSupplier enableHeartbeet;
  public static final BooleanSupplier brewHeartbeet;

  // seeds
  public static final BooleanSupplier enableBlockCrops;
  public static final BooleanSupplier smoothBlockCropGrowth;
  public static final BooleanSupplier bonemealBlockCrop;

  public static final BooleanValue nerfCactusFarms;
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
  public static final BooleanSupplier bonemealMushrooms;
  public static final BooleanSupplier bonemealDeadBush;
  public static final BooleanSupplier bonemealGrassSpread;
  public static final BooleanSupplier bonemealMyceliumSpread;

  public static final BooleanSupplier caveSpiderDrops;
  public static final BooleanSupplier skeletonSkull;

  static {
    Builder server = new Builder();
    Builder client = new Builder();
    Builder override = new Builder();

    // modules root config
    server.push("modules");
    {
      buildingModule = server
          .comment("Building blocks to improve decoration")
          .worldRestart()
          .define("building", true);

      utilityModule = server
          .comment("Adds tools for automation and redstone interaction")
          .worldRestart()
          .define("utility", true);

      tweaksModule = server
          .comment("Contains tweaks to vanilla features")
          .worldRestart()
          .define("tweaks", true);

      toolsModule = server
          .comment("Includes new tools to reduce dependency on debug features")
          .worldRestart()
          .define("tools", true);

      recipesModule = server
          .comment("Includes new blocks that add new types of recipes")
          .worldRestart()
          .define("tools", true);
    }
    server.pop();

    showAllVariants = server
        .comment("Shows all variants for dynamically textured blocks, like bookshelves. If false just the first will be shown")
        .define("general.showAllVariants", true);

    /*
     * Building module
     */

    server.push("building");
    {
      // bookshelves
      enableBookshelf = and(buildingModule, server
          .comment("Enables the shelf: a decorative block to display books and other items")
          .worldRestart()
          .define("bookshelf.enable", true));
      enableColoredBooks = and(enableBookshelf, server
          .comment("Enables colored books: basically colored versions of the vanilla book to decorate shelves")
          .worldRestart()
          .define("bookshelf.coloredBooks", true));
      bookshelvesBoostEnchanting = and(buildingModule, server
          .comment("If true, shelves will increase enchanting table power.")
          .define("bookshelf.boostEnchanting", true));
      defaultEnchantingPower = server
          .comment("Default power for a book for enchanting, can be overridden in the book overrides.")
          .defineInRange("bookshelf.defaultEnchanting", 1.5f, 0.0f, 15.0f);
      bookKeywords = server
          .comment("List of keywords for valid books, used to determine books in the shelf (non-books take more space). Separate each by commas.")
          .define("bookshelf.bookKeywords", BOOK_KEYWORD_DEFAULTS);


      // rope
      enableRope = and(buildingModule, server
          .comment("Enables rope: can be climbed like ladders and extended with additional rope")
          .worldRestart()
          .define("rope", true));
      enableRopeLadder = and(enableRope, server
          .comment("Enables rope ladders: right click ropes with sticks to extend the hitbox")
          .define("ropeLadder", true));
      climbableIronBars = override
          .comment("Makes iron bars climbable if a rope is below them.")
          .worldRestart()
          .define("climbableBars", true);

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
      enableRedstoneBook = and(enableBookshelf, utilityModule, server
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
      pipeUpwards = server
          .comment("Allows pipes to output upwards. This removes a limitation on not being able to pipe items up without dropper elevators, but should be balanced alongside modded pipes.")
          .define("pipe.upwards", true);

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
		server.push("recipes");
		{
		  /*
			// anvil smashing
			// configFile.moveProperty("tweaks", "anvilSmashing", "recipes");
			enableAnvilSmashing = builder_override
					.comment("Anvils break glass blocks and transform blocks into other blocks on landing. Uses a block override, so disable if another mod replaces anvils.")
					.worldRestart()
					.define("anvilSmashing", true);
			*/

			// cauldron //

      // extended options
      extendedCauldron = override
					.comment("Replace the cauldron block to allow it to hold other liquids and perform extended recipes.")
					.worldRestart()
					.define("cauldron", true);

			server.push("cauldron");
			{
			  // base config
        cauldronRecipes = and(recipesModule, server
            .comment("Allows additional recipes to be performed in the cauldron. If the block replacement is disabled, functionality will be limited to water in cauldrons.")
            .define("enable", true));
        extendedCauldronRecipes = and(cauldronRecipes, extendedCauldron);
        
        // base recipes
        cauldronConcrete = and(cauldronRecipes, server
            .comment("Allows concrete to be made in the cauldron")
            .define("concrete", true));
        cauldronIce = and(extendedCauldronRecipes, server
            .comment("If true, the cauldron can be used to make ice when in a cold biome or surrounded with ice")
            .define("ice", true));

				// fluids
				enableCauldronFluids = and(extendedCauldronRecipes, server
						.comment("Allows cauldrons to be filled with any fluid and use them in recipes")
						.define("fluids.enable", true));

				// dyeing
				enableCauldronDyeing = and(extendedCauldronRecipes, server
						.comment("Allows cauldrons to be filled with dyes and dye items using cauldrons")
						.define("dyeing.enable", true));
				extraBottleRecipes = and(enableCauldronDyeing, server
						.comment("Adds extra dyed bottle recipes to craft green and brown")
						.define("dyeing.extraBottleRecipes", true));

				// potions
				enableCauldronPotions = and(cauldronRecipes, extendedCauldron, server
						.comment("Allows cauldrons to be filled with potions and support brewing")
						.define("potions.enable", true));
				enableCauldronBrewing = and(enableCauldronPotions, server
						.comment("Allows cauldrons to perform brewing recipes.")
						.define("potions.brewing", true));
				/* TODO: reconsider
				expensiveCauldronBrewing = builder
						.comment("Caps brewing at 2 potions per ingredient, requiring 2 ingredients for a full cauldron. Makes the brewing stand still useful and balances better against the bigger cauldron.")
						.define("potions.brewingExpensive", true);
				*/
				cauldronTipArrows = and(enableCauldronPotions, server
						.comment("Allows cauldrons to tip arrows with potions.")
						.define("potions.tippedArrow", true));

        fasterCauldronRain = and(recipesModule, extendedCauldron, server
            .comment("If true, cauldrons fill faster in the rain.")
            .define("fasterRain", true));

				// dispensers
        /* TODO: reconsider
				enableCauldronDispenser = builder
						.comment("Allows dispensers to perform some recipes in the cauldron. Intended to be used for recipes to fill and empty fluid containers as droppers can already be used for recipes")
						.define("dispenser", true);
						*/
			}
      server.pop();
		}
    server.pop();

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
      enableDimensionCompass = and(toolsModule, server
          .comment("Enables the dimension compass: a compass which points towards the place you entered a dimension.", "When used on a lodestone, works across dimensions taking nether coordinates into account")
          .worldRestart()
          .define("dimensionCompass.enable", true));

      // TODO: consider a way to allow the registry sub, but still have these props set by the server
      // enchantments
      moreShieldEnchantments = override
          .comment("If true, shields can now be enchanted with enchantments such as protection, fire aspect, knockback, and thorns. This requires replacing these enchantments.")
          .worldRestart()
          .define("enchantments.moreShield", true);
      shieldEnchantmentTable = and(moreShieldEnchantments, override
          .comment("If true, shields can be enchanted in an enchantment table. Does not support modded shields as it requires a registry substitution")
          .worldRestart()
          .define("enchantments.shieldTable", true));
      fixShieldTooltip = and(toolsModule, server
          .comment("If true, fixes the tooltip on shield items so it looks better with both patterns and enchantments")
          .worldRestart()
          .define("enchantments.fixShieldTooltip", true));

      axeWeaponEnchants = override
          .comment("If true, axes will be able to be enchanted with weapon enchants such as looting, fire aspect, and knockback")
          .worldRestart()
          .define("enchantments.axeWeapon", true);
      axeEnchantmentTable = override
          .comment("If true, axes can receive available weapon enchantments at the enchantment table")
          .worldRestart()
          .define("enchantments.axeTable", true);
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
      enableFittedCarpets = override
          .comment("Replace carpet blocks, allowing them to fit to stairs below them.")
          .worldRestart()
          .define("fittedCarpets", true);

      // waterloggable hoppers
      waterlogHopper = override
          .comment("Replace hopper blocks, allowing them to be waterlogged.")
          .worldRestart()
          .define("waterlogHoppers", true);

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
      milkCooldown = server
          .comment("Adds a cooldown to milking cows, prevents practically infinite milk in modded worlds where milk is more useful.")
          .define("milkCooldown.enable", false);
      milkCooldownTime = server
          .comment("Delay in seconds after milking a cow before it can be milked again.")
          .defineInRange("milkCooldown.time", 600, 1, Short.MAX_VALUE);

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
          nerfCactusFarms = server
              .comment("If false, cactus seeds planted on cactus have fewer restrictions.",
                       "Setting to true means cactus seeds are broken by neighboring blocks, meaning classic cactus farms will drop cactus seeds instead of full cactus.")
              .define("nerfCactusFarms", false);
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
      betterCauldronItem = client
          .comment("Replaces the flat cauldron sprite with the 3D cauldron block model")
          .define("betterCauldronItemModel", true);

      // colored fireworks
      coloredFireworkItems = client
          .comment("Colors the fireworks item based on the colors of the stars")
          .worldRestart()
          .define("coloredFireworkItems", true);

      // colored enchanted book ribbons
      coloredEnchantedRibbons = client
          .comment("The ribbon on enchanted books colors based on the enchantment rarity")
          .worldRestart()
          .define("coloredEnchantedRibbons", true);

      // portal color
      customPortalColor = client
          .comment("Allows the portal color to be changed by placing colored blocks under the portal. Any block that tints a beacon beam will work for the color.")
          .worldRestart()
          .define("customPortalColor", true);
    }

    // build all specs
    SERVER_SPEC = server.build();
    CLIENT_SPEC = client.build();
    OVERRIDE_SPEC = override.build();
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
        InspirationsRegistry.setBookKeywords(
            Arrays.stream(Config.bookKeywords.get().split(","))
                  .map(String::trim)
                  .collect(Collectors.toList()));

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
