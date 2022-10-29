package knightminer.inspirations.library;

import knightminer.inspirations.Inspirations;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class InspirationsTags {
  public static class Blocks {
    public static final TagKey<Block> MULCH = tag("mulch");
    public static final TagKey<Block> SMALL_FLOWERS = tag("small_flowers");
    public static final TagKey<Block> FLOWER_POTS = tag("flower_pots");
    public static final TagKey<Block> CARPETED_TRAPDOORS = tag("carpeted_trapdoors");
    public static final TagKey<Block> CARPETED_PRESSURE_PLATES = tag("carpeted_pressure_plates");
    public static final TagKey<Block> SHELVES = tag("shelves");
    public static final TagKey<Block> ENLIGHTENED_BUSHES = tag("enlightened_bushes");

    /** Blocks with this tag act as fire for the cauldron */
    public static final TagKey<Block> CAULDRON_FIRE = tag("cauldron_fire");
    /** Blocks that act as ice to surround the cauldron */
    public static final TagKey<Block> CAULDRON_ICE = tag("cauldron_ice");

    /**
     * Creates a block tag for Inspirations
     */
    private static TagKey<Block> tag(String name) {
      return BlockTags.create(Inspirations.getResource(name));
    }
  }

  public static class Items {
    // Duplicates of above. Flower pots and pressure plates have no item form.
    public static final TagKey<Item> MULCH = tag("mulch");
    public static final TagKey<Item> SMALL_FLOWERS = tag("small_flowers");
    public static final TagKey<Item> CARPETED_TRAPDOORS = tag("carpeted_trapdoors");
    public static final TagKey<Item> BOOKSHELVES = tag("bookshelves");
    public static final TagKey<Item> ENLIGHTENED_BUSHES = tag("enlightened_bushes");

    /**
     * Items with this tag are registered to perform cauldron recipes.
     */
    public static final TagKey<Item> DISP_CAULDRON_RECIPES = tag("cauldron_recipes");

    /**
     * Items with this tag are registered to have fluid tank functionality.
     */
    public static final TagKey<Item> DISP_FLUID_TANKS = tag("fluid_containers");

    public static final TagKey<Item> MILK_CONTAINERS = tag("milk_containers");

    /**
     * Forge tags for anything that is a book
     */
    public static final TagKey<Item> FORGE_BOOKS = forgeTag("books");

    /**
     * Items which are valid to be placed on bookshelves.
     */
    public static final TagKey<Item> BOOKS = tag("books");

    /**
     * Vanilla carpets and shulker boxes, for recipe use.
     */
    public static final TagKey<Item> CARPETS = tag("carpets");
    public static final TagKey<Item> SHULKER_BOXES = tag("shulker_boxes");
    public static final TagKey<Item> TERRACOTTA = tag("terracotta");

    public static final TagKey<Item> DYE_BOTTLES = tag("dyed_water_bottles");

    /* Inputs for potion cauldron recipes */
    public static final TagKey<Item> SPLASH_BOTTLES = forgeTag("splash_bottles");
    public static final TagKey<Item> LINGERING_BOTTLES = forgeTag("lingering_bottles");

    /**
     * Creates an item tag for Inspirations
     */
    private static TagKey<Item> tag(String name) {
      return ItemTags.create(Inspirations.getResource(name));
    }
    private static TagKey<Item> forgeTag(String name) {
      return ItemTags.create(new ResourceLocation("forge", name));
    }
  }

  public static class Fluids {
    public static final TagKey<Fluid> HONEY = forgeTag("honey");
    public static final TagKey<Fluid> BEETROOT_SOUP = forgeTag("beetroot_soup");
    public static final TagKey<Fluid> MUSHROOM_STEW = forgeTag("mushroom_stew");
    public static final TagKey<Fluid> RABBIT_STEW = forgeTag("rabbit_stew");

    private static TagKey<Fluid> forgeTag(String name) {
      return FluidTags.create(new ResourceLocation("forge", name));
    }
  }
}
