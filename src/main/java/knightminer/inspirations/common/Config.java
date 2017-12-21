package knightminer.inspirations.common;

import java.util.function.BooleanSupplier;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import slimeknights.mantle.client.CreativeTab;
import slimeknights.mantle.pulsar.config.ForgeCFG;

public class Config {

	public static ForgeCFG pulseConfig = new ForgeCFG("inspirationsModules", "Modules");

	private static Configuration configFile;


	// building
	public static boolean enableRope = true;
	public static boolean enableBookshelf = true;
	public static boolean enableColoredBooks = true;
	public static boolean showAllVariants = true;
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

	// tweaks
	public static boolean enablePigDesaddle = true;
	public static boolean enableFittedCarpets = true;

	public static boolean enableGlassDoor = true;



	/**
	 * Loads the configuration file from the event
	 * @param event  PreInit event from main mod class
	 */
	public static void load(FMLPreInitializationEvent event) {
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

			bookOverrides = configFile.getStringList("bookOverrides", "building.bookshelf", bookOverrides,
					"List of itemstacks to override book behavior. Format is modid:name[:meta[:isBook]]. Unset meta will default wildcard. Unset isBook will default true");
			processBookOverrides(bookOverrides);


			// rope
			enableRope = configFile.getBoolean("rope", "building", enableRope, "Enables rope, can be climbed like ladders and extended with additional rope");

			// glass door
			enableGlassDoor = configFile.getBoolean("glassDoor", "building", enableGlassDoor, "Enables glass doors and trapdoors. Basically doors, but made of glass. Not sure what you would expect.");
		}

		// utility
		{
			enableRedstoneBook = configFile.getBoolean("redstoneBook", "utility", enableRedstoneBook, "Enables the trapped book, which will emit redstone power when placed in a bookshelf. Requires bookshelf.") && enableBookshelf;

			// torch lever
			enableTorchLever = configFile.getBoolean("torchLever", "utility", enableTorchLever, "Enables the torch lever. Basically a lever which looks like a torch");

			// redstone charge
			enableRedstoneCharge = configFile.getBoolean("redstoneCharge", "utility", enableRedstoneCharge, "Enables the redstone charge, a quick pulse created with a flint and steel like item");

			// lock
			enableLock = configFile.getBoolean("lock", "utility", enableLock, "An item allowing you to lock a tile entity to only open for a special named item");

		}

		// tweaks
		{
			enablePigDesaddle = configFile.getBoolean("desaddlePig", "tweaks", enablePigDesaddle, "Allows pigs to be desaddled by shift-right click with an empty hand");
			enableFittedCarpets = configFile.getBoolean("fittedCarpets", "tweaks", enableFittedCarpets, "Carpets fit to stairs. Uses a block override, so disable if another mod replaces carpets");
		}

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
			if("".equals(override)) {
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
			if(item == null) {
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
				case "rope": return enableRope;

				// utility
				case "lock": return enableLock;
				case "redstone_book": return enableRedstoneBook;
				case "redstone_charge": return enableRedstoneCharge;
				case "torch_lever": return enableTorchLever;
			}

			throw new JsonSyntaxException("Invalid propertyname '" + property + "'");
		}
	}
}
