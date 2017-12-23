package knightminer.inspirations.common;

import java.util.function.BooleanSupplier;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import slimeknights.mantle.client.CreativeTab;
import slimeknights.mantle.pulsar.config.ForgeCFG;

public class Config {

	public static ForgeCFG pulseConfig = new ForgeCFG("inspirationsModules", "Modules");

	private static Configuration configFile;

	// general
	public static boolean showAllVariants = true;

	// building
	public static boolean enableRope = true;
	public static boolean enableGlassDoor = true;
	public static boolean enableMulch = true;
	public static boolean enablePath = true;

	public static boolean enableBookshelf = true;
	public static boolean enableColoredBooks = true;
	private static String[] bookKeywords = {
			"book",
			"guide",
			"manual"
	};
	private static String[] bookOverrides = new String[0];

	// utility
	public static boolean enableLock = true;
	public static boolean enableTorchLever = true;
	public static boolean enableRedstoneBook = true;
	public static boolean enableRedstoneCharge = true;
	public static boolean enableBricksButton = true;

	// tweaks
	public static boolean enablePigDesaddle = true;
	public static boolean enableFittedCarpets = true;
	public static boolean enableExtraBonemeal = true;
	public static boolean enableHeartbeet = true;
	public static boolean brewHeartbeet = true;
	public static boolean enableAnvilSmashing = true;
	public static boolean dispensersPlaceAnvils = true;
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


	/**
	 * Loads the configuration file from the event
	 * @param event  PreInit event from main mod class
	 */
	public static void preInit(FMLPreInitializationEvent event) {
		configFile = new Configuration(event.getSuggestedConfigurationFile(), "0.1", false);

		showAllVariants = configFile.getBoolean("showAllVariants", "general", showAllVariants,
				"Shows all variants for dynamically textured blocks, like bookshelves. If false just the first will be shown");

		// building
		{
			// bookshelves
			enableBookshelf = configFile.getBoolean("bookshelf", "building", enableBookshelf, "Enables the bookshelf, a decorative block to display books");
			enableColoredBooks = configFile.getBoolean("coloredBooks", "building.bookshelf", enableColoredBooks, "Enables colored books, basically colored versions of the vanilla book to decorate bookshelves") && enableBookshelf;
			bookKeywords = configFile.getStringList("bookKeywords", "building.bookshelf", bookKeywords,
					"List of keywords for valid books, used to determine valid books in the bookshelf");
			InspirationsRegistry.setBookKeywords(bookKeywords);


			// rope
			enableRope = configFile.getBoolean("rope", "building", enableRope, "Enables rope, can be climbed like ladders and extended with additional rope");

			// glass door
			enableGlassDoor = configFile.getBoolean("glassDoor", "building", enableGlassDoor, "Enables glass doors and trapdoors. Basically doors, but made of glass. Not sure what you would expect.");

			// mulch
			enableMulch = configFile.getBoolean("mulch", "building", enableMulch, "Enables mulch, a craftable falling block which supports plants such as flowers");

			// path
			enablePath = configFile.getBoolean("path", "building", enablePath, "Enables stone paths: a carpet like decorative block for making decorative paths");
		}

		// utility
		{
			enableRedstoneBook = configFile.getBoolean("redstoneBook", "utility", enableRedstoneBook, "Enables the trapped book, which will emit redstone power when placed in a bookshelf. Requires bookshelf.") && enableBookshelf;

			// torch lever
			enableTorchLever = configFile.getBoolean("torchLever", "utility", enableTorchLever, "Enables the torch lever. Basically a lever which looks like a torch");

			// redstone charge
			enableRedstoneCharge = configFile.getBoolean("redstoneCharge", "utility", enableRedstoneCharge, "Enables the redstone charge, a quick pulse created with a flint and steel like item");

			// lock
			enableLock = configFile.getBoolean("lock", "utility", enableLock, "Enables locks and keys, an item allowing you to lock a tile entity to only open for a special named item");

			// lock
			enableBricksButton = configFile.getBoolean("bricksButton", "utility", enableBricksButton, "Enables button blocks disguised as a full bricks or nether bricks block");
		}

		// tweaks
		{
			// pig desaddle
			enablePigDesaddle = configFile.getBoolean("desaddlePig", "tweaks", enablePigDesaddle, "Allows pigs to be desaddled by shift-right click with an empty hand");

			// fitted carpets
			enableFittedCarpets = configFile.getBoolean("fittedCarpets", "tweaks", enableFittedCarpets, "Carpets fit to stairs. Uses a block override, so disable if another mod replaces carpets");

			// bonemeal
			enableExtraBonemeal = configFile.getBoolean("extraBonemeal", "tweaks", enableExtraBonemeal, "Bonemeal can be used on mycelium to produce mushrooms and on sand to produce dead bushes");

			// heartroot
			enableHeartbeet = configFile.getBoolean("heartbeet", "tweaks", enableHeartbeet, "Enables heartbeets: a rare drop from beetroots which can be eaten to restore a bit of health");
			brewHeartbeet = configFile.getBoolean("brewRegeneration", "tweaks.heartbeet", brewHeartbeet, "Allows heartbeets to be used as an alternative to ghast tears in making potions of regeneration") && enableHeartbeet;

			// anvil smashing
			enableAnvilSmashing = configFile.getBoolean("anvilSmashing", "tweaks", enableAnvilSmashing, "Anvils break glass blocks and transform blocks into other blocks on landing. Uses a block override, so disable if another mod replaces anvils");

			// dispensers place anvils
			dispensersPlaceAnvils = configFile.getBoolean("dispensersPlaceAnvils", "tweaks", dispensersPlaceAnvils, "Dispensers will place anvils instead of dropping them. Plays well with anvil smashing.");
		}

		// saving
		if(configFile.hasChanged()) {
			configFile.save();
		}
	}

	/**
	 * Anything which we need access to block or item registries
	 * @param event
	 */
	public static void init(FMLInitializationEvent event) {
		// building
		bookOverrides = configFile.get("building.bookshelf", "bookOverrides", bookOverrides,
				"List of itemstacks to override book behavior. Format is modid:name[:meta[:isBook]]. Unset meta will default wildcard. Unset isBook will default true").getStringList();
		processBookOverrides(bookOverrides);

		// anvil smashing
		// skip the helper method so the defaults are not put in the comment
		anvilSmashing = configFile.get("tweaks.anvilSmashing", "smashing", anvilSmashing,
				"List of blocks to add to anvil smashing. Format is modid:input[:meta][->modid:output[:meta]]. If the output is excluded, it will default to air (breaking the block). If the meta is excluded, it will check all states for input and use the default for output").getStringList();
		processAnvilSmashing(anvilSmashing);

		// saving
		if(configFile.hasChanged()) {
			configFile.save();
		}
	}

	/**
	 * Parses the book overrides from the string array
	 * @param overrides  Input string array
	 */
	private static void processBookOverrides(String[] overrides) {
		NonNullList<ItemStack> stacks;
		String[] parts;
		Item item;
		int meta;
		boolean isBook;
		// simply look through each entry
		for(String override : overrides) {
			// skip blank lines
			if("".equals(override) || override.startsWith("#")) {
				continue;
			}

			// split by semicolons, valid keys are length of 2, 3, or 4
			parts = override.split(":");
			if(parts.length < 2 || parts.length > 4) {
				Inspirations.log.error("Invalid book override {}: must be in format modid:name[:meta[:isBook]]. ", override);
				continue;
			}

			// next parse meta. If unset default wildcard
			meta = OreDictionary.WILDCARD_VALUE;
			if(parts.length > 2) {
				// invalid numbers set -2 so we can handle negative too
				try {
					meta = Integer.parseInt(parts[2]);
				} catch(NumberFormatException e) {
					meta = -2;
				}

				// though -1 is wildcard, default behavior
				if(meta == -1) {
					meta = OreDictionary.WILDCARD_VALUE;
				} else if(meta < -1) {
					Inspirations.log.error("Invalid book override {}: invalid metadata", override);
					continue;
				}
			}

			// if the length and meta are valid, try finding the item
			item = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(parts[0], parts[1]));
			if(item == Items.AIR) {
				Inspirations.log.warn("Unable to find item {}:{} for {}", parts[0], parts[1], override);
				continue;
			}

			// finally, parse the isBook boolean. Pretty lazy here, just check if its not the string false
			isBook = parts.length > 3 ? !"false".equals(parts[3]) : true;

			// finally, add the entry
			if(meta == OreDictionary.WILDCARD_VALUE) {
				// wildcard iterates through stacks
				stacks = NonNullList.create();
				item.getSubItems(CreativeTab.SEARCH, stacks);
				for(ItemStack stack : stacks) {
					InspirationsRegistry.registerBook(stack, isBook);
				}
			} else {
				InspirationsRegistry.registerBook(item, meta, isBook);
			}
		}
	}

	/**
	 * Parses the anvil smashing array into the registry
	 * @param transformations  Input array
	 */
	@SuppressWarnings("deprecation")
	private static void processAnvilSmashing(String[] transformations) {
		main:
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

				// find blockstates for the input and output
				// loop so I am not doing this twice
				Block[] blocks = new Block[2];
				IBlockState[] states = new IBlockState[2];
				int meta;
				for(int i = 0; i < transformParts.length; i++) {
					// split into parts
					String transformPart = transformParts[i];
					String[] parts = transformPart.split(":");

					// should have name and ID with optional meta
					if(parts.length > 3 || parts.length < 2) {
						Inspirations.log.warn("Invalid anvil smashing {}: invalid parameter length for {}, expected modid:blockid[:meta]",
								transformation, transformPart);
						continue main;
					}

					// try parsing the metadata
					meta = -1;
					if(parts.length > 2) {
						try {
							meta = Integer.parseInt(parts[2]);
						} catch(NumberFormatException e) {
							meta = -1;
						}
						// handle invalid numbers and negatives here
						if(meta < 0) {
							Inspirations.log.error("Invalid anvil smashing {}: invalid metadata for {}", transformation, transformPart);
							continue main;
						}
					}

					// next, try finding the block
					blocks[i] = GameRegistry.findRegistry(Block.class).getValue(new ResourceLocation(parts[0], parts[1]));
					if(blocks[i] == Blocks.AIR) {
						Inspirations.log.warn("Unable to find block {}:{} for transformation {}", parts[0], parts[1], transformation);
						continue main;
					}

					// if we have meta, parse the blockstate
					if(meta > -1) {
						states[i] = blocks[i].getStateFromMeta(meta);
					}
				}
				// if the length is 1, this is block breaking, so use air for the output
				if(transformParts.length == 1) {
					blocks[1] = Blocks.AIR;
				}

				// if no result state, just grab the default state. That is all the registry does anyways
				if(states[1] == null) {
					states[1] = blocks[1].getDefaultState();
				}

				// determine whether to use block or blockstate parameter
				if(states[0] == null) {
					InspirationsRegistry.registerAnvilSmashing(blocks[0], states[1]);
				} else {
					InspirationsRegistry.registerAnvilSmashing(states[0], states[1]);
				}
			}
	}

	public static class PulseLoaded implements IConditionFactory {
		@Override
		public BooleanSupplier parse(JsonContext context, JsonObject json) {
			String pulse = JsonUtils.getString(json, "pulse");
			return () -> Inspirations.pulseManager.isPulseLoaded(pulse);
		}
	}

	public static class ConfigProperty implements IConditionFactory {
		@Override
		public BooleanSupplier parse(JsonContext context, JsonObject json) {
			String prop = JsonUtils.getString(json, "prop");
			return () -> propertyEnabled(prop);
		}

		private static boolean propertyEnabled(String property) {
			switch(property) {
				// building
				case "bookshelf": return enableBookshelf;
				case "colored_books": return enableColoredBooks;
				case "glass_door": return enableGlassDoor;
				case "mulch": return enableMulch;
				case "path": return enablePath;
				case "rope": return enableRope;

				// utility
				case "bricks_button": return enableBricksButton;
				case "lock": return enableLock;
				case "redstone_book": return enableRedstoneBook;
				case "redstone_charge": return enableRedstoneCharge;
				case "torch_lever": return enableTorchLever;
			}

			throw new JsonSyntaxException("Invalid propertyname '" + property + "'");
		}
	}
}
