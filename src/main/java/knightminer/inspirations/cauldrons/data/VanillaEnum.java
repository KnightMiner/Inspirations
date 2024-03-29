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
      .put(DyeColor.WHITE,      Blocks.WHITE_BED.delegate)
      .put(DyeColor.ORANGE,     Blocks.ORANGE_BED.delegate)
      .put(DyeColor.MAGENTA,    Blocks.MAGENTA_BED.delegate)
      .put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_BED.delegate)
      .put(DyeColor.YELLOW,     Blocks.YELLOW_BED.delegate)
      .put(DyeColor.LIME,       Blocks.LIME_BED.delegate)
      .put(DyeColor.PINK,       Blocks.PINK_BED.delegate)
      .put(DyeColor.GRAY,       Blocks.GRAY_BED.delegate)
      .put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_BED.delegate)
      .put(DyeColor.CYAN,       Blocks.CYAN_BED.delegate)
      .put(DyeColor.PURPLE,     Blocks.PURPLE_BED.delegate)
      .put(DyeColor.BLUE,       Blocks.BLUE_BED.delegate)
      .put(DyeColor.BROWN,      Blocks.BROWN_BED.delegate)
      .put(DyeColor.GREEN,      Blocks.GREEN_BED.delegate)
      .put(DyeColor.RED,        Blocks.RED_BED.delegate)
      .put(DyeColor.BLACK,      Blocks.BLACK_BED.delegate)
      .build();

  public static final EnumObject<DyeColor,Block> CARPET = InspirationsCommons.VANILLA_CARPETS;

  public static final EnumObject<DyeColor,Block> SHULKER_BOX = new EnumObject.Builder<DyeColor,Block>(DyeColor.class)
      .put(DyeColor.WHITE, Blocks.WHITE_SHULKER_BOX.delegate)
      .put(DyeColor.ORANGE,     Blocks.ORANGE_SHULKER_BOX.delegate)
      .put(DyeColor.MAGENTA,    Blocks.MAGENTA_SHULKER_BOX.delegate)
      .put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_SHULKER_BOX.delegate)
      .put(DyeColor.YELLOW,     Blocks.YELLOW_SHULKER_BOX.delegate)
      .put(DyeColor.LIME,       Blocks.LIME_SHULKER_BOX.delegate)
      .put(DyeColor.PINK,       Blocks.PINK_SHULKER_BOX.delegate)
      .put(DyeColor.GRAY,       Blocks.GRAY_SHULKER_BOX.delegate)
      .put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_SHULKER_BOX.delegate)
      .put(DyeColor.CYAN,       Blocks.CYAN_SHULKER_BOX.delegate)
      .put(DyeColor.PURPLE,     Blocks.PURPLE_SHULKER_BOX.delegate)
      .put(DyeColor.BLUE,       Blocks.BLUE_SHULKER_BOX.delegate)
      .put(DyeColor.BROWN,      Blocks.BROWN_SHULKER_BOX.delegate)
      .put(DyeColor.GREEN,      Blocks.GREEN_SHULKER_BOX.delegate)
      .put(DyeColor.RED,        Blocks.RED_SHULKER_BOX.delegate)
      .put(DyeColor.BLACK,      Blocks.BLACK_SHULKER_BOX.delegate)
      .build();

  public static final EnumObject<DyeColor,Block> STAINED_GLASS = new EnumObject.Builder<DyeColor,Block>(DyeColor.class)
      .put(DyeColor.WHITE, Blocks.WHITE_STAINED_GLASS.delegate)
      .put(DyeColor.ORANGE,     Blocks.ORANGE_STAINED_GLASS.delegate)
      .put(DyeColor.MAGENTA,    Blocks.MAGENTA_STAINED_GLASS.delegate)
      .put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_STAINED_GLASS.delegate)
      .put(DyeColor.YELLOW,     Blocks.YELLOW_STAINED_GLASS.delegate)
      .put(DyeColor.LIME,       Blocks.LIME_STAINED_GLASS.delegate)
      .put(DyeColor.PINK,       Blocks.PINK_STAINED_GLASS.delegate)
      .put(DyeColor.GRAY,       Blocks.GRAY_STAINED_GLASS.delegate)
      .put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_STAINED_GLASS.delegate)
      .put(DyeColor.CYAN,       Blocks.CYAN_STAINED_GLASS.delegate)
      .put(DyeColor.PURPLE,     Blocks.PURPLE_STAINED_GLASS.delegate)
      .put(DyeColor.BLUE,       Blocks.BLUE_STAINED_GLASS.delegate)
      .put(DyeColor.BROWN,      Blocks.BROWN_STAINED_GLASS.delegate)
      .put(DyeColor.GREEN,      Blocks.GREEN_STAINED_GLASS.delegate)
      .put(DyeColor.RED,        Blocks.RED_STAINED_GLASS.delegate)
      .put(DyeColor.BLACK,      Blocks.BLACK_STAINED_GLASS.delegate)
      .build();

  public static final EnumObject<DyeColor,Block> STAINED_GLASS_PANE = new EnumObject.Builder<DyeColor,Block>(DyeColor.class)
      .put(DyeColor.WHITE, Blocks.WHITE_STAINED_GLASS_PANE.delegate)
      .put(DyeColor.ORANGE,     Blocks.ORANGE_STAINED_GLASS_PANE.delegate)
      .put(DyeColor.MAGENTA,    Blocks.MAGENTA_STAINED_GLASS_PANE.delegate)
      .put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE.delegate)
      .put(DyeColor.YELLOW,     Blocks.YELLOW_STAINED_GLASS_PANE.delegate)
      .put(DyeColor.LIME,       Blocks.LIME_STAINED_GLASS_PANE.delegate)
      .put(DyeColor.PINK,       Blocks.PINK_STAINED_GLASS_PANE.delegate)
      .put(DyeColor.GRAY,       Blocks.GRAY_STAINED_GLASS_PANE.delegate)
      .put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE.delegate)
      .put(DyeColor.CYAN,       Blocks.CYAN_STAINED_GLASS_PANE.delegate)
      .put(DyeColor.PURPLE,     Blocks.PURPLE_STAINED_GLASS_PANE.delegate)
      .put(DyeColor.BLUE,       Blocks.BLUE_STAINED_GLASS_PANE.delegate)
      .put(DyeColor.BROWN,      Blocks.BROWN_STAINED_GLASS_PANE.delegate)
      .put(DyeColor.GREEN,      Blocks.GREEN_STAINED_GLASS_PANE.delegate)
      .put(DyeColor.RED,        Blocks.RED_STAINED_GLASS_PANE.delegate)
      .put(DyeColor.BLACK,      Blocks.BLACK_STAINED_GLASS_PANE.delegate)
      .build();

  public static final EnumObject<DyeColor,Block> TERRACOTTA = new EnumObject.Builder<DyeColor,Block>(DyeColor.class)
      .put(DyeColor.WHITE, Blocks.WHITE_TERRACOTTA.delegate)
      .put(DyeColor.ORANGE,     Blocks.ORANGE_TERRACOTTA.delegate)
      .put(DyeColor.MAGENTA,    Blocks.MAGENTA_TERRACOTTA.delegate)
      .put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_TERRACOTTA.delegate)
      .put(DyeColor.YELLOW,     Blocks.YELLOW_TERRACOTTA.delegate)
      .put(DyeColor.LIME,       Blocks.LIME_TERRACOTTA.delegate)
      .put(DyeColor.PINK,       Blocks.PINK_TERRACOTTA.delegate)
      .put(DyeColor.GRAY,       Blocks.GRAY_TERRACOTTA.delegate)
      .put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_TERRACOTTA.delegate)
      .put(DyeColor.CYAN,       Blocks.CYAN_TERRACOTTA.delegate)
      .put(DyeColor.PURPLE,     Blocks.PURPLE_TERRACOTTA.delegate)
      .put(DyeColor.BLUE,       Blocks.BLUE_TERRACOTTA.delegate)
      .put(DyeColor.BROWN,      Blocks.BROWN_TERRACOTTA.delegate)
      .put(DyeColor.GREEN,      Blocks.GREEN_TERRACOTTA.delegate)
      .put(DyeColor.RED,        Blocks.RED_TERRACOTTA.delegate)
      .put(DyeColor.BLACK,      Blocks.BLACK_TERRACOTTA.delegate)
      .build();

  public static final EnumObject<DyeColor,Block> WOOL = new EnumObject.Builder<DyeColor,Block>(DyeColor.class)
      .put(DyeColor.WHITE,      Blocks.WHITE_WOOL.delegate)
      .put(DyeColor.ORANGE,     Blocks.ORANGE_WOOL.delegate)
      .put(DyeColor.MAGENTA,    Blocks.MAGENTA_WOOL.delegate)
      .put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL.delegate)
      .put(DyeColor.YELLOW,     Blocks.YELLOW_WOOL.delegate)
      .put(DyeColor.LIME,       Blocks.LIME_WOOL.delegate)
      .put(DyeColor.PINK,       Blocks.PINK_WOOL.delegate)
      .put(DyeColor.GRAY,       Blocks.GRAY_WOOL.delegate)
      .put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL.delegate)
      .put(DyeColor.CYAN,       Blocks.CYAN_WOOL.delegate)
      .put(DyeColor.PURPLE,     Blocks.PURPLE_WOOL.delegate)
      .put(DyeColor.BLUE,       Blocks.BLUE_WOOL.delegate)
      .put(DyeColor.BROWN,      Blocks.BROWN_WOOL.delegate)
      .put(DyeColor.GREEN,      Blocks.GREEN_WOOL.delegate)
      .put(DyeColor.RED,        Blocks.RED_WOOL.delegate)
      .put(DyeColor.BLACK,      Blocks.BLACK_WOOL.delegate)
      .build();
}
