package knightminer.inspirations.library.recipe.cauldron.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.function.IntPredicate;

import static knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe.MAX;

/**
 * Predicate to match a cauldron level
 */
public abstract class LevelPredicate implements IntPredicate {
  private static final String KEY_MIN = "min";
  private static final String KEY_MAX = "max";
  /** Cache of all levels, first key is min, second is max */
  private static final LevelPredicate[][] CACHE = new LevelPredicate[MAX + 1][MAX + 1];

  /** No constructor to prevent extension, it will not work with the read methods */
  private LevelPredicate() {}

  /**
   * Gets a level predicate for a minimum number
   * @param min  Minimum level value, must be between 0 and 3
   * @return  Level predicate
   */
  public static LevelPredicate min(int min) {
    if (min < 0 || min > MAX) {
      throw new IllegalArgumentException("Invalid minimum level " + min + ", must be between 0 and 3");
    }
    // all min can be written as a range from min to MAX
    if (CACHE[min][MAX] == null) {
      CACHE[min][MAX] = new Min(min);
    }
    return CACHE[min][MAX];
  }

  /**
   * Gets a level predicate for a maximum number
   * @param max  Maximum level value, must be between 0 and 3
   * @return  Level predicate
   */
  public static LevelPredicate max(int max) {
    if (max < 0 || max > MAX) {
      throw new IllegalArgumentException("Invalid maximum level " + max + ", must be between 0 and 3");
    }
    // all max can be written as a range from 0 to max
    if (CACHE[0][max] == null) {
      CACHE[0][max] = new Max(max);
    }
    return CACHE[0][max];
  }

  /**
   * Gets a level predicate for a range of values
   * @param min  Minumum level value
   * @param max  Maximum level value
   * @return  Level predicate
   */
  public static LevelPredicate range(int min, int max) {
    if (min < 0 || min > MAX) {
      throw new IllegalArgumentException("Invalid minimum level " + min + ", must be between 0 and 3");
    }
    if (max < 0 || max > MAX) {
      throw new IllegalArgumentException("Invalid maximum level " + max + ", must be between 0 and 3");
    }
    if (min > max) {
      throw new IllegalArgumentException("Minumum cannot be larger than maximum");
    }
    // cache is shared
    if (CACHE[min][max] == null) {
      // if max is MAX, we can ignore it
      if (max == MAX) {
        return min(min);
      }
      // if min is 0, we can ignore it
      if (min == 0) {
        return max(max);
      }
      // otherwise, new range
      CACHE[min][max] = new Range(min, max);
    }
    // return cached
    return CACHE[min][max];
  }

  /**
   * Reads a level predicate from JSON
   * @param json  JSON object
   * @return  Level predicate
   */
  public static LevelPredicate read(JsonObject json) {
    Integer min = json.has(KEY_MIN) ? JSONUtils.getInt(json, KEY_MIN) : null;
    Integer max = json.has(KEY_MAX) ? JSONUtils.getInt(json, KEY_MAX) : null;
    if (min != null) {
      if (max != null) {
        return range(min, max);
      }
      return min(min);
    } if (max != null) {
      return max(max);
    }
    throw new JsonSyntaxException("Must specify 'min' or 'max' for input");
  }

  /**
   * Reads a level predicate from the packet buffer
   * @param buffer  Buffer instance
   * @return  Level predicate
   */
  public static LevelPredicate read(PacketBuffer buffer) {
    Type type = buffer.readEnumValue(Type.class);
    int i = buffer.readVarInt();
    switch (type) {
      case MIN: return max(i);
      case MAX: return min(i);
      case RANGE: return range(i, buffer.readVarInt());
    }
    throw new DecoderException("Got null type, this should not be possible");
  }

  /**
   * Writes this to the packet buffer
   * @param buffer  Buffer instance
   */
  public abstract void write(PacketBuffer buffer);

  /**
   * Writes this to the packet buffer
   * @param json  Json object
   */
  public abstract void write(JsonObject json);

  /**
   * Writes this to JSON
   */
  public JsonObject toJson() {
    JsonObject object = new JsonObject();
    write(object);
    return object;
  }

  /**
   * Predicate to match a minimum level or higher
   */
  private static class Min extends LevelPredicate {
    private final int min;

    private Min(int min) {
      this.min = min;
    }

    @Override
    public boolean test(int value) {
      return value >= min;
    }

    @Override
    public void write(PacketBuffer buffer) {
      buffer.writeEnumValue(Type.MIN);
      buffer.writeVarInt(min);
    }

    @Override
    public void write(JsonObject json) {
      json.addProperty(KEY_MIN, min);
    }
  }

  /**
   * Predicate to match a maximum level or lower
   */
  private static class Max extends LevelPredicate {
    private final int max;

    private Max(int max) {
      this.max = max;
    }

    @Override
    public boolean test(int value) {
      return value <= max;
    }

    @Override
    public void write(PacketBuffer buffer) {
      buffer.writeEnumValue(Type.MAX);
      buffer.writeVarInt(max);
    }

    @Override
    public void write(JsonObject json) {
      json.addProperty(KEY_MAX, max);
    }
  }

  /**
   * Predicate to match a value between two numbers. Really no idea why you would want this, but included for completion
   */
  private static class Range extends LevelPredicate {
    private final int min, max;

    private Range(int min, int max) {
      this.min = min;
      this.max = max;
    }

    @Override
    public boolean test(int value) {
      return value <= max && value >= min;
    }

    @Override
    public void write(PacketBuffer buffer) {
      buffer.writeEnumValue(Type.MAX);
      buffer.writeVarInt(min);
      buffer.writeVarInt(max);
    }

    @Override
    public void write(JsonObject json) {
      json.addProperty(KEY_MIN, min);
      json.addProperty(KEY_MAX, max);
    }
  }

  /** All valid level predicate types */
  private enum Type {
    MIN,
    MAX,
    RANGE;

    private final String name = name().toLowerCase(Locale.US);

    /**
     * Gets the name of this type
     * @return  Type name
     */
    public String getName() {
      return name;
    }

    /**
     * Gets a predicate type for the given name
     * @param name  Name to check
     * @return  Value, or null if missing
     */
    @Nullable
    public static Type byName(String name) {
      for (Type type : values()) {
        if (type.getName().equals(name)) {
          return type;
        }
      }
      return null;
    }
  }
}
