package knightminer.inspirations.library;

import knightminer.inspirations.Inspirations;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

public class InspirationsTags {
  public static class Blocks {
    public static final IOptionalNamedTag<Block> MULCH = tag("mulch");
    public static final IOptionalNamedTag<Block> SMALL_FLOWERS = tag("small_flowers");
    public static final IOptionalNamedTag<Block> FLOWER_POTS = tag("flower_pots");
    public static final IOptionalNamedTag<Block> CARPETED_TRAPDOORS = tag("carpeted_trapdoors");
    public static final IOptionalNamedTag<Block> CARPETED_PRESSURE_PLATES = tag("carpeted_pressure_plates");
    public static final IOptionalNamedTag<Block> SHELVES = tag("shelves");
    public static final IOptionalNamedTag<Block> ENLIGHTENED_BUSHES = tag("enlightened_bushes");

    /** Blocks with this tag act as fire for the cauldron */
    public static final IOptionalNamedTag<Block> CAULDRON_FIRE = tag("cauldron_fire");
    /** Blocks that act as ice to surround the cauldron */
    public static final IOptionalNamedTag<Block> CAULDRON_ICE = tag("cauldron_ice");

    /**
     * Creates a block tag for Inspirations
     */
    private static IOptionalNamedTag<Block> tag(String name) {
      return BlockTags.createOptional(Inspirations.getResource(name));
    }
  }

  public static class Items {
    // Duplicates of above. Flower pots and pressure plates have no item form.
    public static final IOptionalNamedTag<Item> MULCH = tag("mulch");
    public static final IOptionalNamedTag<Item> SMALL_FLOWERS = tag("small_flowers");
    public static final IOptionalNamedTag<Item> CARPETED_TRAPDOORS = tag("carpeted_trapdoors");
    public static final IOptionalNamedTag<Item> BOOKSHELVES = tag("bookshelves");
    public static final IOptionalNamedTag<Item> ENLIGHTENED_BUSHES = tag("enlightened_bushes");

    /**
     * Items with this tag are registered to perform cauldron recipes.
     */
    public static final IOptionalNamedTag<Item> DISP_CAULDRON_RECIPES = tag("cauldron_recipes");

    /**
     * Items with this tag are registered to have fluid tank functionality.
     */
    public static final IOptionalNamedTag<Item> DISP_FLUID_TANKS = tag("fluid_containers");

    public static final IOptionalNamedTag<Item> MILK_CONTAINERS = tag("milk_containers");

    /**
     * Forge tags for anything that is a book
     */
    public static final IOptionalNamedTag<Item> FORGE_BOOKS = forgeTag("books");

    /**
     * Items which are valid to be placed on bookshelves.
     */
    public static final IOptionalNamedTag<Item> BOOKS = tag("books");

    /**
     * Vanilla carpets and shulker boxes, for recipe use.
     */
    public static final IOptionalNamedTag<Item> CARPETS = tag("carpets");
    public static final IOptionalNamedTag<Item> SHULKER_BOXES = tag("shulker_boxes");
    public static final IOptionalNamedTag<Item> TERRACOTTA = tag("terracotta");

    public static final IOptionalNamedTag<Item> WAYPOINT_COMPASSES = tag("waypoint_compasses");
    public static final IOptionalNamedTag<Item> DYE_BOTTLES = tag("dyed_water_bottles");

    /* Inputs for potion cauldron recipes */
    public static final IOptionalNamedTag<Item> SPLASH_BOTTLES = forgeTag("splash_bottles");
    public static final IOptionalNamedTag<Item> LINGERING_BOTTLES = forgeTag("lingering_bottles");

    /**
     * Creates an item tag for Inspirations
     */
    private static IOptionalNamedTag<Item> tag(String name) {
      return ItemTags.createOptional(Inspirations.getResource(name));
    }
    private static IOptionalNamedTag<Item> forgeTag(String name) {
      return ItemTags.createOptional(new ResourceLocation("forge", name));
    }
  }

  public static class Fluids {
    public static final IOptionalNamedTag<Fluid> MILK = forgeTag("milk");

    private static IOptionalNamedTag<Fluid> forgeTag(String name) {
      return FluidTags.createOptional(new ResourceLocation("forge", name));
    }
  }
}
