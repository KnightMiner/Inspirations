package knightminer.inspirations.library.recipe.cauldron.util;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;

import java.util.function.IntPredicate;

import static knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe.MAX;

/**
 * Predicate to match a cauldron level
 */
public class LevelPredicate implements IntPredicate {
  private static final String KEY_MIN = "min";
  private static final String KEY_MAX = "max";
  /** Cache of all levels, first key is min, second is max */
  private static final LevelPredicate[][] CACHE = new LevelPredicate[MAX + 1][MAX + 1];

  private final int min;
  private final int max;

  /** No constructor to prevent extension, it will not work with the read methods */
  private LevelPredicate(int min, int max) {
    this.min = min;
    this.max = max;
  }

  /**
   * Gets a level predicate for a minimum number
   * @param min  Minimum level value, must be between 0 and 3
   * @return  Level predicate
   */
  public static LevelPredicate min(int min) {
    return range(min, MAX);
  }

  /**
   * Gets a level predicate for a maximum number
   * @param max  Maximum level value, must be between 0 and 3
   * @return  Level predicate
   */
  public static LevelPredicate max(int max) {
    return range(0, max);
  }

  /**
   * Gets a level predicate for a range of values
   * @param min  Minumum level value
   * @param max  Maximum level value
   * @return  Level predicate
   */
  public static LevelPredicate range(int min, int max) {
    // validate inputs
    if (min < 0 || min > MAX) {
      throw new IllegalArgumentException("Invalid minimum level " + min + ", must be between 0 and 3");
    }
    if (max < 0 || max > MAX) {
      throw new IllegalArgumentException("Invalid maximum level " + max + ", must be between 0 and 3");
    }
    if (min > max) {
      throw new IllegalArgumentException("Minimum cannot be larger than maximum");
    }
    //cache a new one if missing
    if (CACHE[min][max] == null) {
      CACHE[min][max] = new LevelPredicate(min, max);
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
    int min = JSONUtils.getInt(json, KEY_MIN, 0);
    int max = JSONUtils.getInt(json, KEY_MAX, MAX);
    return range(min, max);
  }

  /**
   * Reads a level predicate from the packet buffer
   * @param buffer  Buffer instance
   * @return  Level predicate
   */
  public static LevelPredicate read(PacketBuffer buffer) {
    int min = buffer.readVarInt();
    int max = buffer.readVarInt();
    return range(min, max);
  }

  @Override
  public boolean test(int value) {
    return value >= min && value <= max;
  }

  /**
   * Gets the minimum number that matches for display. Use {@link #test(int)} for testing
   * @return  Minimum match
   */
  public int getMin() {
    return min;
  }

  /**
   * Gets the maximum number that matches for display. Use {@link #test(int)} for testing
   * @return  Maximum match
   */
  public int getMax() {
    return max;
  }

  /**
   * Writes this to the packet buffer
   * @param buffer  Buffer instance
   */
  public void write(PacketBuffer buffer) {
    buffer.writeVarInt(min);
    buffer.writeVarInt(max);
  }

  /**
   * Writes this to JSON
   */
  public JsonObject toJson() {
    JsonObject object = new JsonObject();
    if (min > 0) {
      object.addProperty(KEY_MIN, min);
    }
    if (max < MAX) {
      object.addProperty(KEY_MAX, max);
    }
    return object;
  }

  @Override
  public String toString() {
    return String.format("LevelPredicate[%d,%d]", min, max);
  }
}
