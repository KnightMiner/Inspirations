package knightminer.inspirations.common;

import com.electronwill.nightconfig.core.EnumGetMethod;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.util.RecipeUtil;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.model.multipart.ICondition;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.crafting.IConditionSerializer;
import net.minecraftforge.common.util.JsonUtils;
import net.minecraftforge.fml.loading.FMLConfig;
import slimeknights.mantle.pulsar.config.PulsarConfig;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static net.minecraftforge.common.ForgeConfigSpec.*;

@SuppressWarnings("WeakerAccess")
public class Config {

	public static PulsarConfig pulseConfig = new PulsarConfig("inspirationsModules", "Modules");

	public static Builder BUILDER;
	public static ForgeConfigSpec SPEC;
	static {
		BUILDER = new Builder();
		configure(BUILDER);
		SPEC = BUILDER.build();
	}

	// general
	public static BooleanValue showAllVariants;
	public static BooleanValue witherBoneDrop;

	// building
	public static BooleanValue enableRope;
	public static BooleanValue enableGlassDoor;
	public static BooleanValue enableMulch;
	public static BooleanValue enablePath;
	public static BooleanValue enableFlowers;
	public static BooleanValue enableEnlightenedBush;

	public static BooleanValue enableBookshelf;
	public static BooleanValue enableColoredBooks;
	public static BooleanValue bookshelvesBoostEnchanting;
	public static DoubleValue defaultEnchantingPower;

	public static ConfigValue<List<String>> bookKeywords;
	private static List<String> bookKeywordsDefault = Arrays.asList(
			"almanac",
			"atlas",
			"book",
			"catalogue",
			"concordance",
			"dictionary",
			"directory",
			"encyclopedia",
			"guide",
			"journal",
			"lexicon",
			"manual",
			"thesaurus",
			"tome"
	);
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
	public static BooleanValue enableRedstoneBook;
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
	public static String[] fluidContainers = {
			"ceramics:clay_bucket",
			"forge:bucketfilled",
			"minecraft:bucket",
			"minecraft:water_bucket",
			"minecraft:milk_bucket",
			"minecraft:lava_bucket"
	};

	// recipes
	// cauldron - extended
	public static BooleanValue enableCauldronRecipes;
	public static BooleanValue enableExtendedCauldron;
	public static BooleanValue simpleCauldronRecipes;
	// cauldron - extended options
	public static BooleanValue enableBiggerCauldron;
	public static BooleanValue fasterCauldronRain;

	private enum SpongeEmptyCauldron {
		FALSE, // No emptying.
		TRUE, // For any amount of liquid.
		FULL  // Allowed, but only full cauldrons.
	}

	private static EnumValue<SpongeEmptyCauldron> spongeEmptyCauldron;

	public static boolean canSpongeEmptyCauldron() {
		return spongeEmptyCauldron.get() != SpongeEmptyCauldron.FALSE;
	}
	public static boolean canSpongeEmptyFullOnly() {
		return spongeEmptyCauldron.get() == SpongeEmptyCauldron.FULL;
	}

	public static BooleanValue cauldronObsidian;

	public static BooleanValue dropCauldronContents;
	// cauldron - fluids
	public static BooleanValue enableCauldronFluids;
	public static BooleanValue enableMilk;
	// cauldron - dyeing
	public static BooleanValue enableCauldronDyeing;
	public static BooleanValue patchVanillaDyeRecipes;
	public static BooleanValue extraBottleRecipes;
	// cauldron - potions
	public static BooleanValue enableCauldronPotions;
	public static BooleanValue enableCauldronBrewing;
	private static boolean expensiveCauldronBrewing = true;
	public static BooleanValue cauldronTipArrows;
	// cauldron - recipes
	private static String[] cauldronRecipes = {
			"minecraft:sticky_piston->minecraft:piston"
	};
	private static String[] cauldronFire = {
			"minecraft:fire"
	};
	// cauldron - fluid containers
	public static BooleanValue enableCauldronDispenser;
	public static String[] cauldronDispenserRecipes = {
			"inspirations:dyed_bottle",
			"inspirations:materials:2",
			"inspirations:materials:3",
			"minecraft:beetroot_soup",
			"minecraft:bowl",
			"minecraft:glass_bottle",
			"minecraft:lingering_potion",
			"minecraft:mushroom_stew",
			"minecraft:potion",
			"minecraft:rabbit_stew",
			"minecraft:splash_potion",
			"toughasnails:fruit_juice",
			"toughasnails:purified_water_bottle"
	};
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
	// crook
	public static BooleanValue enableCrook;
	public static BooleanValue separateCrook;
	public static boolean hoeCrook = false;
	public static int crookChance = 10;
	public static BooleanValue netherCrooks;
	// waypoint compass
	public static BooleanValue enableWaypointCompass;
	public static BooleanValue dyeWaypointCompass;
	public static BooleanValue craftWaypointCompass;
	public static BooleanValue copyWaypointCompass;
	public static BooleanValue waypointCompassAdvTooltip;
	public static BooleanValue waypointCompassCrossDimension;
	// enchantments
	public static BooleanValue moreShieldEnchantments;
	public static BooleanValue shieldEnchantmentTable;
	public static BooleanValue axeWeaponEnchants;
	public static BooleanValue axeEnchantmentTable;

	// tweaks
	public static BooleanValue enablePigDesaddle;
	public static BooleanValue enableFittedCarpets;
	public static BooleanValue betterFlowerPot;
	public static BooleanValue flowerPotComparator;
	public static BooleanValue coloredEnchantedRibbons;
	public static BooleanValue brewMissingPotions;
	public static BooleanValue coloredFireworkItems;
	public static BooleanValue lilypadBreakFall;
	public static BooleanValue betterCauldronItem;
	public static BooleanValue unstackableRecipeAlts;
	public static BooleanValue dispensersPlaceAnvils;
	public static boolean milkCooldown = false;
	public static short milkCooldownTime = 600;
	// heartbeet
	public static BooleanValue enableHeartbeet;
	public static BooleanValue brewHeartbeet;
	public static int heartbeetChance = 75;
	// seeds
	public static BooleanValue enableMoreSeeds;
	public static BooleanValue addGrassDrops;
	public static BooleanValue nerfCarrotPotatoDrops;
	// bonemeal
	public static BooleanValue bonemealMushrooms;
	public static BooleanValue bonemealDeadBush;
	public static BooleanValue bonemealGrassSpread;
	public static BooleanValue bonemealMyceliumSpread;

	public static String[] flowerOverrides = {
			"biomesoplenty:flower_0->7",
			"biomesoplenty:flower_0->7",
			"biomesoplenty:mushroom->1",
			"biomesoplenty:sapling_0->12",
			"biomesoplenty:sapling_1->12",
			"biomesoplenty:sapling_2->12"
	};
	private static String[] milkContainersDefault = {
			"ceramics:clay_bucket",
			"minecraft:bowl", // mushroom stew from mooshrooms
			"minecraft:bucket",
			"simplytea:teapot"
	};
	public static Set<Item> milkContainers;

	// compatibility
	public static BooleanValue tanJuiceInCauldron;


	private static void configure(Builder builder) {

		showAllVariants = builder
				.comment("Shows all variants for dynamically textured blocks, like bookshelves. If false just the first will be shown")
				.define("general.showAllVariants", true);

		witherBoneDrop = builder
				.comment("Enables the wither bone drop. Option here in case another mod adds this. Requires either nether crooks or extra potions to be enabled")
				.define("general.witherBoneDrop", true);


		builder.push("building");
		{
			// bookshelves
			enableBookshelf = builder
					.comment("Enables the bookshelf: a decorative block to display books")
					.worldRestart()
					.define("bookshelf", true);
			enableColoredBooks = builder
					.comment("Enables colored books: basically colored versions of the vanilla book to decorate bookshelves")
					.worldRestart()
					.define("bookshelf.coloredBooks", true );
			// Todo: force colored books when shelf is disabled.

			bookshelvesBoostEnchanting = builder
					.comment( "If true, bookshelves will increase enchanting table power.")
					.define("bookshelf.boostEnchanting", true);
			defaultEnchantingPower = builder
					.comment("Default power for a book for enchanting, can be overridden in the book overrides.")
					.defineInRange("bookshelf.defaultEnchanting", 1.5f, 0.0f, 15.0f);
			bookKeywords = builder
					.comment("List of keywords for valid books, used to determine valid books in the bookshelf")
					.defineList("bookshelf.bookKeywords",  bookKeywordsDefault, (Object x) -> x instanceof String);


			// rope
			enableRope = builder
					.comment("Enables rope: can be climbed like ladders and extended with additional rope")
					.worldRestart()
					.define("rope", true);

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
			enableCarpetedTrapdoor = configFile.getBoolean("carpetedTrapdoor", "utility", enableCarpetedTrapdoor, "Enables carpeted trapdoors: a trapdoor which appears to be a carpet when closed");

			// carpeted pressure plate
			enableCarpetedPressurePlate = configFile.getBoolean("carpetedPressurePlate", "utility", enableCarpetedPressurePlate, "Allows placing a carpet on a stone pressure plate to hide it");

			// collector
			enableCollector = configFile.getBoolean("collector", "utility", enableCollector, "Enables the collector: extracts items from inventories or the world similar to a hopper, but can face in all 6 directions and cannot place items in inventories");

			// pipe
			enablePipe = configFile.getBoolean("pipe", "utility", enablePipe, "Enables pipes: a more economical hopper that only outputs items, does not pull from inventories. Both cheaper and better for performance.");
			pipeUpwards = configFile.getBoolean("upwards", "utility.pipe", pipeUpwards, "Allows pipes to output upwards. This removes a limitation on not being able to pipe items up without dropper elevators, but should be balanced alongside modded pipes.");

			// dispenser fluid containers
			enableDispenserFluidTanks = configFile.getBoolean("dispenserFluidTanks", "utility", enableDispenserFluidTanks, "Allows dispensers to fill and empty fluid tanks using fluid containers");
			fluidContainers = configFile.get("utility.dispenserFluidTanks", "containers", fluidContainers,
					"List of itemstacks that can be used as fluid containers to fill or empty fluid tanks").getStringList();
		}
		builder.pop();

		// recipes
		builder.push("recipes");
		{
			// anvil smashing
			// configFile.moveProperty("tweaks", "anvilSmashing", "recipes");
			enableAnvilSmashing = builder
					.comment("Anvils break glass blocks and transform blocks into other blocks on landing. Uses a block override, so disable if another mod replaces anvils")
					.worldRestart()
					.define("anvilSmashing", true);

			// cauldron //

			// basic config
			spongeEmptyCauldron = builder
					.comment("Allows sponges to be used to empty the cauldron of dye, water, or potions. Can be 'true', 'false', or 'full'. If set to 'full', requires the cauldron to be full, prevents duplicating water but is less useful for removing unwanted fluids.")
					.defineEnum("spongeEmpty", SpongeEmptyCauldron.TRUE);

			// extended options
			String extendCauldron = configFile.getString("extendCauldron", "recipes", "true", "Allows additional recipes to be performed in the cauldron. Can be 'true', 'false', or 'simple'. If true, requires a block substitution. If simple, functionality will be limited to water in cauldrons.", new String[]{"false", "simple", "true"});
			enableCauldronRecipes = !extendCauldron.equals("false");
			simpleCauldronRecipes = extendCauldron.equals("simple");
			enableExtendedCauldron = extendCauldron.equals("true");

			enableBiggerCauldron = configFile.getBoolean("bigger", "recipes.cauldron", enableBiggerCauldron, "Makes the cauldron hold 4 bottle per bucket instead of 3. Translates better to modded fluids.") && enableExtendedCauldron;
			InspirationsRegistry.setConfig("biggerCauldron", enableBiggerCauldron);
			fasterCauldronRain = configFile.getBoolean("fasterRain", "recipes.cauldron", fasterCauldronRain, "Cauldrons fill faster in the rain than vanilla painfully slow rate.") && enableExtendedCauldron;
			dropCauldronContents = configFile.getBoolean("dropContents", "recipes.cauldron", dropCauldronContents, "Cauldrons will drop their contents when broken.") && enableExtendedCauldron;

			cauldronObsidian = configFile.getBoolean("obsidian", "recipes.cauldron", cauldronObsidian, "Allows making obsidian in a cauldron by using a lava bucket on a water filled cauldron. Supports modded buckets. If cauldron fluids is enabled, you can also use a water bucket on a lava filled cauldron.");

			// fluids
			enableCauldronFluids = configFile.getBoolean("fluids", "recipes.cauldron", enableCauldronFluids, "Allows cauldrons to be filled with any fluid and use them in recipes") && enableExtendedCauldron;
			configFile.moveProperty("recipes.cauldron", "milk", "recipes.cauldron.fluids");
			enableMilk = configFile.getBoolean("milk", "recipes.cauldron.fluids", enableMilk, "Registers milk as a fluid so it can be used in cauldron recipes.") && enableCauldronFluids;

			// dyeing
			enableCauldronDyeing = configFile.getBoolean("dyeing", "recipes.cauldron", enableCauldronDyeing, "Allows cauldrons to be filled with dyes and dye items using cauldrons") && enableExtendedCauldron;
			patchVanillaDyeRecipes = configFile.getBoolean("patchVanillaRecipes", "recipes.cauldron.dyeing", patchVanillaDyeRecipes, "Makes crafting two dyed water bottles together produce a dyed water bottle. Requires modifying vanilla recipes to prevent a conflict") && enableCauldronDyeing;
			extraBottleRecipes = configFile.getBoolean("extraBottleRecipes", "recipes.cauldron.dyeing", extraBottleRecipes, "Adds extra dyed bottle recipes to craft green and brown") && enableCauldronDyeing;

			// potions
			configFile.renameProperty("recipes.cauldron", "brewing", "potions");
			enableCauldronPotions = configFile.getBoolean("potions", "recipes.cauldron", enableCauldronPotions, "Allows cauldrons to be filled with potions and support brewing") && enableExtendedCauldron;
			enableCauldronBrewing = configFile.getBoolean("brewing", "recipes.cauldron.potions", enableCauldronBrewing, "Allows cauldrons to perform brewing recipes.") && enableCauldronPotions;
			expensiveCauldronBrewing = configFile.getBoolean("brewingExpensive", "recipes.cauldron.potions", expensiveCauldronBrewing, "Caps brewing at 2 potions per ingredient, requiring 2 ingredients for a full cauldron. Makes the brewing stand still useful and balances better against the bigger cauldron.") && enableCauldronBrewing;
			cauldronTipArrows = configFile.getBoolean("tippedArrow", "recipes.cauldron.potions", cauldronTipArrows, "Allows cauldrons to tip arrows with potions.") && enableCauldronPotions;
			InspirationsRegistry.setConfig("expensiveCauldronBrewing", expensiveCauldronBrewing);

			// dispensers
			enableCauldronDispenser = configFile.getBoolean("dispenser", "recipes.cauldron", enableCauldronDispenser, "Allows dispensers to perform some recipes in the cauldron. Intended to be used for recipes to fill and empty fluid containers as droppers can already be used for recipes") && enableCauldronRecipes;
			cauldronDispenserRecipes = configFile.get("recipes.cauldron.dispenser", "items", cauldronDispenserRecipes,
					"List of itemstacks that can be used as to perform cauldron recipes in a dispenser").getStringList();
		}
		builder.pop();

		builder.push("tools");
		{
			// redstone charge
			configFile.moveProperty("utility", "redstoneCharge", "tools");
			enableRedstoneCharge = configFile.getBoolean("redstoneCharge", "tools", enableRedstoneCharge, "Enables the redstone charger: a quick pulse created with a flint and steel like item");
			enableChargedArrow = configFile.getBoolean("chargedArrow", "tools", enableChargedArrow, "Enables the charged arrow: places a redstone pulse where it lands");

			// lock
			configFile.moveProperty("utility", "lock", "tools");
			enableLock = configFile.getBoolean("lock", "tools", enableLock, "Enables locks and keys: an item allowing you to lock a tile entity to only open for a special named item");

			// crooks
			String crookType = configFile.getString("crook", "tools", "true", "Enables the crook: a tool to break leaves faster and increase sapling chance. Can be 'true', 'false', or 'simple'. If true, adds a new tool. If simple, functionality will be added to hoes instead.", new String[]{ "false", "simple", "true" });
			enableCrook = !crookType.equals("false");
			separateCrook = crookType.equals("true");
			hoeCrook = crookType.equals("simple");
			crookChance = configFile.getInt("chance", "tools.crook", crookChance, 1, 100, "Chance of a sapling to drop when using the crook. Acts as 1 in [chance] if the initial sapling drop fails. Set to 1 to always drop saplings when using a crook.");
			netherCrooks = configFile.getBoolean("netherCrooks", "tools.crook", netherCrooks, "Enables crooks crafted from blaze rods and wither bones. They have higher stats than other crooks and inflict fire and wither on the target respectively.") && separateCrook;

			// harvest hanging vines
			configFile.moveProperty("tweaks", "harvestHangingVines", "tools.shears");
			harvestHangingVines = configFile.getBoolean("harvestHangingVines", "tools.shears", harvestHangingVines, "When shearing vines, any supported vines will also be sheared instead of just broken");

			// shears reclaim melons
			configFile.moveProperty("tweaks", "shearsReclaimMelons", "tools.shears");
			configFile.renameProperty("tools.shears", "shearsReclaimMelons", "reclaimMelons");
			shearsReclaimMelons = configFile.getBoolean("reclaimMelons", "tools.shears", shearsReclaimMelons, "Breaking a melon block with shears will always return 9 slices");

			// compass
			enableNorthCompass = configFile.getBoolean("northCompass", "tools", enableNorthCompass, "Enables the north compass: a cheaper compass that always points north. Intended to either allow packs to replace the compass or as an alternative for F3 navigation");
			renameVanillaCompass = configFile.getBoolean("renameVanilla", "tools.northCompass", renameVanillaCompass, "Renames the vanilla compass to 'origin compass' to help clarify the difference between the two compasses.");

			// barometer
			enableBarometer = configFile.getBoolean("barometer", "tools", enableBarometer, "Enables the barometer: a tool to measure the player's height in world.");

			// photometer
			enablePhotometer = configFile.getBoolean("photometer", "tools", enablePhotometer, "Enables the photometer: a tool to measure light in world. Can be pointed at a block to measure the light level of that block.");

			// photometer
			enableWaypointCompass = configFile.getBoolean("waypointCompass", "tools", enableWaypointCompass, "Enables the waypoint compass: a compass which points towards a full beacon.");
			dyeWaypointCompass = configFile.getBoolean("dye", "tools.waypointCompass", dyeWaypointCompass, "If true, waypoint compasses can be dyed all vanilla colors") && enableWaypointCompass;
			craftWaypointCompass = configFile.getBoolean("craft", "tools.waypointCompass", craftWaypointCompass, "If true, waypoint compasses can be crafted using iron and a blaze rod. If false, they are obtained by using a vanilla compass on a beacon.") && enableWaypointCompass;
			waypointCompassAdvTooltip = configFile.getBoolean("advTooltip", "tools.waypointCompass", waypointCompassAdvTooltip, "If true, waypoint compasses show the position target in the advanced item tooltip. Disable for packs that disable coordinates.");
			waypointCompassCrossDimension = configFile.getBoolean("crossDimension", "tools.waypointCompass", waypointCompassCrossDimension, "If true, waypoint compasses work across dimensions. The coordinates between the overworld and nether will be adjusted, allowing for portal syncing.");
			copyWaypointCompass = configFile.getBoolean("copy", "tools.waypointCompass", copyWaypointCompass, "If true, you can copy the position of one waypoint compass to another in a crafting table, similarly to maps or compasses") && enableWaypointCompass;

			// enchantments
			moreShieldEnchantments = configFile.getBoolean("moreShield", "tools.enchantments", moreShieldEnchantments, "If true, shields can now be enchanted with enchantments such as protection, fire aspect, knockback, and thorns");
			shieldEnchantmentTable = configFile.getBoolean("shieldTable", "tools.enchantments", shieldEnchantmentTable, "If true, shields can be enchanted in an enchantment table. Does not support modded shields as it requires a registry substitution") && moreShieldEnchantments;
			axeWeaponEnchants = configFile.getBoolean("axeWeapon", "tools.enchantments", axeWeaponEnchants, "If true, axes will be able to be enchanted with weapon enchants such as looting, fire aspect, and knockback");
			axeEnchantmentTable = configFile.getBoolean("axeTable", "tools.enchantments", axeEnchantmentTable, "If true, axes can receive available weapon enchantments at the enchantment table");
		}
		builder.pop();

		builder.push("tweaks");
		{
			// pig desaddle
			enablePigDesaddle = configFile.getBoolean("desaddlePig", "tweaks", enablePigDesaddle, "Allows pigs to be desaddled by shift-right click with an empty hand");

			// fitted carpets
			enableFittedCarpets = configFile.getBoolean("fittedCarpets", "tweaks", enableFittedCarpets, "Carpets fit to stairs. Uses a block override, so disable if another mod replaces carpets");

			// bonemeal
			if (getConfigVersion() < 0.4) {
				boolean oldValue = configFile.get("tweaks", "extraBonemeal", true).getBoolean();
				bonemealMushrooms = oldValue;
				bonemealDeadBush = oldValue;
			}
			bonemealMushrooms = configFile.getBoolean("mushrooms", "tweaks.bonemeal", bonemealMushrooms, "Bonemeal can be used on mycelium to produce mushrooms");
			bonemealDeadBush = configFile.getBoolean("deadBush", "tweaks.bonemeal", bonemealDeadBush, "Bonemeal can be used on sand to produce dead bushes");
			bonemealGrassSpread = configFile.getBoolean("grassSpread", "tweaks.bonemeal", bonemealGrassSpread, "Bonemeal can be used on dirt to produce grass if adjecent to grass");
			bonemealMyceliumSpread = configFile.getBoolean("myceliumSpread", "tweaks.bonemeal", bonemealMyceliumSpread, "Bonemeal can be used on dirt to produce mycelium if adjecent to mycelium");

			// heartroot
			enableHeartbeet = configFile.getBoolean("heartbeet", "tweaks", enableHeartbeet, "Enables heartbeets: a rare drop from beetroots which can be eaten to restore a bit of health");
			brewHeartbeet = configFile.getBoolean("brewRegeneration", "tweaks.heartbeet", brewHeartbeet, "Allows heartbeets to be used as an alternative to ghast tears in making potions of regeneration") && enableHeartbeet;
			heartbeetChance = configFile.getInt("chance", "tweaks.heartbeet", heartbeetChance, 10, 1000, "Chance of a heartbeet to drop instead of a normal drop. Formula is two 1 in [chance] chances for it to drop each harvest");

			// dispensers place anvils
			dispensersPlaceAnvils = configFile.getBoolean("dispensersPlaceAnvils", "tweaks", dispensersPlaceAnvils, "Dispensers will place anvils instead of dropping them. Plays well with anvil smashing.");

			// better cauldron item
			betterCauldronItem = configFile.getBoolean("betterCauldronItemModel", "tweaks", betterCauldronItem, "Replaces the flat cauldron sprite with the 3D cauldron block model");

			// better flower pots
			betterFlowerPot = configFile.getBoolean("betterFlowerPot", "tweaks", betterFlowerPot, "Flower pots can hold modded flowers");
			flowerPotComparator = configFile.getBoolean("comparator", "tweaks.betterFlowerPot", flowerPotComparator, "Flower pots will emit a comparator signal if they have a flower");

			// colored enchanted book ribbons
			coloredEnchantedRibbons = configFile.getBoolean("coloredEnchantedRibbons", "tweaks", coloredEnchantedRibbons, "The ribbon on enchanted books colors based on the enchantment rarity");

			// more potions
			brewMissingPotions = configFile.getBoolean("brewMissingPotions", "tweaks", brewMissingPotions, "Adds brewing recipes for vanilla potions which are missing a recipe");

			// colored fireworks
			coloredFireworkItems = configFile.getBoolean("coloredFireworkItems", "tweaks", coloredFireworkItems, "Colors the fireworks item based on the colors of the stars");

			// lilypad fall breaking
			lilypadBreakFall = configFile.getBoolean("lilypadBreakFall", "tweaks", lilypadBreakFall, "Lily pads prevent fall damage, but break in the process");

			// stackable alternative recipes
			unstackableRecipeAlts = configFile.getBoolean("unstackableRecipeAlts", "tweaks", unstackableRecipeAlts, "Adds stackable recipes to some vanilla or Inspriations items that require unstackable items to craft");

			// seeds
			enableMoreSeeds = configFile.getBoolean("moreSeeds", "tweaks", enableMoreSeeds, "Adds seeds for additional vanilla plants, including cactus, sugar cane, carrots, and potatoes.");
			addGrassDrops = configFile.getBoolean("grassDrops", "tweaks.moreSeeds", addGrassDrops, "Makes carrot and potato seeds drop from grass") && enableMoreSeeds;
			nerfCarrotPotatoDrops = configFile.getBoolean("nerfCarrotPotatoDrops", "tweaks.moreSeeds", nerfCarrotPotatoDrops, "Makes carrots and potatoes drop their respective seed if not fully grown") && enableMoreSeeds;

			// milk cooldown
			milkCooldown = configFile.getBoolean("milkCooldown", "tweaks", milkCooldown, "Adds a cooldown to milking cows, prevents practically infinite milk in modded worlds where milk is more useful.");
			milkCooldownTime = (short)configFile.getInt("time", "tweaks.milkCooldown", milkCooldownTime, 1, Short.MAX_VALUE, "Delay in seconds after milking a cow before it can be milked again.");

		}
		builder.pop();

		// milk squids
		milkSquids = builder
				.comment("Allows milking squids with a glass bottle to get black dyed water.")
				.define("tweaks.milkSquids", true);
		milkSquidCooldown = builder
				.comment("Delay in seconds after milking a squid before it can be milked again.")
				.defineInRange("tweaks.milkSquids.cooldown", 300, 1, Short.MAX_VALUE);

		// compatibility
		builder.push("compatibility");
		{
			// TAN Plugin: make juice in cauldron
			tanJuiceInCauldron = builder
					.comment("Enables making Tough as Nails juices in the cauldron. Requires enhanced cauldron")
					.define("tanJuiceInCauldron", true);
		}
		builder.pop();
	}

	/**
	 * Anything which we need access to block or item registries
	 * @param event
	 */
	public static void init(FMLInitializationEvent event) {
		double version = getConfigVersion();

		// building
		Property property = configFile.get("building.bookshelf", "bookOverrides", bookOverrides,
				"List of itemstacks to override book behavior. Format is modid:name[:meta][->enchantingPower].\nUnset meta will default wildcard.\n0 is a valid enchanting power, if unset uses default. Set to 'false' to mark something as not a book.");
		bookOverrides = property.getStringList();
		// if before config version 0.3, update to new format and add enchanted book in
		if(version < 0.3) {
			bookOverrides = Stream.concat(Arrays.stream(bookOverrides).map(line -> updateConfig(line, "1.5", "false")), Stream.of("minecraft:enchanted_book->2.5", "quark:ancient_tome->3.0")).toArray(String[]::new);
			property.set(bookOverrides);
		}
		processBookOverrides(bookOverrides);

		// anvil smashing
		// skip the helper method so the defaults are not put in the comment
		configFile.moveProperty("tweaks.anvilSmashing", "recipes.anvilSmashing", "smashing");
		anvilSmashing = configFile.get("recipes.anvilSmashing", "smashing", anvilSmashing,
				"List of blocks to add to anvil smashing. Format is modid:input[:meta][->modid:output[:meta]]. If the output is excluded, it will default to air (breaking the block). If the meta is excluded, it will check all states for input and use the default for output").getStringList();
		processAnvilSmashing(anvilSmashing);

		// cauldron uses
		configFile.moveProperty("recipes.cauldronRecipes", "recipes", "recipes.cauldron");
		cauldronRecipes = configFile.get("recipes.cauldron", "recipes", cauldronRecipes,
				"List of recipes to add to the cauldron on right click. Format is (modid:input:meta|oreString)->modid:output:meta[->isBoiling]. If isBoiling is excluded, it defaults to false.").getStringList();
		processCauldronRecipes(cauldronRecipes);

		// flowers
		property = configFile.get("tweaks.betterFlowerPot", "flowerOverrides", flowerOverrides,
				"List of itemstacks to override default flower behavior, default checks for BlockBush.\n"
						+ "Format is 'modid:name[:meta]->power'. Unset meta will default wildcard. Power refers to comparator power, non-zero makes it valid for a flower pot. Specific values:\n"
						+ "* 0 - not flower, blacklists from placing in a flower pot\n* 1 - mushroom\n* 4 - fern\n* 7 - flower\n* 10 - dead bush\n* 12 - sapling\n* 15 - cactus");
		flowerOverrides = property.getStringList();
		// if loaded in 0.1, update to 0.2 format
		if(version < 0.2) {
			flowerOverrides = Arrays.stream(flowerOverrides).map(line -> updateConfig(line, "7", "0")).toArray(String[]::new);
			property.set(flowerOverrides);
		}
		processFlowerOverrides(flowerOverrides);

		// cauldron fires
		cauldronFire = configFile.get("recipes.cauldron", "fires", cauldronFire,
				"List of blocks to act is fire below a cauldron. Format is modid:name[:meta]. If meta is excluded all states of the block will count as fire").getStringList();
		processCauldronFire(cauldronFire);

		// cauldron fires
		milkContainersDefault = configFile.get("tweaks.milkCooldown", "containers", milkContainersDefault,
				"List of containers which will milk a cow when interacting. Used to prevent milking and to apply the milked tag").getStringList();
		processMilkContainers(milkContainersDefault);

		// saving
		if(configFile.hasChanged()) {
			configFile.save();
		}
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
	 * Safely gets the config version as a double
	 * @return Config version
	 */
	private static double getConfigVersion() {
		try {
			return Double.parseDouble(configFile.getLoadedConfigVersion());
		} catch (NumberFormatException e) {
			return Double.NaN;
		}
	}

	/**
	 * Parses the book overrides from the string array
	 * @param overrides  Input string array
	 */
	private static void processBookOverrides(String[] overrides) {
		if(!enableBookshelf) {
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
			float power = defaultEnchantingPower;
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
			RecipeUtil.forStackInString(parts[0], stack -> InspirationsRegistry.registerBook(stack, enchPower));
		}
	}

	/**
	 * Processes the flower string array into the registry
	 * @param overrides  Overrides to process
	 */
	private static void processFlowerOverrides(String[] overrides) {
		if(!Config.betterFlowerPot) {
			return;
		}
		for(String line : overrides) {
			String[] split = line.split("->");
			if(split.length != 2) {
				Inspirations.log.error("Invalid flower pot override, expected format 'modid:name[:meta]->power'");
				continue;
			}

			// parse comparator power
			int power;
			try {
				power = Integer.parseInt(split[1]);
			} catch(NumberFormatException e) {
				Inspirations.log.error("Invalid flower pot power, must be a valid number");
				continue;
			}
			if(power < 0 || power > 15) {
				Inspirations.log.error("Invalid flower pot power, must between 0 to 15");
				continue;
			}

			// find item
			RecipeUtil.forStackInString(split[0], stack -> InspirationsRegistry.registerFlower(stack, power));
		}
	}

	/**
	 * Parses the anvil smashing array into the registry
	 * @param transformations  Input array
	 */
	private static void processAnvilSmashing(String[] transformations) {
		if(!enableAnvilSmashing) {
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
			IBlockState output;
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
		if(!enableCauldronRecipes) {
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
				InspirationsRegistry.addCauldronRecipe(parts[0], output, boiling);
			} else {
				InspirationsRegistry.addCauldronRecipe(input, output, boiling);
			}
		}
	}


	/**
	 * Parses the cauldron fire list from the config
	 * @param fires  List of fire blocks or block states
	 */
	private static void processCauldronFire(String[] fires) {
		if(!enableCauldronRecipes.get()) {
			return;
		}

		for(String fire : fires) {
			// skip blank lines and comments
			if("".equals(fire) || fire.startsWith("#")) {
				continue;
			}

			RecipeUtil.forBlockInString(fire, InspirationsRegistry::registerCauldronFire, InspirationsRegistry::registerCauldronFire);
		}
	}

	/**
	 * Parses the milk containers list into a ImmutableSet
	 * @param containers
	 */
	private static void processMilkContainers(String[] containers) {
		if(!milkCooldown) {
			return;
		}

		ImmutableSet.Builder<Item> builder = ImmutableSet.builder();
		Consumer<ItemStack> callback = (stack) -> {
			builder.add(stack.getItem());
		};
		for(String container : containers) {
			// skip blank lines and comments
			if("".equals(container) || container.startsWith("#")) {
				continue;
			}
			RecipeUtil.forStackInString(container, callback);
		}
		milkContainers = builder.build();
	}

	/*
	 * Factories for recipe conditions
	 */

	public static class PulseLoaded implements IConditionSerializer {
		@Override
		public BooleanSupplier parse(JsonObject json) {
			String pulse = JSONUtils.getString(json, "pulse");
			return () -> Inspirations.pulseManager.isPulseLoaded(pulse);
		}
	}

	public static class ConfigProperty implements IConditionSerializer {
		@Override
		public BooleanSupplier parse(JsonObject json) {
			String prop = JSONUtils.getString(json, "prop");
			return () -> propertyEnabled(prop);
		}

		private static boolean propertyEnabled(String property) {
			switch(property) {
				// building
				case "bookshelf": return enableBookshelf.get();
				case "colored_books": return enableColoredBooks.get();
				case "enlightened_bush": return enableEnlightenedBush.get();
				case "flowers": return enableFlowers.get();
				case "glass_door": return enableGlassDoor.get();
				case "mulch": return enableMulch.get();
				case "path": return enablePath.get();
				case "rope": return enableRope.get();

				// utility
				case "bricks_button": return enableBricksButton.get();
				case "carpeted_trapdoor": return enableCarpetedTrapdoor.get();
				case "collector": return enableCollector.get();
				case "pipe": return enablePipe.get();
				case "redstone_barrel": return enableRedstoneBarrel.get();
				case "redstone_book": return enableRedstoneBook.get();
				case "redstone_torch_lever": return enableRedstoneTorchLever.get();
				case "torch_lever": return enableTorchLever.get();

				// tools
				case "barometer": return enableBarometer.get();
				case "charged_arrow": return enableChargedArrow.get();
				case "craft_waypoint_compass": return craftWaypointCompass.get();
				case "crook": return separateCrook.get();
				case "dye_waypoint_compass": return dyeWaypointCompass.get();
				case "lock": return enableLock.get();
				case "nether_crook": return netherCrooks.get();
				case "north_compass": return enableNorthCompass.get();
				case "photometer": return enablePhotometer.get();
				case "redstone_charge": return enableRedstoneCharge.get();

				// tweaks
				case "more_seeds": return enableMoreSeeds.get();
				case "unstackable_alts": return unstackableRecipeAlts.get();

				// recipes
				case "cauldron_dyeing": return enableCauldronDyeing.get();
				case "cauldron_fluids": return enableCauldronFluids.get();
				case "cauldron_potions": return enableCauldronPotions.get();
				case "extra_dyed_bottle_recipes": return extraBottleRecipes.get();
				case "patch_vanilla_dye_recipes": return patchVanillaDyeRecipes.get();
			}

			throw new JsonSyntaxException("Invalid propertyname '" + property + "'");
		}
	}
}
