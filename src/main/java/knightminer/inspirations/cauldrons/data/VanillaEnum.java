package knightminer.inspirations.cauldrons.data;

import knightminer.inspirations.common.InspirationsCommons;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import slimeknights.mantle.registration.object.EnumObject;

/**
 * Class containing {@link EnumObject} instances for many colored vanilla blocks.
 * This class is currently only used in datagen. To prevent wasted memory, any objects used outside data gen should be called via {@link InspirationsCommons}
 */
public class VanillaEnum {
  public static final EnumObject<DyeColor,Block> BED = new EnumObject.Builder<DyeColor,Block>(DyeColor.class)
      .put(DyeColor.WHITE,      Blocks.WHITE_BED)
      .put(DyeColor.ORANGE,     Blocks.ORANGE_BED)
      .put(DyeColor.MAGENTA,    Blocks.MAGENTA_BED)
      .put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_BED)
      .put(DyeColor.YELLOW,     Blocks.YELLOW_BED)
      .put(DyeColor.LIME,       Blocks.LIME_BED)
      .put(DyeColor.PINK,       Blocks.PINK_BED)
      .put(DyeColor.GRAY,       Blocks.GRAY_BED)
      .put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_BED)
      .put(DyeColor.CYAN,       Blocks.CYAN_BED)
      .put(DyeColor.PURPLE,     Blocks.PURPLE_BED)
      .put(DyeColor.BLUE,       Blocks.BLUE_BED)
      .put(DyeColor.BROWN,      Blocks.BROWN_BED)
      .put(DyeColor.GREEN,      Blocks.GREEN_BED)
      .put(DyeColor.RED,        Blocks.RED_BED)
      .put(DyeColor.BLACK,      Blocks.BLACK_BED)
      .build();

  public static final EnumObject<DyeColor,Block> CARPET = InspirationsCommons.VANILLA_CARPETS;

  public static final EnumObject<DyeColor,Block> SHULKER_BOX = new EnumObject.Builder<DyeColor,Block>(DyeColor.class)
      .put(DyeColor.WHITE, Blocks.WHITE_SHULKER_BOX)
      .put(DyeColor.ORANGE,     Blocks.ORANGE_SHULKER_BOX)
      .put(DyeColor.MAGENTA,    Blocks.MAGENTA_SHULKER_BOX)
      .put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_SHULKER_BOX)
      .put(DyeColor.YELLOW,     Blocks.YELLOW_SHULKER_BOX)
      .put(DyeColor.LIME,       Blocks.LIME_SHULKER_BOX)
      .put(DyeColor.PINK,       Blocks.PINK_SHULKER_BOX)
      .put(DyeColor.GRAY,       Blocks.GRAY_SHULKER_BOX)
      .put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_SHULKER_BOX)
      .put(DyeColor.CYAN,       Blocks.CYAN_SHULKER_BOX)
      .put(DyeColor.PURPLE,     Blocks.PURPLE_SHULKER_BOX)
      .put(DyeColor.BLUE,       Blocks.BLUE_SHULKER_BOX)
      .put(DyeColor.BROWN,      Blocks.BROWN_SHULKER_BOX)
      .put(DyeColor.GREEN,      Blocks.GREEN_SHULKER_BOX)
      .put(DyeColor.RED,        Blocks.RED_SHULKER_BOX)
      .put(DyeColor.BLACK,      Blocks.BLACK_SHULKER_BOX)
      .build();

  public static final EnumObject<DyeColor,Block> STAINED_GLASS = new EnumObject.Builder<DyeColor,Block>(DyeColor.class)
      .put(DyeColor.WHITE, Blocks.WHITE_STAINED_GLASS)
      .put(DyeColor.ORANGE,     Blocks.ORANGE_STAINED_GLASS)
      .put(DyeColor.MAGENTA,    Blocks.MAGENTA_STAINED_GLASS)
      .put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_STAINED_GLASS)
      .put(DyeColor.YELLOW,     Blocks.YELLOW_STAINED_GLASS)
      .put(DyeColor.LIME,       Blocks.LIME_STAINED_GLASS)
      .put(DyeColor.PINK,       Blocks.PINK_STAINED_GLASS)
      .put(DyeColor.GRAY,       Blocks.GRAY_STAINED_GLASS)
      .put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_STAINED_GLASS)
      .put(DyeColor.CYAN,       Blocks.CYAN_STAINED_GLASS)
      .put(DyeColor.PURPLE,     Blocks.PURPLE_STAINED_GLASS)
      .put(DyeColor.BLUE,       Blocks.BLUE_STAINED_GLASS)
      .put(DyeColor.BROWN,      Blocks.BROWN_STAINED_GLASS)
      .put(DyeColor.GREEN,      Blocks.GREEN_STAINED_GLASS)
      .put(DyeColor.RED,        Blocks.RED_STAINED_GLASS)
      .put(DyeColor.BLACK,      Blocks.BLACK_STAINED_GLASS)
      .build();

  public static final EnumObject<DyeColor,Block> STAINED_GLASS_PANE = new EnumObject.Builder<DyeColor,Block>(DyeColor.class)
      .put(DyeColor.WHITE, Blocks.WHITE_STAINED_GLASS_PANE)
      .put(DyeColor.ORANGE,     Blocks.ORANGE_STAINED_GLASS_PANE)
      .put(DyeColor.MAGENTA,    Blocks.MAGENTA_STAINED_GLASS_PANE)
      .put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE)
      .put(DyeColor.YELLOW,     Blocks.YELLOW_STAINED_GLASS_PANE)
      .put(DyeColor.LIME,       Blocks.LIME_STAINED_GLASS_PANE)
      .put(DyeColor.PINK,       Blocks.PINK_STAINED_GLASS_PANE)
      .put(DyeColor.GRAY,       Blocks.GRAY_STAINED_GLASS_PANE)
      .put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE)
      .put(DyeColor.CYAN,       Blocks.CYAN_STAINED_GLASS_PANE)
      .put(DyeColor.PURPLE,     Blocks.PURPLE_STAINED_GLASS_PANE)
      .put(DyeColor.BLUE,       Blocks.BLUE_STAINED_GLASS_PANE)
      .put(DyeColor.BROWN,      Blocks.BROWN_STAINED_GLASS_PANE)
      .put(DyeColor.GREEN,      Blocks.GREEN_STAINED_GLASS_PANE)
      .put(DyeColor.RED,        Blocks.RED_STAINED_GLASS_PANE)
      .put(DyeColor.BLACK,      Blocks.BLACK_STAINED_GLASS_PANE)
      .build();

  public static final EnumObject<DyeColor,Block> TERRACOTTA = new EnumObject.Builder<DyeColor,Block>(DyeColor.class)
      .put(DyeColor.WHITE, Blocks.WHITE_TERRACOTTA)
      .put(DyeColor.ORANGE,     Blocks.ORANGE_TERRACOTTA)
      .put(DyeColor.MAGENTA,    Blocks.MAGENTA_TERRACOTTA)
      .put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_TERRACOTTA)
      .put(DyeColor.YELLOW,     Blocks.YELLOW_TERRACOTTA)
      .put(DyeColor.LIME,       Blocks.LIME_TERRACOTTA)
      .put(DyeColor.PINK,       Blocks.PINK_TERRACOTTA)
      .put(DyeColor.GRAY,       Blocks.GRAY_TERRACOTTA)
      .put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_TERRACOTTA)
      .put(DyeColor.CYAN,       Blocks.CYAN_TERRACOTTA)
      .put(DyeColor.PURPLE,     Blocks.PURPLE_TERRACOTTA)
      .put(DyeColor.BLUE,       Blocks.BLUE_TERRACOTTA)
      .put(DyeColor.BROWN,      Blocks.BROWN_TERRACOTTA)
      .put(DyeColor.GREEN,      Blocks.GREEN_TERRACOTTA)
      .put(DyeColor.RED,        Blocks.RED_TERRACOTTA)
      .put(DyeColor.BLACK,      Blocks.BLACK_TERRACOTTA)
      .build();

  public static final EnumObject<DyeColor,Block> WOOL = new EnumObject.Builder<DyeColor,Block>(DyeColor.class)
      .put(DyeColor.WHITE,      Blocks.WHITE_WOOL)
      .put(DyeColor.ORANGE,     Blocks.ORANGE_WOOL)
      .put(DyeColor.MAGENTA,    Blocks.MAGENTA_WOOL)
      .put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL)
      .put(DyeColor.YELLOW,     Blocks.YELLOW_WOOL)
      .put(DyeColor.LIME,       Blocks.LIME_WOOL)
      .put(DyeColor.PINK,       Blocks.PINK_WOOL)
      .put(DyeColor.GRAY,       Blocks.GRAY_WOOL)
      .put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL)
      .put(DyeColor.CYAN,       Blocks.CYAN_WOOL)
      .put(DyeColor.PURPLE,     Blocks.PURPLE_WOOL)
      .put(DyeColor.BLUE,       Blocks.BLUE_WOOL)
      .put(DyeColor.BROWN,      Blocks.BROWN_WOOL)
      .put(DyeColor.GREEN,      Blocks.GREEN_WOOL)
      .put(DyeColor.RED,        Blocks.RED_WOOL)
      .put(DyeColor.BLACK,      Blocks.BLACK_WOOL)
      .build();
}
