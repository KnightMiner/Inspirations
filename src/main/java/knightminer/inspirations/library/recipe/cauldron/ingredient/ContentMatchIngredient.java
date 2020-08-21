package knightminer.inspirations.library.recipe.cauldron.ingredient;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.DecoderException;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.contenttype.CauldronContentType;
import knightminer.inspirations.library.recipe.cauldron.contenttype.MapContentType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * Type that can match a value or list of values. Only supports {@link MapContentType} at this time, but the base logic can be extracted if needed.
 * @param <C>  Content type to match
 * @param <T>  Value type
 */
public abstract class ContentMatchIngredient<C extends ICauldronContents,T> implements ICauldronIngredient {
  private final ICauldronIngredientSerializer<?> serializer;
  protected final MapContentType<C,T> type;

  @SuppressWarnings("WeakerAccess")
  protected ContentMatchIngredient(ICauldronIngredientSerializer<?> serializer, MapContentType<C,T> type) {
    this.serializer = serializer;
    this.type = type;
  }

  /**
   * Creates an instance from the given type and value
   * @param type   Type
   * @param value  Value
   * @param <C>    Content type
   * @param <T>    Value type
   * @return  Ingredient
   */
  public static <C extends ICauldronContents,T> ContentMatchIngredient<C,T> of(MapContentType<C,T> type, T value) {
    return new Single<>(Serializer.GENERIC, type, value);
  }

  /**
   * Creates an instance from the given type and values
   * @param type    Type
   * @param values  Values
   * @param <C>     Content type
   * @param <T>     Value type
   * @return  Ingredient
   */
  public static <C extends ICauldronContents,T> ContentMatchIngredient<C,T> of(MapContentType<C,T> type, Collection<T> values) {
    return new Multi<>(Serializer.GENERIC, type, ImmutableSet.copyOf(values));
  }

  /**
   * Creates an instance from the given serializer and value
   * @param serializer  Serializer to use
   * @param value       Value
   * @param <C>         Content type
   * @param <T>         Value type
   * @return  Ingredient
   */
  public static <C extends ICauldronContents,T> ContentMatchIngredient<C,T> of(Serializer<C,T> serializer, T value) {
    return new Single<>(serializer, Objects.requireNonNull(serializer.type), value);
  }

  /**
   * Creates an instance from the given serializer and values
   * @param serializer  Serializer to use
   * @param values      Values
   * @param <C>         Content type
   * @param <T>         Value type
   * @return  Ingredient
   */
  public static <C extends ICauldronContents,T> ContentMatchIngredient<C,T> of(Serializer<C,T> serializer, Collection<T> values) {
    return new Multi<>(serializer, Objects.requireNonNull(serializer.type), ImmutableSet.copyOf(values));
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
    return contents.as(type)
                   .map(type::getValue)
                   .map(this::testValue)
                   .orElse(false);
  }

  @Override
  public ICauldronIngredientSerializer<?> getSerializer() {
    return serializer;
  }

  /** Matches a single value */
  private static class Single<C extends ICauldronContents, T> extends ContentMatchIngredient<C,T> {
    private final T value;
    private Single(ICauldronIngredientSerializer<?> serializer, MapContentType<C,T> type, T value) {
      super(serializer, type);
      this.value = value;
    }

    @Override
    protected boolean testValue(T value) {
      return this.value.equals(value);
    }

    @Override
    protected void write(JsonObject json) {
      json.addProperty(type.getKey(), type.getName(value));
    }

    @Override
    protected void write(PacketBuffer buffer) {
      buffer.writeString(type.getName(value));
    }
  }

  /** Matches from a set */
  private static class Multi<C extends ICauldronContents, T> extends ContentMatchIngredient<C,T> {
    private final Set<T> values;
    private Multi(ICauldronIngredientSerializer<?> serializer, MapContentType<C,T> type, Set<T> values) {
      super(serializer, type);
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
        array.add(type.getName(value));
      }
      json.add(type.getKey(), array);
    }

    @Override
    protected void write(PacketBuffer buffer) {
      buffer.writeVarInt(values.size());
      for (T value : values) {
        buffer.writeString(type.getName(value));
      }
    }
  }

  public static class Serializer<C extends ICauldronContents,T> implements ICauldronIngredientSerializer<ContentMatchIngredient<C,T>> {
    /**
     * Generic recipe serializer, requires both JSON and packets to include the contents type
     */
    public static final Serializer<?,?> GENERIC = new Serializer<>();

    @Nullable
    private final MapContentType<C,T> type;

    /**
     * Creates a new serializer using the given type
     * @param type  Serializer type
     */
    public Serializer(MapContentType<C,T> type) {
      this.type = type;
    }

    /**
     * Creates a new generic serializer
     */
    private Serializer() {
      this.type = null;
    }

    /**
     * Helper to get a single value from a string
     * @param type       Content type
     * @param name       Value name
     * @param <C>  Type of contents
     * @param <T>  Type of value
     * @return  Content match ingredient
     */
    private static <C extends ICauldronContents, T> ContentMatchIngredient<C,T> getSingle(MapContentType<C,T> type, String name) {
      T value = type.getEntry(name);
      if (value == null) {
        throw new JsonSyntaxException("Invalid value '" + name + "' for type " + type);
      }
      return of(type, value);
    }

    /**
     * Gets a type from the given name and for the given exception function
     * @param name       Type name
     * @param exception  Exception function
     * @return  Type instance
     * @throws RuntimeException  if the type is missing or the wrong class type
     */
    private static <C extends ICauldronContents, T> MapContentType<C,T> getType(ResourceLocation name, Function<String,RuntimeException> exception) {
      CauldronContentType<?> type = CauldronContentTypes.get(name);
      // must exist
      if (type == null) {
        throw exception.apply("Unknown cauldron content type '" + name + "'");
      }
      // must match type
      if (!(type instanceof MapContentType)) {
        throw exception.apply("Cauldron content type '" + name + "' does not support content match");
      }
      // only used by generic type, so type has ?,? generics
      //noinspection unchecked
      return (MapContentType<C,T>) type;
    }


    /**
     * Helper to get a list of values from a string
     * @param type       Content type
     * @param names      Value names
     * @param exception  Exception function
     * @param <C>  Type of contents
     * @param <T>  Type of value
     * @return  Content match ingredient
     */
    private static <C extends ICauldronContents, T> ContentMatchIngredient<C,T> getList(MapContentType<C,T> type, List<String> names, Function<String,RuntimeException> exception) {
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
        return of(type, values.get(0));
      }
      return of(type, values);
    }

    @Override
    public ContentMatchIngredient<C,T> read(JsonObject json) {
      // use the instance type if present
      MapContentType<C,T> type = this.type;
      // if generic, find a type using the match key
      if (type == null) {
        type = getType(new ResourceLocation(JSONUtils.getString(json, "match")), JsonSyntaxException::new);
      }

      // actual element can be a string or array
      JsonElement element = JsonHelper.getElement(json, type.getKey());

      // single name
      if (element.isJsonPrimitive()) {
        return getSingle(type, element.getAsString());
      }

      // array of names
      if (element.isJsonArray()) {
        List<String> names = JsonHelper.parseList(element.getAsJsonArray(), "names", JSONUtils::getString, Function.identity());
        return getList(type, names, JsonSyntaxException::new);
      }

      // error
      throw new JsonSyntaxException("Invalid '" + type.getKey() + "', must be a single value or an array");
    }

    @Override
    public void write(ContentMatchIngredient<C,T> ingredient, JsonObject json) {
      if (this.type == null) {
        json.addProperty("match", CauldronContentTypes.getName(ingredient.type).toString());
      }
      ingredient.write(json);
    }

    @Override
    public ContentMatchIngredient<C,T> read(PacketBuffer buffer) {
      // use the instance type if present
      MapContentType<C,T> type = this.type;
      // if generic, find a type using the match key
      if (type == null) {
        type = getType(buffer.readResourceLocation(), DecoderException::new);
      }

      // read all values from the buffer
      int size = buffer.readVarInt();
      List<String> names = new ArrayList<>(size);
      for (int i = 0; i < size; i++) {
        names.add(buffer.readString());
      }

      // parse list
      return getList(type, names, DecoderException::new);
    }

    @Override
    public void write(ContentMatchIngredient<C,T> ingredient, PacketBuffer buffer) {
      // only write the type to the buffer if this is the generic type
      if (this.type == null) {
        buffer.writeResourceLocation(CauldronContentTypes.getName(ingredient.type));
      }
      ingredient.write(buffer);
    }
  }
}
