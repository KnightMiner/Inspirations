package knightminer.inspirations.library.recipe.cauldron.ingredient;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.DecoderException;
import knightminer.inspirations.library.recipe.cauldron.contents.CauldronContentType;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import slimeknights.mantle.util.JsonHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Type that can match a value or list of values.
 * @param <T>  Value type
 */
public abstract class ContentMatchIngredient<T> implements ICauldronIngredient {
  protected final Serializer<T> serializer;

  @SuppressWarnings("WeakerAccess")
  protected ContentMatchIngredient(Serializer<T> serializer) {
    this.serializer = serializer;
  }

  /**
   * Checks that the given value matches
   * @param value  Value to check
   * @return  True if the value matches
   */
  protected abstract boolean testValue(T value);

  /**
   * Writes the fluid ingredient to JSON
   * @param json  JSON to write
   */
  protected abstract void write(JsonObject json);

  /**
   * Writes the fluid ingredient to the packet buffer
   * @param buffer  Buffer instance
   */
  protected abstract void write(PacketBuffer buffer);

  @Override
  public boolean test(ICauldronContents contents) {
    return contents.get(serializer.type)
                   .map(this::testValue)
                   .orElse(false);
  }

  @Override
  public Serializer<?> getSerializer() {
    return serializer;
  }

  /** Matches a single value */
  private static class Single<T> extends ContentMatchIngredient<T> {
    private final T value;
    private List<ICauldronContents> displayValue;
    private Single(Serializer<T> serializer, T value) {
      super(serializer);
      this.value = value;
    }

    @Override
    protected boolean testValue(T value) {
      return this.value.equals(value);
    }

    @Override
    protected void write(JsonObject json) {
      json.addProperty(serializer.type.getKey(), serializer.type.getName(value));
    }

    @Override
    protected void write(PacketBuffer buffer) {
      buffer.writeVarInt(1);
      buffer.writeString(serializer.type.getName(value));
    }

    @Override
    public List<ICauldronContents> getMatchingContents() {
      if (displayValue == null) {
        displayValue = Collections.singletonList(serializer.type.of(value));
      }
      return displayValue;
    }
  }

  /** Matches from a set */
  private static class Multi<T> extends ContentMatchIngredient<T> {
    private final Set<T> values;
    private List<ICauldronContents> displayValues;
    private Multi(Serializer<T> serializer, Set<T> values) {
      super(serializer);
      this.values = values;
    }

    @Override
    protected boolean testValue(T value) {
      return values.contains(value);
    }

    @Override
    protected void write(JsonObject json) {
      JsonArray array = new JsonArray();
      for (T value : values) {
        array.add(serializer.type.getName(value));
      }
      json.add(serializer.type.getKey(), array);
    }

    @Override
    protected void write(PacketBuffer buffer) {
      buffer.writeVarInt(values.size());
      for (T value : values) {
        buffer.writeString(serializer.type.getName(value));
      }
    }

    @Override
    public List<ICauldronContents> getMatchingContents() {
      if (displayValues == null) {
        displayValues = values.stream().map(serializer.type::of).collect(Collectors.toList());
      }
      return displayValues;
    }
  }

  /**
   * Generic serializer for a content match ingredient
   * @param <T>  Value type of serializer
   */
  public static class Serializer<T> implements ICauldronIngredientSerializer<ContentMatchIngredient<T>> {
    /**
     * Content type represented by this serializer
     */
    private final CauldronContentType<T> type;

    /**
     * Creates a new serializer using the given type
     * @param type  Serializer type
     */
    public Serializer(CauldronContentType<T> type) {
      this.type = type;
    }

    /**
     * Creates a new ingredient from the given value
     * @param value  Value
     * @return  Ingredient instance
     */
    public ContentMatchIngredient<T> of(T value) {
      return new Single<>(this, value);
    }

    /**
     * Creates a new ingredient from the given values
     * @param values  Values
     * @return  Ingredient instance
     */
    public ContentMatchIngredient<T> of(Collection<T> values) {
      return new Multi<>(this, ImmutableSet.copyOf(values));
    }

    /**
     * Helper to get a single value from a string
     * @param name       Value name
     * @return  Content match ingredient
     */
    private ContentMatchIngredient<T> getSingle(String name) {
      T value = type.getEntry(name);
      if (value == null) {
        throw new JsonSyntaxException("Invalid value '" + name + "' for type " + type);
      }
      return of(value);
    }

    /**
     * Helper to get a list of values from a string
     * @param names      Value names
     * @param exception  Exception function
     * @return  Content match ingredient
     */
    private ContentMatchIngredient<T> getList(List<String> names, Function<String,RuntimeException> exception) {
      List<T> values = new ArrayList<>();
      for (String name : names) {
        T value = type.getEntry(name);
        if (value == null) {
          throw exception.apply("Invalid value '" + name + "' for type " + type);
        }
        values.add(value);
      }
      // if a single element, save effort
      if (values.size() == 1) {
        return of(values.get(0));
      }
      return of(values);
    }

    @Override
    public ContentMatchIngredient<T> read(JsonObject json) {
      // actual element can be a string or array
      JsonElement element = JsonHelper.getElement(json, this.type.getKey());

      // single name
      if (element.isJsonPrimitive()) {
        return getSingle(element.getAsString());
      }

      // array of names
      if (element.isJsonArray()) {
        List<String> names = JsonHelper.parseList(element.getAsJsonArray(), "names", JSONUtils::getString, Function.identity());
        return getList(names, JsonSyntaxException::new);
      }

      // error
      throw new JsonSyntaxException("Invalid '" + this.type.getKey() + "', must be a single value or an array");
    }

    @Override
    public void write(ContentMatchIngredient<T> ingredient, JsonObject json) {
      ingredient.write(json);
    }

    @Override
    public ContentMatchIngredient<T> read(PacketBuffer buffer) {
      // read all values from the buffer
      int size = buffer.readVarInt();
      List<String> names = new ArrayList<>(size);
      for (int i = 0; i < size; i++) {
        names.add(buffer.readString());
      }

      // parse list
      return getList(names, DecoderException::new);
    }

    @Override
    public void write(ContentMatchIngredient<T> ingredient, PacketBuffer buffer) {
      ingredient.write(buffer);
    }
  }
}
