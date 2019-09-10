package knightminer.inspirations.common;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.util.RecipeUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import slimeknights.mantle.pulsar.config.PulsarConfig;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import static net.minecraftforge.common.ForgeConfigSpec.Builder;
import static net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import static net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import static net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import static net.minecraftforge.common.ForgeConfigSpec.IntValue;

@SuppressWarnings("WeakerAccess")
public class Config {

	public static PulsarConfig pulseConfig = new PulsarConfig("inspirationsModules", "Modules");

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
	public static BooleanValue enableGlassDoor;
	public static BooleanValue enableMulch;
	public static BooleanValue enablePath;
	public static BooleanValue enableFlowers;
	public static BooleanValue enableEnlightenedBush;


	public static BooleanValue enableBookshelf;
	private static BooleanValue enableColoredBooksRaw;
	public static BooleanValue bookshelvesBoostEnchanting;
	public static DoubleValue defaultEnchantingPower;
	public static boolean enableColoredBooks() {
		return enableColoredBooksRaw.get() && enableBookshelf.get();
	}

	public static ConfigValue<String> bookKeywords;
	private static String bookKeywordsDefault = "almanac, atlas, book, catalogue, concordance, dictionary, directory, encyclopedia, guide, journal, lexicon, manual, thesaurus, tome";
	public static ConfigValue<List<String>> bookOverrides;
	private static List<String> bookOverridesDefault = Arrays.asList(
			"defiledlands:book_wyrm_raw->false",
			"defiledlands:book_wyrm_cooked->false",
			"defiledlands:book_wyrm_scale->false",
			"defiledlands:book_wyrm_scale_golden->false",
			"defiledlands:book_wyrm_analyzer->false",
			"minecraft:enchanted_book->2.5",
			"quark:ancient_tome->3.0",
			"theoneprobe:probenote->1.0"
	);

	// utility
	public static BooleanValue enableTorchLever;
	public static BooleanValue enableRedstoneTorchLever;
	private static BooleanValue enableRedstoneBookRaw;
	public static BooleanValue enableBricksButton;
	public static BooleanValue enableRedstoneBarrel;
	public static BooleanValue enableCarpetedTrapdoor;
	public static BooleanValue enableCarpetedPressurePlate;
	public static BooleanValue enableCollector;
	public static BooleanValue enablePipe;
	public static BooleanValue pipeUpwards;
	public static BooleanValue enableDispenserFluidTanks;
	public static BooleanValue milkSquids;
	public static IntValue milkSquidCooldown;

	public static boolean enableRedstoneBook() { return enableRedstoneBookRaw.get() && enableBookshelf.get(); }

	// recipes

	// cauldron - extended
	private static BooleanValue replaceCauldron;
	private static BooleanValue enableCauldronRecipesRaw;

	public static boolean enableCauldronRecipes() {
		return enableCauldronRecipesRaw.get();
	}
	public static boolean enableExtendedCauldron() {
		return replaceCauldron.get() && replaceCauldron.get();
	}

	// cauldron - extended options
	private static BooleanValue enableBiggerCauldronRaw;
	private static BooleanValue fasterCauldronRainRaw;
	public static boolean enableBiggerCauldron() { return enableBiggerCauldronRaw.get() && enableExtendedCauldron(); }
	public static boolean fasterCauldronRain() { return fasterCauldronRainRaw.get() && enableExtendedCauldron(); }


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

	public static BooleanValue dropCauldronContentsRaw;
	public static boolean dropCauldronContents() {
		return dropCauldronContentsRaw.get() && enableExtendedCauldron();
	}

	// cauldron - fluids
	private static BooleanValue enableCauldronFluidsRaw;
	private static BooleanValue enableMilkRaw;

	public static boolean enableCauldronFluids() {
		return enableCauldronFluidsRaw.get() && enableExtendedCauldron();
	}
	public static boolean enableMilk() {
		return enableMilkRaw.get() && enableExtendedCauldron();
	}

	// cauldron - dyeing
	private static BooleanValue enableCauldronDyeingRaw;
	private static BooleanValue patchVanillaDyeRecipesRaw;
	private static BooleanValue extraBottleRecipesRaw;

	public static boolean enableCauldronDyeing() {
		return enableCauldronDyeingRaw.get() && enableExtendedCauldron();
	}
	public static boolean patchVanillaDyeRecipes() {
		return patchVanillaDyeRecipesRaw.get() && enableCauldronDyeing();
	}
	public static boolean extraBottleRecipes() {
		return extraBottleRecipesRaw.get() && enableCauldronDyeing();
	}

	// cauldron - potions
	private static BooleanValue enableCauldronPotionsRaw;
	private static BooleanValue enableCauldronBrewingRaw;
	private static BooleanValue expensiveCauldronBrewingRaw;
	private static BooleanValue cauldronTipArrowsRaw;

	public static boolean enableCauldronPotions() {
		return enableCauldronPotionsRaw.get() && enableExtendedCauldron();
	}
	public static boolean enableCauldronBrewing() {
		return enableCauldronBrewingRaw.get() && enableCauldronPotions();
	}
	public static boolean expensiveCauldronBrewing() {
		return expensiveCauldronBrewingRaw.get() && enableCauldronPotions();
	}
	public static boolean cauldronTipArrows() {
		return cauldronTipArrowsRaw.get() && enableCauldronPotions();
	}

	// cauldron - recipes
	private static String[] cauldronRecipes = {
			"minecraft:sticky_piston->minecraft:piston"
	};

	// cauldron - fluid containers
	private static BooleanValue enableCauldronDispenserRaw;
	public static boolean enableCauldronDispenser() {
		return enableCauldronDispenserRaw.get() && enableCauldronPotions();
	}
	// anvil smashing
	public static BooleanValue enableAnvilSmashing;
	private static String[] anvilSmashing = {
			"# Stone",
			"minecraft:stone:0->minecraft:cobblestone",
			"minecraft:stonebrick->minecraft:cobblestone",
			"minecraft:stonebrick:1->minecraft:mossy_cobblestone",
			"minecraft:cobblestone->minecraft:gravel",
			"minecraft:stone:2->minecraft:stone:1",
			"minecraft:stone:4->minecraft:stone:3",
			"minecraft:stone:6->minecraft:stone:5",

			"# Sandstone",
			"minecraft:sandstone->minecraft:sand:0",
			"minecraft:red_sandstone->minecraft:sand:1",

			"# Ice",
			"minecraft:packed_ice->minecraft:ice",
			"minecraft:ice",
			"minecraft:frosted_ice",

			"# Plants",
			"minecraft:brown_mushroom_block",
			"minecraft:red_mushroom_block",
			"minecraft:leaves",
			"minecraft:leaves2",
			"minecraft:melon_block",
			"minecraft:pumpkin",
			"minecraft:lit_pumpkin",

			"# Concrete",
			"minecraft:concrete:0->minecraft:concrete_powder:0",
			"minecraft:concrete:1->minecraft:concrete_powder:1",
			"minecraft:concrete:2->minecraft:concrete_powder:2",
			"minecraft:concrete:3->minecraft:concrete_powder:3",
			"minecraft:concrete:4->minecraft:concrete_powder:4",
			"minecraft:concrete:5->minecraft:concrete_powder:5",
			"minecraft:concrete:6->minecraft:concrete_powder:6",
			"minecraft:concrete:7->minecraft:concrete_powder:7",
			"minecraft:concrete:8->minecraft:concrete_powder:8",
			"minecraft:concrete:9->minecraft:concrete_powder:9",
			"minecraft:concrete:10->minecraft:concrete_powder:10",
			"minecraft:concrete:11->minecraft:concrete_powder:11",
			"minecraft:concrete:12->minecraft:concrete_powder:12",
			"minecraft:concrete:13->minecraft:concrete_powder:13",
			"minecraft:concrete:14->minecraft:concrete_powder:14",
			"minecraft:concrete:15->minecraft:concrete_powder:15",

			"# Misc",
			"minecraft:planks->inspirations:mulch:0",
			"minecraft:prismarine:1->minecraft:prismarine:0",
			"minecraft:end_bricks->minecraft:end_stone",
			"minecraft:monster_egg"
	};


	// tools
	public static BooleanValue enableLock;
	public static BooleanValue enableRedstoneCharge;
	public static BooleanValue enableChargedArrow;
	public static BooleanValue harvestHangingVines;
	public static BooleanValue shearsReclaimMelons;
	public static BooleanValue enableNorthCompass;
	public static BooleanValue renameVanillaCompass;
	public static BooleanValue enableBarometer;
	public static BooleanValue enablePhotometer;

	// waypoint compass
	public static BooleanValue enableWaypointCompass;
	private static BooleanValue dyeWaypointCompassRaw;
	private static BooleanValue craftWaypointCompassRaw;
	private static BooleanValue copyWaypointCompassRaw;
	public static BooleanValue waypointCompassAdvTooltip;
	public static BooleanValue waypointCompassCrossDimension;
	public static boolean dyeWaypointCompass () {
		return dyeWaypointCompassRaw.get() && enableWaypointCompass.get();
	}
	public static boolean craftWaypointCompass () {
		return craftWaypointCompassRaw.get() && enableWaypointCompass.get();
	}
	public static boolean copyWaypointCompass () {
		return copyWaypointCompassRaw.get() && enableWaypointCompass.get();
	}

	// enchantments
	public static BooleanValue moreShieldEnchantments;
	private static BooleanValue shieldEnchantmentTableRaw;
	public static boolean shieldEnchantmentTable() {
		return shieldEnchantmentTableRaw.get() && moreShieldEnchantments.get();
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
	private static BooleanValue brewHeartbeetRaw;
	public static boolean brewHeartbeet() {
		return brewHeartbeetRaw.get() && enableHeartbeet.get();
	}
	public static IntValue heartbeetChance;

	// seeds
	public static BooleanValue enableMoreSeeds;
	private static BooleanValue addGrassDropsRaw;
	private static BooleanValue nerfCarrotPotatoDropsRaw;
	public static boolean addGrassDrops() {
		return addGrassDropsRaw.get() && enableMoreSeeds.get();
	}
	public static boolean nerfCarrotPotatoDrops() {
		return nerfCarrotPotatoDropsRaw.get() && enableMoreSeeds.get();
	}
	// bonemeal
	public static BooleanValue bonemealMushrooms;
	public static BooleanValue bonemealDeadBush;
	public static BooleanValue bonemealGrassSpread;
	public static BooleanValue bonemealMyceliumSpread;

	public static BooleanValue caveSpiderDrops;
	public static BooleanValue skeletonSkull;

	private static String[] milkContainersDefault = {
			"ceramics:clay_bucket",
			"minecraft:bowl", // mushroom stew from mooshrooms
			"minecraft:bucket",
			"simplytea:teapot"
	};
	public static Set<Item> milkContainers;

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
					.define("bookshelf.enable", true);
			enableColoredBooksRaw = builder
					.comment("Enables colored books: basically colored versions of the vanilla book to decorate bookshelves")
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
			enableRedstoneBookRaw = builder
					.comment("Enables the trapped book: will emit redstone power when placed in a bookshelf. Requires bookshelf.")
					.worldRestart()
					.define("redstoneBook", true);

			// torch lever
			enableTorchLever = builder
					.comment("Enables the torch lever: basically a lever which looks like a torch")
					.worldRestart()
					.define("torchLever", true);

			// bricks button
			enableBricksButton = builder
					.comment("Enables button blocks disguised as a full bricks or nether bricks block")
					.worldRestart()
					.define("bricksButton", true);

			// redstone barrel
			enableRedstoneBarrel = builder
					.comment("Enables the redstone barrel: a block that gives a configurable comparator output and can be pushed by pistons")
					.worldRestart()
					.define("redstoneBarrel", true);

			// redstone torch lever
			enableRedstoneTorchLever = builder
					.comment("Enables the redstone torch lever: a lever that toggles its state when the block it's on gets powered")
					.worldRestart()
					.define("redstoneTorchLever", true);

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
					.worldRestart()
					.define("pipe.upwards", true);

			// dispenser fluid containers
			enableDispenserFluidTanks = builder
					.comment("Allows dispensers to fill and empty fluid tanks using fluid containers")
					.worldRestart()
					.define("dispenserFluidTanks", true);
		}
		builder.pop();

		// recipes
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
			enableCauldronRecipesRaw = builder
					.comment("Allows additional recipes to be performed in the cauldron. If the block replacement is disabled, functionality will be limited to water in cauldrons.")
					.define("extendCauldron", true);

			builder.push("cauldron");
			{

				enableBiggerCauldronRaw = builder
						.comment("Makes the cauldron hold 4 bottle per bucket instead of 3. Translates better to modded fluids.")
						.worldRestart()
						.define("bigger", false);

				fasterCauldronRainRaw = builder
						.comment("Cauldrons fill faster in the rain than vanilla painfully slow rate.")
						.define("fasterRain", true);
				dropCauldronContentsRaw = builder
						.comment("Cauldrons will drop their contents when broken.")
						.define("dropContents", true);


				cauldronObsidian = builder
						.comment("Allows making obsidian in a cauldron by using a lava bucket on a water filled cauldron. Supports modded buckets. If cauldron fluids is enabled, you can also use a water bucket on a lava filled cauldron.")
						.define("obsidian", true);

				// fluids
				enableCauldronFluidsRaw = builder
						.comment("Allows cauldrons to be filled with any fluid and use them in recipes")
						.define("fluids.enable", true);
				enableMilkRaw = builder
						.comment("Registers milk as a fluid so it can be used in cauldron recipes.")
						.define("fluids.milk", true);

				// dyeing
				enableCauldronDyeingRaw = builder
						.comment("Allows cauldrons to be filled with dyes and dye items using cauldrons")
						.define("dyeing.enable", true);
				patchVanillaDyeRecipesRaw = builder
						.comment("Makes crafting two dyed water bottles together produce a dyed water bottle. Requires modifying vanilla recipes to prevent a conflict")
						.define("dyeing.patchVanillaRecipes", true);
				extraBottleRecipesRaw = builder
						.comment("Adds extra dyed bottle recipes to craft green and brown")
						.define("dyeing.extraBottleRecipes", true);

				// potions
				enableCauldronPotionsRaw = builder
						.comment("Allows cauldrons to be filled with potions and support brewing")
						.define("potions.enable", true);
				enableCauldronBrewingRaw = builder
						.comment("Allows cauldrons to perform brewing recipes.")
						.define("potions.brewing", true);
				expensiveCauldronBrewingRaw = builder
						.comment("Caps brewing at 2 potions per ingredient, requiring 2 ingredients for a full cauldron. Makes the brewing stand still useful and balances better against the bigger cauldron.")
						.define("potions.brewingExpensive", true);
				cauldronTipArrowsRaw = builder
						.comment("Allows cauldrons to tip arrows with potions.")
						.define("potions.tippedArrow", true);

				// dispensers
				enableCauldronDispenserRaw = builder
						.comment("Allows dispensers to perform some recipes in the cauldron. Intended to be used for recipes to fill and empty fluid containers as droppers can already be used for recipes")
						.define("dispenser", true);
			}
			builder.pop();
		}
		builder.pop();

		builder.push("tools");
		{
			// redstone charge
			enableRedstoneCharge = builder
					.comment("Enables the redstone charger: a quick pulse created with a flint and steel like item")
					.worldRestart()
					.define("redstoneCharge", true);

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
			renameVanillaCompass = builder
					.comment("Renames the vanilla compass to 'origin compass' to help clarify the difference between the two compasses.")
					.worldRestart()
					.define("northCompass.renameVanilla", true);

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
			dyeWaypointCompassRaw = builder
					.comment("If true, waypoint compasses can be dyed all vanilla colors")
					.define("waypointCompass.dye", true);
			craftWaypointCompassRaw = builder
					.comment("If true, waypoint compasses can be crafted using iron and a blaze rod. If false, they are obtained by using a vanilla compass on a beacon.")
					.define("waypointCompass.craft", true);
			waypointCompassAdvTooltip = builder
					.comment("If true, waypoint compasses show the position target in the advanced item tooltip. Disable for packs that disable coordinates.")
					.define("waypointCompass.advTooltip", true);
			waypointCompassCrossDimension = builder
					.comment("If true, waypoint compasses work across dimensions. The coordinates between the overworld and nether will be adjusted, allowing for portal syncing.")
					.define("waypointCompass.crossDimension", true);
			copyWaypointCompassRaw = builder
					.comment("If true, you can copy the position of one waypoint compass to another in a crafting table, similarly to maps or compasses")
					.define("waypointCompass.copy", true);

			// enchantments
			moreShieldEnchantments = builder_override
					.comment("If true, shields can now be enchanted with enchantments such as protection, fire aspect, knockback, and thorns. This requires replacing these enchantments.")
					.define("enchantments.moreShield", true);
			shieldEnchantmentTableRaw = builder_override
					.comment("If true, shields can be enchanted in an enchantment table. Does not support modded shields as it requires a registry substitution")
					.define("enchantments.shieldTable", true);
			fixShieldTooltip = builder
					.comment("If true, fixes the tooltip on shield items so it looks better with both patterns and enchantments")
					.define("fixShieldTooltip", true);

			axeWeaponEnchants = builder_override
					.comment("If true, axes will be able to be enchanted with weapon enchants such as looting, fire aspect, and knockback")
					.define("enchantments.axeWeapon", true);
			axeEnchantmentTable = builder_override
					.comment("If true, axes can receive available weapon enchantments at the enchantment table")
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
					.define("heartbeet.enable", true);
			brewHeartbeetRaw = builder
					.comment("Allows heartbeets to be used as an alternative to ghast tears in making potions of regeneration")
					.define("heartbeet.brewRegeneration", true);  // && enableHeartbeet;
			heartbeetChance = builder
					.comment("Chance of a heartbeet to drop instead of a normal drop. Formula is two 1 in [chance] chances for it to drop each harvest")
					.defineInRange("heartbeet.chance", 75, 10, 1000);

			// dispensers place anvils
			dispensersPlaceAnvils = builder
					.comment("Dispensers will place anvils instead of dropping them. Plays well with anvil smashing.")
					.define("dispensersPlaceAnvils", true);

			// better cauldron item
			betterCauldronItem = builder
					.comment("Replaces the flat cauldron sprite with the 3D cauldron block model")
					.define("betterCauldronItemModel", true);

			// colored enchanted book ribbons
			coloredEnchantedRibbons = builder
					.comment("The ribbon on enchanted books colors based on the enchantment rarity")
					.worldRestart()
					.define("coloredEnchantedRibbons", true);

			// colored fireworks
			coloredFireworkItems = builder
					.comment("Colors the fireworks item based on the colors of the stars")
					.define("coloredFireworkItems", true);

			// lilypad fall breaking
			lilypadBreakFall = builder
					.comment("Lily pads prevent fall damage, but break in the process")
					.define("lilypadBreakFall", true);

			// stackable alternative recipes
			unstackableRecipeAlts = builder
					.comment("Adds stackable recipes to some vanilla or Inspriations items that require unstackable items to craft")
					.define("unstackableRecipeAlts", true);

			// seeds
			enableMoreSeeds = builder
					.comment("Adds seeds for additional vanilla plants, including cactus, sugar cane, carrots, and potatoes.")
					.worldRestart()
					.define("moreSeeds.enable", true);
			addGrassDropsRaw = builder
					.comment("Makes carrot and potato seeds drop from grass")
					.define("moreSeeds.grassDrops", true);
			nerfCarrotPotatoDropsRaw = builder
					.comment("Makes carrots and potatoes drop their respective seed if not fully grown")
					.define("moreSeeds.nerfCarrotPotatoDrops", true);

			// milk cooldown
			milkCooldown = builder
					.comment("Adds a cooldown to milking cows, prevents practically infinite milk in modded worlds where milk is more useful.")
					.define("milkCooldown.enable", false);
			milkCooldownTime = builder
					.comment("Delay in seconds after milking a cow before it can be milked again.")
					.defineInRange("milkCooldown.time", 600, 1, Short.MAX_VALUE);
			customPortalColor = builder
					.comment( "Allows the portal color to be changed by placing colored blocks under the portal. Any block that tints a beacon beam will work for the color.")
					.define("customPortalColor", true);
		}
		builder.pop();

		// milk squids
		milkSquids = builder
				.comment("Allows milking squids with a glass bottle to get black dyed water.")
				.define("tweaks.milkSquids.enable", true);
		milkSquidCooldown = builder
				.comment("Delay in seconds after milking a squid before it can be milked again.")
				.defineInRange("tweaks.milkSquids.cooldown", 300, 1, Short.MAX_VALUE);


		// drops
		caveSpiderDrops = builder
				.comment("If true, cave spiders will rarely drop webs, giving them an advantage to farm over regular spiders")
				.define("caveSpiderWeb", true);
		skeletonSkull = builder
				.comment("If true, skeletons will rarely drop their skull for consistency with wither skeletons. Does not affect creeper or zombie heads.")
				.define("skeletonSkull", true);

		// building
//		Property property = configFile.get("building.bookshelf", "bookOverrides", bookOverrides,
//				"List of itemstacks to override book behavior. Format is modid:name[:meta][->enchantingPower].\nUnset meta will default wildcard.\n0 is a valid enchanting power, if unset uses default. Set to 'false' to mark something as not a book.");
//		bookOverrides = property.getStringList();
//		processBookOverrides(bookOverrides);

		// anvil smashing
		// skip the helper method so the defaults are not put in the comment
//		configFile.moveProperty("tweaks.anvilSmashing", "recipes.anvilSmashing", "smashing");
//		anvilSmashing = configFile.get("recipes.anvilSmashing", "smashing", anvilSmashing,
//				"List of blocks to add to anvil smashing. Format is modid:input[:meta][->modid:output[:meta]]. If the output is excluded, it will default to air (breaking the block). If the meta is excluded, it will check all states for input and use the default for output").getStringList();
//		processAnvilSmashing(anvilSmashing);

		// cauldron uses
//		configFile.moveProperty("recipes.cauldronRecipes", "recipes", "recipes.cauldron");
//		cauldronRecipes = configFile.get("recipes.cauldron", "recipes", cauldronRecipes,
//				"List of recipes to add to the cauldron on right click. Format is (modid:input:meta|oreString)->modid:output:meta[->isBoiling]. If isBoiling is excluded, it defaults to false.").getStringList();
//		processCauldronRecipes(cauldronRecipes);

	}

	/**
	 * Updates a config file from a bunch of colons to the new -> format
	 * @param line  Old line
	 * @param t  True string
	 * @param f  False string
	 * @return  New line
	 */
	private static String updateConfig(String line, String t, String f) {
		String[] parts = line.split(":");
		switch(parts.length) {
			// 'modid:name' -> 'modid:name->1.5
			case 2:
				return line + "->" + t;
				// 'modid:name:meta' -> 'modid:name:meta->7
			case 3:
				// if meta -1, remove as wildcard is just none now
				if(parts[2].equals("-1")) {
					return String.format("%s:%s->%s", parts[0], parts[1], t);
				}
				return line + "->" + t;
			case 4:
				// first, determine power
				String power = "false".equals(parts[3]) ? f : t;
				// if meta -1, remove as wildcard is just none now
				if(parts[2].equals("-1")) {
					return String.format("%s:%s->%s", parts[0], parts[1], power);
				}
				return String.format("%s:%s:%s->%s", parts[0], parts[1], parts[2], power);
		}

		return line;
	}

	/**
	 * Parses the book overrides from the string array
	 * @param overrides  Input string array
	 */
	private static void processBookOverrides(String[] overrides) {
		if(!enableBookshelf.get()) {
			return;
		}

		String[] parts;
		// simply look through each entry
		for(String override : overrides) {
			// skip blank lines
			if("".equals(override) || override.startsWith("#")) {
				continue;
			}

			parts = override.split("->");
			if(parts.length > 2) {
				Inspirations.log.error("Invalid book override {}: must be in format modid:name[:meta][->power]. ", override);
				continue;
			}

			// finally, parse the isBook boolean. Pretty lazy here, just check if its not the string false
			float power = defaultEnchantingPower.get().floatValue();
			if (parts.length > 1) {
				try {
					power = Float.parseFloat(parts[1]);
				} catch(NumberFormatException e) {
					if (parts[1].equals("false")) {
						power = -1;
					} else {
						Inspirations.log.error("Invalid book override {}: power must be a number. ", override);
						continue;
					}
				}
			}
			// normalize not a book
			if (power < 0) {
				power = -1;
			}
			final float enchPower = power;
			RecipeUtil.forStackInString(parts[0], stack -> InspirationsRegistry.registerBook(stack.getItem(), enchPower));
		}
	}

	/**
	 * Parses the anvil smashing array into the registry
	 * @param transformations  Input array
	 */
	private static void processAnvilSmashing(String[] transformations) {
		if(!enableAnvilSmashing.get()) {
			return;
		}

		for(String transformation : transformations) {
			// skip blank lines
			if("".equals(transformation) || transformation.startsWith("#")) {
				continue;
			}

			// first, ensure we have the right number of inputs
			// it should be 1 for plain old smashing or two for a transformation
			String[] transformParts = transformation.split("->");
			if(transformParts.length > 2 || transformParts.length < 1) {
				Inspirations.log.error("Invalid anvil smashing {}: must be in the format of modid:input[:meta][->modid:output[:meta]]", transformation);
				continue;
			}

			// if the length is 1, this is block breaking, so use air for the output
			BlockState output;
			if(transformParts.length == 1) {
				output = Blocks.AIR.getDefaultState();
			} else {
				output = RecipeUtil.getBlockStateFromString(transformParts[1]);
				if (output == null) {
					Inspirations.log.info("Skipping anvil smashing {}: unable to find output {}", transformation, transformParts[1]);
					continue;
				}
			}

			RecipeUtil.forBlockInString(transformParts[0],
					state -> InspirationsRegistry.registerAnvilSmashing(state, output),
					block -> InspirationsRegistry.registerAnvilSmashing(block, output));
		}
	}

	/**
	 * Processes the simple cauldron recipes from the config
	 * @param cauldronRecipes  List of recipe strings
	 */
	private static void processCauldronRecipes(String[] cauldronRecipes) {
		if(!enableCauldronRecipes()) {
			return;
		}

		for(String recipe : cauldronRecipes) {
			// skip blank lines
			if("".equals(recipe) || recipe.startsWith("#")) {
				continue;
			}

			String[] parts = recipe.split("->");
			if(parts.length < 2 || parts.length > 3) {
				Inspirations.log.error("Invalid cauldron recipe {}: must be in format input->output[->isBoiling]", recipe);
				continue;
			}

			// input
			ItemStack input = null;
			if(parts[0].contains(":")) {
				input = RecipeUtil.getItemStackFromString(parts[0], true);
				if(input.isEmpty()) {
					continue;
				}
			}

			// output
			ItemStack output = RecipeUtil.getItemStackFromString(parts[1], false);
			if(output.isEmpty()) {
				continue;
			}

			// add recipe
			Boolean boiling = parts.length > 2 ? parts[2].equals("true") : null;
			// if the input is empty, we are using an oreString
			if(input == null) {
//				InspirationsRegistry.addCauldronRecipe(parts[0], output, boiling);
			} else {
//				InspirationsRegistry.addCauldronRecipe(input, output, boiling);
			}
		}
	}
}
