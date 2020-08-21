package knightminer.inspirations.library.recipe.cauldron.util;

import com.google.gson.JsonObject;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.function.IntUnaryOperator;

import static knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe.MAX;

/**
 * Logic to update the level to a new value
 */
public abstract class LevelUpdate implements IntUnaryOperator {
  private static final String KEY_ADD = "add";
  private static final String KEY_SET = "set";
  /** Cache of add operations, ranges from -MAX to MAX */
  private static final LevelUpdate[] ADD_CACHE = new LevelUpdate[MAX + MAX + 1];
  /** Cache of set operations */
  private static final LevelUpdate[] SET_CACHE = new LevelUpdate[MAX + 1];

  /** No constructor to prevent extension, it will not work with the read methods */
  private LevelUpdate() {}

  /** Level update that returns the input */
  public static final LevelUpdate IDENTITY = new LevelUpdate() {
    @Override
    public void write(PacketBuffer buffer) {
      buffer.writeEnumValue(Type.IDENTITY);
    }

    @Override
    public int applyAsInt(int original) {
      return original;
    }

    @Override
    public JsonObject toJson() {
      return new JsonObject();
    }
  };
  static {
    // -3 (offset) + 3 = 0
    ADD_CACHE[MAX] = IDENTITY;
  }

  /**
   * Creates a new level update to add the specified amount
   * @param amount  Amount to add
   * @return  Level update
   */
  public static LevelUpdate add(int amount) {
    if (amount < -MAX || amount > MAX) {
      throw new IllegalArgumentException("Invalid amount " + amount + ", must be between -3 and 3");
    }
    // negatives are not array indexes
    int key = amount + MAX;
    if (ADD_CACHE[key] == null) {
      ADD_CACHE[key] = new Add(amount);
    }
    return ADD_CACHE[key];
  }

  /**
   * Creates a new level update to set the specified amount
   * @param amount  Amount to set
   * @return  Level update
   */
  public static LevelUpdate set(int amount) {
    if (amount < 0 || amount > MAX) {
      throw new IllegalArgumentException("Invalid amount " + amount + ", must be between 0 and 3");
    }
    if (SET_CACHE[amount] == null) {
      SET_CACHE[amount] = new Set(amount);
    }
    return SET_CACHE[amount];
  }

  /**
   * Reads a level update from JSON
   * @param json  JSON object
   * @return  Level predicate
   */
  public static LevelUpdate read(JsonObject json) {
    if (json.has(KEY_ADD)) {
      return new Add(JSONUtils.getInt(json, KEY_ADD));
    }
    if (json.has(KEY_SET)) {
      return new Set(JSONUtils.getInt(json, KEY_SET));
    }

    // neither? means identity
    return IDENTITY;
  }

  /**
   * Reads a level update from the packet buffer
   * @param buffer  Buffer instance
   * @return  Level predicate
   */
  public static LevelUpdate read(PacketBuffer buffer) {
    Type type = buffer.readEnumValue(Type.class);
    switch (type) {
      case IDENTITY: return IDENTITY;
      case SET: return new Set(buffer.readVarInt());
      case ADD: return new Add(buffer.readVarInt());
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
  protected void write(JsonObject json) {}

  /**
   * Writes this to JSON
   */
  public JsonObject toJson() {
    JsonObject object = new JsonObject();
    write(object);
    return object;
  }

  /**
   * Updater that sets the amount
   */
  private static class Set extends LevelUpdate {
    private final int amount;
    private Set(int amount) {
      this.amount = amount;
    }

    @Override
    public int applyAsInt(int original) {
      return amount;
    }

    @Override
    public void write(PacketBuffer buffer) {
      buffer.writeEnumValue(Type.SET);
      buffer.writeVarInt(amount);
    }

    @Override
    protected void write(JsonObject json) {
      json.addProperty(KEY_SET, amount);
    }
  }

  /**
   * Updater that adds to the amount
   */
  private static class Add extends LevelUpdate {
    private final int amount;
    private Add(int amount) {
      this.amount = amount;
    }

    @Override
    public int applyAsInt(int original) {
      return MathHelper.clamp(original + amount, 0, 3);
    }

    @Override
    public void write(PacketBuffer buffer) {
      buffer.writeEnumValue(Type.ADD);
      buffer.writeVarInt(amount);
    }

    @Override
    protected void write(JsonObject json) {
      json.addProperty(KEY_ADD, amount);
    }
  }

  /** All valid level update types */
  private enum Type {
    IDENTITY,
    SET,
    ADD;

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
