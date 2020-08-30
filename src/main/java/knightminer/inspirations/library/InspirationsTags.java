package knightminer.inspirations.library;

import knightminer.inspirations.Inspirations;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;

public class InspirationsTags {
  public static class Blocks {
    public static final INamedTag<Block> MULCH = tag("mulch");
    public static final INamedTag<Block> SMALL_FLOWERS = tag("small_flowers");
    public static final INamedTag<Block> FLOWER_POTS = tag("flower_pots");
    public static final INamedTag<Block> CARPETED_TRAPDOORS = tag("carpeted_trapdoors");
    public static final INamedTag<Block> CARPETED_PRESSURE_PLATES = tag("carpeted_pressure_plates");
    public static final INamedTag<Block> BOOKSHELVES = tag("bookshelves");
    public static final INamedTag<Block> ENLIGHTENED_BUSHES = tag("enlightened_bushes");

    /**
     * Blocks with this tag act as fire for the cauldron.
     */
    public static final INamedTag<Block> CAULDRON_FIRE = tag("cauldron_fire");

    /**
     * Creates a block tag for Inspirations
     */
    private static INamedTag<Block> tag(String name) {
      return BlockTags.makeWrapperTag(Inspirations.modID + ":" + name);
    }
  }

  public static class Items {
    // Duplicates of above. Flower pots and pressure plates have no item form.
    public static final INamedTag<Item> MULCH = tag("mulch");
    public static final INamedTag<Item> SMALL_FLOWERS = tag("small_flowers");
    public static final INamedTag<Item> CARPETED_TRAPDOORS = tag("carpeted_trapdoors");
    public static final INamedTag<Item> BOOKSHELVES = tag("bookshelves");
    public static final INamedTag<Item> ENLIGHTENED_BUSHES = tag("enlightened_bushes");

    /**
     * Items with this tag are registered to perform cauldron recipes.
     */
    public static final INamedTag<Item> DISP_CAULDRON_RECIPES = tag("cauldron_recipes");

    /**
     * Items with this tag are registered to have fluid tank functionality.
     */
    public static final INamedTag<Item> DISP_FLUID_TANKS = tag("fluid_containers");

    public static final INamedTag<Item> MILK_CONTAINERS = tag("milk_containers");

    /**
     * Items which are valid to be placed on bookshelves.
     */
    public static final INamedTag<Item> BOOKS = tag("books");

    /**
     * Vanilla carpets, for recipe use.
     */
    public static final INamedTag<Item> CARPETS = tag("carpets");

    public static final INamedTag<Item> WAYPOINT_COMPASSES = tag("waypoint_compasses");
    public static final INamedTag<Item> DYE_BOTTLES = tag("dyed_water_bottles");

    /* Inputs for potion cauldron recipes */
    public static final INamedTag<Item> SPLASH_BOTTLES = forgeTag("splash_bottles");
    public static final INamedTag<Item> LINGERING_BOTTLES = forgeTag("lingering_bottles");

    /**
     * Creates an item tag for Inspirations
     */
    private static INamedTag<Item> tag(String name) {
      return ItemTags.makeWrapperTag(Inspirations.modID + ":" + name);
    }
    private static INamedTag<Item> forgeTag(String name) {
      return ItemTags.makeWrapperTag("forge:" + name);
    }
  }
}
