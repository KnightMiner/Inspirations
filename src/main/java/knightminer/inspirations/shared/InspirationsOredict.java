package knightminer.inspirations.shared;

import javax.annotation.Nonnull;

import com.google.common.eventbus.Subscribe;

import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = InspirationsShared.pulseID, description = "Blocks and items used by all modules", forced = true)
public class InspirationsOredict {

	private static final String[] DYES = { // makes oredict to int a bit easier in a couple other places
			"White",
			"Orange",
			"Magenta",
			"LightBlue",
			"Yellow",
			"Lime",
			"Pink",
			"Gray",
			"LightGray",
			"Cyan",
			"Purple",
			"Blue",
			"Brown",
			"Green",
			"Red",
			"Black"
	};

	@Subscribe
	public void doTheOredict(FMLInitializationEvent event) {
		ensureVanilla();
		registerBuilding();
		registerRecipes();
		registerShared();
		registerUtility();
	}

	private void ensureVanilla() {
		oredict(Items.BOOK, "book");
	}

	private void registerShared() {
		oredict(InspirationsShared.witherBone, "boneWithered");
		oredict(InspirationsShared.stoneRod, "rodStone");
	}

	private static void registerBuilding() {
		oredict(InspirationsBuilding.books, "book");
	}

	private static void registerRecipes() {
		for(EnumDyeColor color : EnumDyeColor.values()) {
			oredict(InspirationsRecipes.dyedWaterBottle, color.getDyeDamage(), dyeNameFor(color));
		}

		oredict(InspirationsShared.splashBottle, "bottleSplash");
		oredict(InspirationsShared.lingeringBottle, "bottleLingering");
	}

	private static void registerUtility() {
		if(InspirationsUtility.carpetedTrapdoors != null) {
			for(Block block : InspirationsUtility.carpetedTrapdoors) {
				oredict(block, "trapdoorCarpeted");
			}
		}
	}

	/* Helper functions */
	public static String dyeNameFor(@Nonnull EnumDyeColor color) {
		return "dye" + DYES[color.getMetadata()];
	}

	public static void oredict(Item item, String... name) {
		oredict(item, OreDictionary.WILDCARD_VALUE, name);
	}

	public static void oredict(Block block, String... name) {
		oredict(block, OreDictionary.WILDCARD_VALUE, name);
	}

	public static void oredict(Item item, int meta, String... name) {
		oredict(new ItemStack(item, 1, meta), name);
	}

	public static void oredict(Block block, int meta, String... name) {
		oredict(new ItemStack(block, 1, meta), name);
	}

	public static void oredict(ItemStack stack, String... names) {
		if(stack != null && !stack.isEmpty()) {
			for(String name : names) {
				OreDictionary.registerOre(name, stack);
			}
		}
	}
}
