package knightminer.inspirations.building.block.type;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

/**
 * Variants for each of the mulch types
 */
public enum ShelfType implements IStringSerializable {
  NORMAL,
  ANCIENT,
  RAINBOW,
  TOMES;

  /** All bookshelves except normal */
  public static final ShelfType[] FANCY = {ANCIENT, RAINBOW, TOMES};
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
