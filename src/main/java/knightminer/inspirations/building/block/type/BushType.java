package knightminer.inspirations.building.block.type;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

/**
 * Variants for each of the mulch types
 */
public enum BushType implements IStringSerializable {
  WHITE(-1),
  RED(0xBF0000),
  GREEN(0x267F00),
  BLUE(0x001CBF);

  private final String name = name().toLowerCase(Locale.ROOT);
  private final int color;
  BushType(int color) {
    this.color = color;
  }

  /**
   * Gets the color of this bush for tinting
   * @return  Tint color
   */
  public int getColor() {
    return color;
  }

  @Override
  public String getString() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }
}
