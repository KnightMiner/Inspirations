package knightminer.inspirations.building.block.type;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.material.MapColor;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * Variants for each of the mulch types
 */
public enum BushType implements StringRepresentable {
  WHITE(MapColor.PLANT, null, -1),
  RED(MapColor.COLOR_RED, DyeColor.RED, 0xBF0000),
  GREEN(MapColor.COLOR_GREEN, DyeColor.GREEN, 0x267F00),
  BLUE(MapColor.COLOR_BLUE, DyeColor.BLUE, 0x001CBF);

  private final String name = name().toLowerCase(Locale.ROOT);
  private final MapColor mapColor;
  private final DyeColor dye;
  private final int color;

  BushType(MapColor mapColor, @Nullable DyeColor dye, int color) {
    this.mapColor = mapColor;
    this.dye = dye;
    this.color = color;
  }

  /** Gets the color for this bush to show on maps */
  public MapColor getMapColor() {
    return mapColor;
  }

  /**
   * Gets the dye for this color
   * @return Dye color
   */
  @Nullable
  public DyeColor getDye() {
    return dye;
  }

  /**
   * Gets the color of this bush for tinting
   * @return Tint color
   */
  public int getColor() {
    return color;
  }

  @Override
  public String getSerializedName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }
}
