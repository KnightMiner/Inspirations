package knightminer.inspirations.library;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;

public class InspirationsTags {

	public static class Blocks {
		public static final Tag<Block> MULCH = new BlockTags.Wrapper(Util.getResource("mulch"));
		public static final Tag<Block> SMALL_FLOWERS = new BlockTags.Wrapper(Util.getResource("small_flowers"));
		public static final Tag<Block> CARPETED_TRAPDOORS = new BlockTags.Wrapper(Util.getResource("carpeted_trapdoors"));
		public static final Tag<Block> CARPETED_PRESSURE_PLATES = new BlockTags.Wrapper(Util.getResource("carpeted_pressure_plates"));
		public static final Tag<Block> BOOKSHELVES = new BlockTags.Wrapper(Util.getResource("bookshelves"));
		public static final Tag<Block> ENLIGHTENED_BUSHES = new BlockTags.Wrapper(Util.getResource("enlightened_bushes"));

		// Blocks with this tag act as fire for the cauldron.
		public static final Tag<Block> CAULDRON_FIRE = new BlockTags.Wrapper(Util.getResource("cauldron_fire"));
	}

	public static class Items {
		// Duplicates of above.
		public static final Tag<Item> MULCH = new ItemTags.Wrapper(Util.getResource("mulch"));
		public static final Tag<Item> SMALL_FLOWERS = new ItemTags.Wrapper(Util.getResource("small_flowers"));
		public static final Tag<Item> CARPETED_TRAPDOORS = new ItemTags.Wrapper(Util.getResource("carpeted_trapdoors"));
		public static final Tag<Item> BOOKSHELVES = new ItemTags.Wrapper(Util.getResource("bookshelves"));
		public static final Tag<Item> ENLIGHTENED_BUSHES = new ItemTags.Wrapper(Util.getResource("enlightened_bushes"));

		// Items with this tag are registered to perform cauldron recipes.
		public static final Tag<Item> DISP_CAULDRON_RECIPES = new ItemTags.Wrapper(Util.getResource("cauldron_recipes"));

		// Items with this tag are registered to have fluid tank functionality.
		public static final Tag<Item> DISP_FLUID_TANKS = new ItemTags.Wrapper(Util.getResource( "fluid_containers"));

		public static final Tag<Item> MILK_CONTAINERS = new ItemTags.Wrapper(Util.getResource("milk_containers"));

		// Items which are valid to be placed on bookshelves.
		public static final Tag<Item> BOOKS = new ItemTags.Wrapper(Util.getResource("books"));

		// Vanilla carpets, for recipe use.
		public static final Tag<Item> CARPETS = new ItemTags.Wrapper(Util.getResource("carpets"));

		public static final Tag<Item> WAYPOINT_COMPASSES = new ItemTags.Wrapper(Util.getResource("waypoint_compasses"));
		public static final Tag<Item> DYE_BOTTLES = new ItemTags.Wrapper(Util.getResource("dyed_water_bottles"));
	}
}
