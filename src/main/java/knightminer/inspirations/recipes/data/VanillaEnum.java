package knightminer.inspirations.recipes.data;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.DyeColor;
import slimeknights.mantle.registration.object.EnumObject;

public class VanillaEnum {
  public static final EnumObject<DyeColor,Block> CONCRETE = new EnumObject.Builder<DyeColor,Block>(DyeColor.class)
      .put(DyeColor.WHITE, Blocks.WHITE_CONCRETE.delegate)
      .put(DyeColor.ORANGE,     Blocks.ORANGE_CONCRETE.delegate)
      .put(DyeColor.MAGENTA,    Blocks.MAGENTA_CONCRETE.delegate)
      .put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_CONCRETE.delegate)
      .put(DyeColor.YELLOW,     Blocks.YELLOW_CONCRETE.delegate)
      .put(DyeColor.LIME,       Blocks.LIME_CONCRETE.delegate)
      .put(DyeColor.PINK,       Blocks.PINK_CONCRETE.delegate)
      .put(DyeColor.GRAY,       Blocks.GRAY_CONCRETE.delegate)
      .put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_CONCRETE.delegate)
      .put(DyeColor.CYAN,       Blocks.CYAN_CONCRETE.delegate)
      .put(DyeColor.PURPLE,     Blocks.PURPLE_CONCRETE.delegate)
      .put(DyeColor.BLUE,       Blocks.BLUE_CONCRETE.delegate)
      .put(DyeColor.BROWN,      Blocks.BROWN_CONCRETE.delegate)
      .put(DyeColor.GREEN,      Blocks.GREEN_CONCRETE.delegate)
      .put(DyeColor.RED,        Blocks.RED_CONCRETE.delegate)
      .put(DyeColor.BLACK,      Blocks.BLACK_CONCRETE.delegate)
      .build();

  public static final EnumObject<DyeColor,Block> CONCRETE_POWDER = new EnumObject.Builder<DyeColor,Block>(DyeColor.class)
      .put(DyeColor.WHITE,      Blocks.WHITE_CONCRETE_POWDER.delegate)
      .put(DyeColor.ORANGE,     Blocks.ORANGE_CONCRETE_POWDER.delegate)
      .put(DyeColor.MAGENTA,    Blocks.MAGENTA_CONCRETE_POWDER.delegate)
      .put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_CONCRETE_POWDER.delegate)
      .put(DyeColor.YELLOW,     Blocks.YELLOW_CONCRETE_POWDER.delegate)
      .put(DyeColor.LIME,       Blocks.LIME_CONCRETE_POWDER.delegate)
      .put(DyeColor.PINK,       Blocks.PINK_CONCRETE_POWDER.delegate)
      .put(DyeColor.GRAY,       Blocks.GRAY_CONCRETE_POWDER.delegate)
      .put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_CONCRETE_POWDER.delegate)
      .put(DyeColor.CYAN,       Blocks.CYAN_CONCRETE_POWDER.delegate)
      .put(DyeColor.PURPLE,     Blocks.PURPLE_CONCRETE_POWDER.delegate)
      .put(DyeColor.BLUE,       Blocks.BLUE_CONCRETE_POWDER.delegate)
      .put(DyeColor.BROWN,      Blocks.BROWN_CONCRETE_POWDER.delegate)
      .put(DyeColor.GREEN,      Blocks.GREEN_CONCRETE_POWDER.delegate)
      .put(DyeColor.RED,        Blocks.RED_CONCRETE_POWDER.delegate)
      .put(DyeColor.BLACK,      Blocks.BLACK_CONCRETE_POWDER.delegate)
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
}
