package knightminer.inspirations.library.recipe.cauldron.util;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * Predicate to check cauldron boiling state
 */
public enum TemperaturePredicate implements Predicate<CauldronTemperature> {
  /** Any temperature matches */
  ANY {
    @Override
    public boolean test(CauldronTemperature temperature) {
      return true;
    }
  },
  /** Cauldron must be boiling */
  BOILING {
    @Override
    public boolean test(CauldronTemperature temperature) {
      return temperature == CauldronTemperature.BOILING;
    }
  },
  /** Cauldron must be freezing */
  FREEZING {
    @Override
    public boolean test(CauldronTemperature temperature) {
      return temperature == CauldronTemperature.FREEZING;
    }
  },
  /** Cauldron must not be boiling or freezing */
  NORMAL {
    @Override
    public boolean test(CauldronTemperature temperature) {
      return temperature == CauldronTemperature.NORMAL;
    }
  },
  /** Cauldron must not be freezing */
  WARM {
    @Override
    public boolean test(CauldronTemperature temperature) {
      return temperature != CauldronTemperature.FREEZING;
    }
  },
  /** Cauldron must not be boiling */
  COOL {
    @Override
    public boolean test(CauldronTemperature temperature) {
      return temperature != CauldronTemperature.BOILING;
    }
  };

  private final String name = name().toLowerCase(Locale.US);

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
