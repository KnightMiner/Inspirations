package knightminer.inspirations.building.block.type;

import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;

/**
 * Variants for each of the mulch types
 */
public enum MulchType implements IStringSerializable {
  PLAIN(MaterialColor.LIGHT_GRAY),
  BROWN(MaterialColor.DIRT),
  RED(MaterialColor.NETHERRACK),
  BLACK(MaterialColor.GRAY),
  BLUE(MaterialColor.BLUE);

  private final String name = name().toLowerCase(Locale.ROOT);
  private final MaterialColor color;
  MulchType(MaterialColor color) {
    this.color = color;
  }

  /**
   * Gets the material color for this mulch type
   * @return  Material color
   */
  public MaterialColor getColor() {
    return this.color;
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
