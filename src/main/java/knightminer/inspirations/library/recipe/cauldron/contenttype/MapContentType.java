package knightminer.inspirations.library.recipe.cauldron.contenttype;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.common.util.Constants.NBT;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Represents a type of contents that can be stored in the cauldron
 * @param <T>  Stored content type
 * @param <C>  {@link ICauldronContents} implementation for this type
 */
public abstract class MapContentType<C extends ICauldronContents, T> extends CauldronContentType<C> {
  private final Map<String, C> cache = new HashMap<>();
  private final Function<? super T, ? extends C> constructor;
  private final Function<C, T> valueGetter;
  private final String key;

  /**
   * Creates a new instance with a specific key name
   * @param clazz        Content type class for validation
   * @param constructor  Constructor to create contents from the type
   * @param valueGetter  Function to get the value from a content type
   * @param key          Key to use for serializing and deserializing
   */
  protected MapContentType(Class<C> clazz, Function<? super T,? extends C> constructor, Function<C,T> valueGetter, String key) {
    super(clazz);
    this.constructor = constructor;
    this.valueGetter = valueGetter;
    this.key = key;
  }

  @SuppressWarnings("WeakerAccess")
  protected MapContentType(Class<C> clazz, Function<? super T,? extends C> constructor, Function<C,T> valueGetter) {
    this(clazz, constructor, valueGetter, "name");
  }

  /* Creation methods */

  /**
   * Adds an override to this type, preventing the default constructor
   * @param value     Override to fetch
   * @param instance  Instance to use for override
   */
  @SuppressWarnings("unused")
  public void addOverride(T value, C instance) {
    cache.put(getName(value), instance);
  }

  /**
   * Gets a value of the given type
   * @param value  Type of value
   * @return  Value to fetch
   */
  public C of(T value) {
    return cache.computeIfAbsent(getName(value), name -> constructor.apply(value));
  }


  /* Utils */

  /**
   * Gets the value of the given contents
   * @param contents  Contents
   * @return  Value
   */
  public T getValue(C contents) {
    return valueGetter.apply(contents);
  }

  /**
   * Gets the key used for JSON and NBT
   * @return  JSON and NBT key
   */
  public String getKey() {
    return key;
  }

  /* Serializing and deserializing */

  /**
   * Gets the name of the given value
   * @param value  Value
   * @return  Name of the value
   */
  public abstract String getName(T value);

  /**
   * Gets the name of the given value
   * @param value  Value
   * @return  Name of the value
   */
  protected final String getName(C value) {
    return getName(getValue(value));
  }

  /**
   * Gets the entry for a given value
   * @param name  Name
   * @return  Entry, or null if missing
   */
  @Nullable
  public abstract T getEntry(String name);

  /**
   * Reads the given type from NBT
   * @param tag  Type of value
   * @return  Value to fetch
   */
  @Nullable
  @Override
  public C read(CompoundNBT tag) {
    if (tag.contains(key, NBT.TAG_STRING)) {
      T value = getEntry(tag.getString(key));
      if (value != null) {
        return of(value);
      }
    }
    return null;
  }

  @Override
  public void write(C contents, CompoundNBT tag) {
    tag.putString(key, getName(contents));
  }

  @Override
  public C read(JsonObject json) {
    String name = JSONUtils.getString(json, key);
    T value = getEntry(name);
    if (value == null) {
      throw new JsonSyntaxException("Invalid name '" + name + "' for type '" + CauldronContentTypes.getName(this) + "'");
    }
    return of(value);
  }

  @Override
  public void write(C contents, JsonObject json) {
    json.addProperty(key, getName(contents));
  }
}
