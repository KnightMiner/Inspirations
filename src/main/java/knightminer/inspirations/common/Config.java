package knightminer.inspirations.common;

import java.util.function.BooleanSupplier;

import com.google.gson.JsonObject;

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


	// bookshelf
	public static boolean showAllVariants = true;
	private static String[] bookKeywords = {
			"book",
			"guide",
			"manual"
	};
	private static String[] bookOverrides = new String[0];

	/**
	 * Loads the configuration file from the event
	 * @param event  PreInit event from main mod class
	 */
	public static void load(FMLPreInitializationEvent event) {
		configFile = new Configuration(event.getSuggestedConfigurationFile(), "0.1", false);

		// bookshelves
		showAllVariants = configFile.getBoolean("showAllVariants", "general", showAllVariants,
				"Shows all variants for dynamically textured blocks, like bookshelves. If false just the first will be shown");

		bookKeywords = configFile.getStringList("bookKeywords", "bookshelf", bookKeywords,
				"List of keywords for valid books, used to determine valid books in the bookshelf");
		InspirationsRegistry.setBookKeywords(bookKeywords);

		bookOverrides = configFile.getStringList("bookOverrides", "bookshelf", bookOverrides,
				"List of itemstacks to override book behavior. Format is modid:name[:meta[:isBook]]. Unset meta will default wildcard. Unset isBook will default true");
		processBookOverrides(bookOverrides);

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
}
