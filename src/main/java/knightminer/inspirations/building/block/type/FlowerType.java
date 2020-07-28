package knightminer.inspirations.building.block.type;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

/**
 * Variants for each of the mulch types
 */
public enum FlowerType implements IStringSerializable {
  CYAN,
  SYRINGA,
  PAEONIA,
  ROSE;

  private final String name = name().toLowerCase(Locale.ROOT);

  @Override
  public String getString() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }
}
