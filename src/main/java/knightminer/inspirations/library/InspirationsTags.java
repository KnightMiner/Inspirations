package knightminer.inspirations.library;

import knightminer.inspirations.Inspirations;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.Mantle;

public class InspirationsTags {
  public static class Blocks {
    public static final TagKey<Block> MULCH = tag("mulch");
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
    public static final TagKey<Item> CARPETED_TRAPDOORS = tag("carpeted_trapdoors");
    public static final TagKey<Item> BOOKSHELVES = tag("bookshelves");
    public static final TagKey<Item> ENLIGHTENED_BUSHES = tag("enlightened_bushes");

    /** Items with this tag are registered to have fluid tank functionality. This is currently unimplemented due to changes to how cauldrons work. */
    public static final TagKey<Item> DISP_FLUID_TANKS = tag("fluid_containers");

    /** Items which can be filled by milking a cow, causing the milk cooldown to apply. */
    public static final TagKey<Item> MILK_CONTAINERS = tag("milk_containers");

    /** Forge tags for anything that is a book */
    public static final TagKey<Item> COMMON_BOOKS = commonTag("books");

    /** Items which are valid to be placed on bookshelves. */
    public static final TagKey<Item> SHELF_BOOKS = tag("books");

    /** Mirror of {@link BlockTags#SHULKER_BOXES} for items, since vanilla does not copy the tag to items. */
    public static final TagKey<Item> SHULKER_BOXES = tag("shulker_boxes");

    /**
     * Creates an item tag for Inspirations
     */
    private static TagKey<Item> tag(String name) {
      return ItemTags.create(Inspirations.getResource(name));
    }
    private static TagKey<Item> commonTag(String name) {
      return ItemTags.create(Mantle.commonResource(name));
    }
  }

  public static class Fluids {
    /** Contains Inspirations honey fluids (still and flowing)). See {@link slimeknights.mantle.datagen.MantleTags} for common. */
    public static final TagKey<Fluid> HONEY = tag("honey");
    /** Contains Inspirations beetroot soup fluids (still and flowing)). See {@link slimeknights.mantle.datagen.MantleTags} for common. */
    public static final TagKey<Fluid> BEETROOT_SOUP = tag("beetroot_soup");
    /** Contains Inspirations mushroom stew fluids (still and flowing)). See {@link slimeknights.mantle.datagen.MantleTags} for common. */
    public static final TagKey<Fluid> MUSHROOM_STEW = tag("mushroom_stew");
    /** Contains Inspirations rabbit stew fluids (still and flowing). See {@link slimeknights.mantle.datagen.MantleTags} for common. */
    public static final TagKey<Fluid> RABBIT_STEW = tag("rabbit_stew");
    /** Contains Inspirations potato soup fluids (still and flowing) */
    public static final TagKey<Fluid> POTATO_SOUP = tag("potato_soup");

    private static TagKey<Fluid> tag(String name) {
      return FluidTags.create(Inspirations.getResource(name));
    }
  }
}
