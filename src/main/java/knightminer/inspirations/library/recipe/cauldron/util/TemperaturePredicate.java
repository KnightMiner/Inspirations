package knightminer.inspirations.library.recipe.cauldron.util;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * Predicate to check cauldron boiling state
 */
public enum TemperaturePredicate implements Predicate<Boolean> {
  ANY,
  HOT,
  COLD;

  private final String name = name().toLowerCase(Locale.US);

  /**
   * Checks if this matches the given boiling predicate
   * @param boiling  Boiling type to match
   * @return  True if this type matches the given boiling state
   */
  public boolean test(boolean boiling) {
    return this == ANY || this == (boiling ? HOT : COLD);
  }

  @Override
  public boolean test(Boolean bool) {
    return test(bool == Boolean.TRUE);
  }

  /**
   * Gets the name of this type
   * @return  Type name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets a predicate for the given name
   * @param name  Name to check
   * @return  Value, or null if missing
   */
  @Nullable
  public static TemperaturePredicate byName(String name) {
    for (TemperaturePredicate boiling : values()) {
      if (boiling.getName().equals(name)) {
        return boiling;
      }
    }
    return null;
  }
}
