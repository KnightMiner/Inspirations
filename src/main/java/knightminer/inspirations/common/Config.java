package knightminer.inspirations.common;

import net.minecraftforge.common.ForgeConfigSpec;
import slimeknights.mantle.pulsar.config.PulsarConfig;

import static net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import static net.minecraftforge.common.ForgeConfigSpec.Builder;
import static net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import static net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import static net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import static net.minecraftforge.common.ForgeConfigSpec.IntValue;

@SuppressWarnings("WeakerAccess")
public class Config {

	public static PulsarConfig pulseConfig = new PulsarConfig("inspirationsModules", "Modules");

	// TODO: add client config
	public static Builder BUILDER;
	public static ForgeConfigSpec SPEC;
	public static Builder BUILDER_OVERRIDE;
	public static ForgeConfigSpec SPEC_OVERRIDE;

	// general
	public static BooleanValue showAllVariants;

	// building
	public static BooleanValue enableRope;
	private static BooleanValue enableRopeLadder;
	public static boolean enableRopeLadder() { return enableRopeLadder.get() && enableRope.get(); }
	public static BooleanValue climbableIronBars;
	public static BooleanValue enableGlassDoor;
	public static BooleanValue enableMulch;
	public static BooleanValue enablePath;
	public static BooleanValue enableFlowers;
	public static BooleanValue enableEnlightenedBush;


	public static BooleanValue enableBookshelf;
	private static BooleanValue enableColoredBooks;
	public static BooleanValue bookshelvesBoostEnchanting;
	public static DoubleValue defaultEnchantingPower;
	public static boolean enableColoredBooks() {
		return enableColoredBooks.get() && enableBookshelf.get();
	}

	public static ConfigValue<String> bookKeywords;
	private static String bookKeywordsDefault = "almanac, atlas, book, catalogue, concordance, dictionary, directory, encyclopedia, guide, journal, lexicon, manual, thesaurus, tome";

	// utility
	public static BooleanValue enableTorchLever;
	private static BooleanValue enableRedstoneBook;
	public static BooleanValue enableBricksButton;
	public static BooleanValue enableCarpetedTrapdoor;
	public static BooleanValue enableCarpetedPressurePlate;
	public static BooleanValue enableCollector;
	public static BooleanValue enablePipe;
	public static BooleanValue pipeUpwards;
	//public static BooleanValue enableDispenserFluidTanks;
	//public static BooleanValue milkSquids;
	//public static IntValue milkSquidCooldown;

	public static boolean enableRedstoneBook() { return enableRedstoneBook.get() && enableBookshelf.get(); }

	// recipes

	// cauldron - extended
	private static BooleanValue replaceCauldron;
	private static BooleanValue enableCauldronRecipes;

	public static boolean enableCauldronRecipes() {
		return enableCauldronRecipes.get();
	}
	public static boolean enableExtendedCauldron() {
		return replaceCauldron.get() && replaceCauldron.get();
	}

	// cauldron - extended options
	private static BooleanValue enableBiggerCauldron;
	private static BooleanValue fasterCauldronRain;
	public static boolean enableBiggerCauldron() { return enableBiggerCauldron.get() && enableExtendedCauldron(); }
	public static boolean fasterCauldronRain() { return fasterCauldronRain.get() && enableExtendedCauldron(); }


	private enum SpongeEmptyCauldron {
		DISABLED, // No emptying.
		ANY, // For any amount of liquid.
		FULL  // Allowed, but only full cauldrons.
	}

	private static EnumValue<SpongeEmptyCauldron> spongeEmptyCauldron;

	public static boolean canSpongeEmptyCauldron() {
		return spongeEmptyCauldron.get() != SpongeEmptyCauldron.DISABLED;
	}
	public static boolean canSpongeEmptyFullOnly() {
		return spongeEmptyCauldron.get() == SpongeEmptyCauldron.FULL;
	}

	public static BooleanValue cauldronObsidian;

	public static BooleanValue dropCauldronContents;
	public static boolean dropCauldronContents() {
		return dropCauldronContents.get() && enableExtendedCauldron();
	}

	// cauldron - fluids
	private static BooleanValue enableCauldronFluids;
	private static BooleanValue enableMilk;

	public static boolean enableCauldronFluids() {
		return enableCauldronFluids.get() && enableExtendedCauldron();
	}
	public static boolean enableMilk() {
		return enableMilk.get() && enableExtendedCauldron();
	}

	// cauldron - dyeing
	private static BooleanValue enableCauldronDyeing;
	private static BooleanValue patchVanillaDyeRecipes;
	private static BooleanValue extraBottleRecipes;

	public static boolean enableCauldronDyeing() {
		return enableCauldronDyeing.get() && enableExtendedCauldron();
	}
	public static boolean patchVanillaDyeRecipes() {
		return patchVanillaDyeRecipes.get() && enableCauldronDyeing();
	}
	public static boolean extraBottleRecipes() {
		return extraBottleRecipes.get() && enableCauldronDyeing();
	}

	// cauldron - potions
	private static BooleanValue enableCauldronPotions;
	private static BooleanValue enableCauldronBrewing;
	private static BooleanValue expensiveCauldronBrewing;
	private static BooleanValue cauldronTipArrows;

	public static boolean enableCauldronPotions() {
		return enableCauldronPotions.get() && enableExtendedCauldron();
	}
	public static boolean enableCauldronBrewing() {
		return enableCauldronBrewing.get() && enableCauldronPotions();
	}
	public static boolean expensiveCauldronBrewing() {
		return expensiveCauldronBrewing.get() && enableCauldronPotions();
	}
	public static boolean cauldronTipArrows() {
		return cauldronTipArrows.get() && enableCauldronPotions();
	}

	// cauldron - fluid containers
	private static BooleanValue enableCauldronDispenser;
	public static boolean enableCauldronDispenser() {
		return enableCauldronDispenser.get() && enableCauldronPotions();
	}
	// anvil smashing
	public static BooleanValue enableAnvilSmashing;

	// tools
	public static BooleanValue enableLock;
	public static BooleanValue enableRedstoneCharger;
	public static BooleanValue enableChargedArrow;
	public static BooleanValue harvestHangingVines;
	public static BooleanValue shearsReclaimMelons;
	public static BooleanValue enableNorthCompass;
	//public static BooleanValue renameVanillaCompass;
	public static BooleanValue enableBarometer;
	public static BooleanValue enablePhotometer;

	// waypoint compass
	public static BooleanValue enableWaypointCompass;
	private static BooleanValue dyeWaypointCompass;
	private static BooleanValue craftWaypointCompass;
	private static BooleanValue copyWaypointCompass;
	public static BooleanValue waypointCompassAdvTooltip;
	public static BooleanValue waypointCompassCrossDimension;
	public static boolean dyeWaypointCompass () {
		return dyeWaypointCompass.get() && enableWaypointCompass.get();
	}
	public static boolean craftWaypointCompass () {
		return craftWaypointCompass.get() && enableWaypointCompass.get();
	}
	public static boolean copyWaypointCompass () {
		return copyWaypointCompass.get() && enableWaypointCompass.get();
	}

	// enchantments
	public static BooleanValue moreShieldEnchantments;
	private static BooleanValue shieldEnchantmentTable;
	public static boolean shieldEnchantmentTable() {
		return shieldEnchantmentTable.get() && moreShieldEnchantments.get();
	}
	public static BooleanValue fixShieldTooltip;
	public static BooleanValue axeWeaponEnchants;
	public static BooleanValue axeEnchantmentTable;

	// tweaks
	public static BooleanValue enablePigDesaddle;
	public static BooleanValue enableFittedCarpets;
	public static BooleanValue coloredEnchantedRibbons;
	public static BooleanValue coloredFireworkItems;
	public static BooleanValue lilypadBreakFall;
	public static BooleanValue betterCauldronItem;
	public static BooleanValue unstackableRecipeAlts;
	public static BooleanValue dispensersPlaceAnvils;
	public static BooleanValue milkCooldown;
	public static IntValue milkCooldownTime;
	public static BooleanValue customPortalColor;

	// heartbeet
	public static BooleanValue enableHeartbeet;
	private static BooleanValue brewHeartbeet;
	public static boolean brewHeartbeet() {
		return brewHeartbeet.get() && enableHeartbeet.get();
	}
	public static IntValue heartbeetChance;

	// seeds
	public static BooleanValue enableMoreSeeds;
//	private static BooleanValue addGrassDrops;
//	private static BooleanValue nerfCarrotPotatoDrops;
//	public static boolean addGrassDrops() {
//		return addGrassDrops.get() && enableMoreSeeds.get();
//	}
//	public static boolean nerfCarrotPotatoDrops() {
//		return nerfCarrotPotatoDrops.get() && enableMoreSeeds.get();
//	}
	// bonemeal
	public static BooleanValue bonemealMushrooms;
	public static BooleanValue bonemealDeadBush;
	public static BooleanValue bonemealGrassSpread;
	public static BooleanValue bonemealMyceliumSpread;

	public static BooleanValue caveSpiderDrops;
	public static BooleanValue skeletonSkull;

	static {
		BUILDER = new Builder();
		BUILDER_OVERRIDE = new Builder();
		configure(BUILDER, BUILDER_OVERRIDE);
		SPEC = BUILDER.build();
		SPEC_OVERRIDE = BUILDER_OVERRIDE.build();
	}

	private static void configure(Builder builder, Builder builder_override) {

		showAllVariants = builder
				.comment("Shows all variants for dynamically textured blocks, like bookshelves. If false just the first will be shown")
				.define("general.showAllVariants", true);


		builder.push("building");
		{
			// bookshelves
			enableBookshelf = builder
					.comment("Enables the bookshelf: a decorative block to display books")
					.worldRestart()
					.define("bookshelf.enable", true);
			enableColoredBooks = builder
					.comment("Enables colored books: basically colored versions of the vanilla book to decorate bookshelves")
					.worldRestart()
					.define("bookshelf.coloredBooks", true );

			bookshelvesBoostEnchanting = builder
					.comment( "If true, bookshelves will increase enchanting table power.")
					.define("bookshelf.boostEnchanting", true);
			defaultEnchantingPower = builder
					.comment("Default power for a book for enchanting, can be overridden in the book overrides.")
					.defineInRange("bookshelf.defaultEnchanting", 1.5f, 0.0f, 15.0f);

			bookKeywords = builder
					.comment("List of keywords for valid books, used to determine valid books in the bookshelf. Separate each by commas.")
					.define("bookshelf.bookKeywords", bookKeywordsDefault);


			// rope
			enableRope = builder
					.comment("Enables rope: can be climbed like ladders and extended with additional rope")
					.worldRestart()
					.define("rope", true);

			enableRopeLadder = builder
					.comment("Enables rope ladders: right click ropes with sticks to extend the hitbox")
					.define("ropeLadder", true);

			climbableIronBars = builder_override
					.comment("Makes iron bars climbale if a rope is below them.")
					.worldRestart()
					.define("rope.climbaleBars", true);

			// glass door
			enableGlassDoor = builder
					.comment("Enables glass doors and trapdoors: basically doors, but made of glass. Not sure what you would expect.")
					.worldRestart()
					.define("glassDoor", true);

			// mulch
			enableMulch = builder
					.comment("Enables mulch: a craftable falling block which supports plants such as flowers")
					.worldRestart()
					.define("mulch", true);

			// path
			enablePath = builder
					.comment("Enables stone paths: a carpet like decorative block for making decorative paths")
					.worldRestart()
					.define("path", true);

			// flowers
			enableFlowers = builder
					.comment("Enables additional flowers from breaking double flowers with shears.")
					.worldRestart()
					.define("flowers", true);

			// enlightenedBush
			enableEnlightenedBush = builder
					.comment( "Enables enlightened bushes: bushes with lights.")
					.worldRestart()
					.define("enlightenedBush", true);
		}
		builder.pop();

		builder.push("utility");
		{
			enableRedstoneBook = builder
					.comment("Enables the trapped book: will emit redstone power when placed in a bookshelf. Requires bookshelf.")
					.worldRestart()
					.define("redstoneBook", true);

			// torch lever
			enableTorchLever = builder
					.comment("Enables the torch lever: basically a lever which looks like a torch")
					.worldRestart()
					.define("torchLever", true);

			// bricks button
			/*
			enableBricksButton = builder
					.comment("Enables button blocks disguised as a full bricks or nether bricks block")
					.worldRestart()
					.define("bricksButton", true);
			 */

			// carpeted trapdoor
			enableCarpetedTrapdoor = builder
					.comment("Enables carpeted trapdoors: a trapdoor which appears to be a carpet when closed")
					.worldRestart()
					.define("carpetedTrapdoor", true);

			// carpeted pressure plate
			enableCarpetedPressurePlate = builder
					.comment("Allows placing a carpet on a stone pressure plate to hide it")
					.worldRestart()
					.define("carpetedPressurePlate", true);

			// collector
			enableCollector = builder
					.comment("Enables the collector: extracts items from inventories or the world similar to a hopper, but can face in all 6 directions and cannot place items in inventories")
					.worldRestart()
					.define("collector", true);

			// pipe
			enablePipe = builder
					.comment("Enables pipes: a more economical hopper that only outputs items, does not pull from inventories. Both cheaper and better for performance.")
					.worldRestart()
					.define("pipe.enable", true);
			pipeUpwards = builder
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
			enableRedstoneCharger = builder
					.comment("Enables the redstone charger: a quick pulse created with a flint and steel like item")
					.worldRestart()
					.define("redstoneCharger", true);

			enableChargedArrow = builder
					.comment("Enables the charged arrow: places a redstone pulse where it lands")
					.worldRestart()
					.define("chargedArrow", true);

			// lock
			enableLock = builder
					.comment("Enables locks and keys: an item allowing you to lock a tile entity to only open for a special named item")
					.worldRestart()
					.define("lock", true);

			// harvest hanging vines
			harvestHangingVines = builder
					.comment("When shearing vines, any supported vines will also be sheared instead of just broken")
					.define("shears.harvestHangingVines", true);

			// shears reclaim melons
			shearsReclaimMelons = builder
					.comment("Breaking a melon block with shears will always return 9 slices")
					.define("reclaimMelons", true);

			// compass
			enableNorthCompass = builder
					.comment("Enables the north compass: a cheaper compass that always points north. Intended to either allow packs to replace the compass or as an alternative for F3 navigation")
					.worldRestart()
					.define("northCompass.enable", true);
			/*
			renameVanillaCompass = builder
					.comment("Renames the vanilla compass to 'origin compass' to help clarify the difference between the two compasses.")
					.worldRestart()
					.define("northCompass.renameVanilla", true);
			 */

			// barometer
			enableBarometer = builder
					.comment("Enables the barometer: a tool to measure the player's height in world.")
					.worldRestart()
					.define("barometer",  true);

			// photometer
			enablePhotometer = builder
					.comment("Enables the photometer: a tool to measure light in world. Can be pointed at a block to measure the light level of that block.")
					.worldRestart()
					.define("photometer", true);

			// waypoint compass
			enableWaypointCompass = builder
					.comment("Enables the waypoint compass: a compass which points towards a full beacon.")
					.worldRestart()
					.define("waypointCompass.enable", true);
			dyeWaypointCompass = builder
					.comment("If true, waypoint compasses can be dyed all vanilla colors")
					.worldRestart()
					.define("waypointCompass.dye", true);
			craftWaypointCompass = builder
					.comment("If true, waypoint compasses can be crafted using iron and a blaze rod. If false, they are obtained by using a vanilla compass on a beacon.")
					.worldRestart()
					.define("waypointCompass.craft", true);
			waypointCompassAdvTooltip = builder
					.comment("If true, waypoint compasses show the position target in the advanced item tooltip. Disable for packs that disable coordinates.")
					.define("waypointCompass.advTooltip", true);
			waypointCompassCrossDimension = builder
					.comment("If true, waypoint compasses work across dimensions. The coordinates between the overworld and nether will be adjusted, allowing for portal syncing.")
					.define("waypointCompass.crossDimension", true);
			copyWaypointCompass = builder
					.comment("If true, you can copy the position of one waypoint compass to another in a crafting table, similarly to maps or compasses")
					.worldRestart()
					.define("waypointCompass.copy", true);

			// TODO: consider a way to allow the registry sub, but still have these props set by the server
			// enchantments
			moreShieldEnchantments = builder_override
					.comment("If true, shields can now be enchanted with enchantments such as protection, fire aspect, knockback, and thorns. This requires replacing these enchantments.")
					.worldRestart()
					.define("enchantments.moreShield", true);
			shieldEnchantmentTable = builder_override
					.comment("If true, shields can be enchanted in an enchantment table. Does not support modded shields as it requires a registry substitution")
					.worldRestart()
					.define("enchantments.shieldTable", true);
			fixShieldTooltip = builder
					.comment("If true, fixes the tooltip on shield items so it looks better with both patterns and enchantments")
					.worldRestart()
					.define("enchantments.fixShieldTooltip", true);

			axeWeaponEnchants = builder_override
					.comment("If true, axes will be able to be enchanted with weapon enchants such as looting, fire aspect, and knockback")
					.worldRestart()
					.define("enchantments.axeWeapon", true);
			axeEnchantmentTable = builder_override
					.comment("If true, axes can receive available weapon enchantments at the enchantment table")
					.worldRestart()
					.define("enchantments.axeTable", true);
		}
		builder.pop();

		builder.push("tweaks");
		{
			// pig desaddle
			enablePigDesaddle = builder
					.comment("Allows pigs to be desaddled by shift-right click with an empty hand")
					.define("desaddlePig", true);

			// fitted carpets
			enableFittedCarpets = builder_override
					.comment("Replace carpet blocks, allowing them to fit to stairs below them.")
					.worldRestart()
					.define("fittedCarpets", true);

			// bonemeal
			builder.push("bonemeal");
			bonemealMushrooms = builder
					.comment("Bonemeal can be used on mycelium to produce mushrooms")
					.define("mushrooms", true);
			bonemealDeadBush = builder
					.comment("Bonemeal can be used on sand to produce dead bushes")
					.define("deadBush", true);
			bonemealGrassSpread = builder
					.comment("Bonemeal can be used on dirt to produce grass if adjecent to grass")
					.define("grassSpread", true);
			bonemealMyceliumSpread = builder
					.comment("Bonemeal can be used on dirt to produce mycelium if adjecent to mycelium")
					.define("myceliumSpread", true);
			builder.pop();

			// heartroot
			enableHeartbeet = builder
					.comment("Enables heartbeets: a rare drop from beetroots which can be eaten to restore a bit of health")
					.worldRestart()
					.define("heartbeet.enable", true);
			brewHeartbeet = builder
					.comment("Allows heartbeets to be used as an alternative to ghast tears in making potions of regeneration")
					.worldRestart()
					.define("heartbeet.brewRegeneration", true);  // && enableHeartbeet;
			// TODO: move to loot tables
			heartbeetChance = builder
					.comment("Chance of a heartbeet to drop instead of a normal drop. Formula is two 1 in [chance] chances for it to drop each harvest")
					.defineInRange("heartbeet.chance", 75, 10, 1000);

			// dispensers place anvils
			dispensersPlaceAnvils = builder
					.comment("Dispensers will place anvils instead of dropping them. Plays well with anvil smashing.")
					.worldRestart()
					.define("dispensersPlaceAnvils", true);

			// better cauldron item
			betterCauldronItem = builder
					.comment("Replaces the flat cauldron sprite with the 3D cauldron block model")
					.worldRestart()
					.define("betterCauldronItemModel", true);

			// colored enchanted book ribbons
			coloredEnchantedRibbons = builder
					.comment("The ribbon on enchanted books colors based on the enchantment rarity")
					.worldRestart()
					.define("coloredEnchantedRibbons", true);

			// colored fireworks
			coloredFireworkItems = builder
					.comment("Colors the fireworks item based on the colors of the stars")
					.worldRestart()
					.define("coloredFireworkItems", true);

			// lilypad fall breaking
			lilypadBreakFall = builder
					.comment("Lily pads prevent fall damage, but break in the process")
					.define("lilypadBreakFall", true);

			// stackable alternative recipes
			unstackableRecipeAlts = builder
					.comment("Adds stackable recipes to some vanilla or Inspriations items that require unstackable items to craft")
					.worldRestart()
					.define("unstackableRecipeAlts", true);

			// seeds
			/*
			enableMoreSeeds = builder
					.comment("Adds seeds for additional vanilla plants, including cactus, sugar cane, carrots, and potatoes.")
					.worldRestart()
					.define("moreSeeds.enable", true);
			addGrassDrops = builder
					.comment("Makes carrot and potato seeds drop from grass")
					.define("moreSeeds.grassDrops", true);
			nerfCarrotPotatoDrops = builder
					.comment("Makes carrots and potatoes drop their respective seed if not fully grown")
					.define("moreSeeds.nerfCarrotPotatoDrops", true);
			 */

			// milk cooldown
			milkCooldown = builder
					.comment("Adds a cooldown to milking cows, prevents practically infinite milk in modded worlds where milk is more useful.")
					.define("milkCooldown.enable", false);
			milkCooldownTime = builder
					.comment("Delay in seconds after milking a cow before it can be milked again.")
					.defineInRange("milkCooldown.time", 600, 1, Short.MAX_VALUE);

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
			customPortalColor = builder
					.comment( "Allows the portal color to be changed by placing colored blocks under the portal. Any block that tints a beacon beam will work for the color.")
					.define("customPortalColor", true);

			// drops
			caveSpiderDrops = builder
					.comment("If true, cave spiders will rarely drop webs, giving them an advantage to farm over regular spiders")
					.define("caveSpiderWeb", true);
			skeletonSkull = builder
					.comment("If true, skeletons will rarely drop their skull for consistency with wither skeletons. Does not affect creeper or zombie heads.")
					.define("skeletonSkull", true);
		}
		builder.pop();
	}
}
