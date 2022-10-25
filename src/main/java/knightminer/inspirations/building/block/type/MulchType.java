package knightminer.inspirations.building.block.type;

import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * Variants for each of the mulch types
 */
public enum MulchType implements StringRepresentable {
  PLAIN(null, MaterialColor.COLOR_LIGHT_GRAY),
  BROWN(DyeColor.BROWN, MaterialColor.DIRT),
  RED(DyeColor.RED, MaterialColor.NETHER),
  BLACK(DyeColor.BLACK, MaterialColor.COLOR_GRAY),
  BLUE(DyeColor.BLUE, MaterialColor.COLOR_BLUE);

  private final String name = name().toLowerCase(Locale.ROOT);
  @Nullable
  private final DyeColor dye;
  private final MaterialColor color;

  MulchType(@Nullable DyeColor dye, MaterialColor color) {
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
   * Gets the material color for this mulch type
   * @return Material color
   */
  public MaterialColor getColor() {
    return this.color;
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
