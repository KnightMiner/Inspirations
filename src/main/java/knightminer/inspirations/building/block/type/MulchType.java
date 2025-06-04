package knightminer.inspirations.building.block.type;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.material.MapColor;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * Variants for each of the mulch types
 */
public enum MulchType implements StringRepresentable {
  PLAIN(null, MapColor.COLOR_LIGHT_GRAY),
  BROWN(DyeColor.BROWN, MapColor.DIRT),
  RED(DyeColor.RED, MapColor.NETHER),
  BLACK(DyeColor.BLACK, MapColor.COLOR_GRAY),
  BLUE(DyeColor.BLUE, MapColor.COLOR_BLUE);

  private final String name = name().toLowerCase(Locale.ROOT);
  @Nullable
  private final DyeColor dye;
  private final MapColor color;

  MulchType(@Nullable DyeColor dye, MapColor color) {
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
  public MapColor getColor() {
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
