package knightminer.inspirations.building.block.type;

import net.minecraft.item.DyeColor;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * Variants for each of the mulch types
 */
public enum BushType implements IStringSerializable {
  WHITE(null, -1),
  RED(DyeColor.RED, 0xBF0000),
  GREEN(DyeColor.GREEN, 0x267F00),
  BLUE(DyeColor.BLUE, 0x001CBF);

  private final String name = name().toLowerCase(Locale.ROOT);
  private final DyeColor dye;
  private final int color;

  BushType(@Nullable DyeColor dye, int color) {
    this.dye = dye;
    this.color = color;
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
