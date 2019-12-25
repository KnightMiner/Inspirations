package knightminer.inspirations.library;

import knightminer.inspirations.Inspirations;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class InspirationsTags {

	public static class Blocks {

		// Blocks with this tag act as fire for the cauldron.
		public static final Tag<Block> CAULDRON_FIRE = new BlockTags.Wrapper(new ResourceLocation(Inspirations.modID, "cauldron_fire"));
	}

	public static class Items {
		// Items with this tag are registered to perform cauldron recipes.
		public static final Tag<Item> DISP_CAULDRON_RECIPES = new ItemTags.Wrapper(new ResourceLocation(Inspirations.modID, "cauldron_recipes"));

		// Items with this tag are registered to have fluid tank functionality.
		public static final Tag<Item> DISP_FLUID_TANKS = new ItemTags.Wrapper(new ResourceLocation(Inspirations.modID, "fluid_containers"));

		public static final Tag<Item> MILK_CONTAINERS = new ItemTags.Wrapper(new ResourceLocation(Inspirations.modID, "milk_containers"));

		public static final Tag<Item> DYE_BOTTLES = new ItemTags.Wrapper(Util.getResource("dyed_water_bottles"));
	}
}
