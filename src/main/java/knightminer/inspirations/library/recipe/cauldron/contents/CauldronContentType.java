package knightminer.inspirations.library.recipe.cauldron.contents;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.netty.handler.codec.DecoderException;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.recipes.recipe.cauldron.contents.CauldronContents;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Represents a type of contents that can be stored in the cauldron
 * @param <T> Type of values contained in this type
 */
public abstract class CauldronContentType<T> {
  /** Resource location meaning no texture exists. Basically a copy of {@link net.minecraft.client.renderer.texture.MissingTextureSprite#getLocation()} that is server safe */
  public static final ResourceLocation NO_TEXTURE = Inspirations.getResource("missingno");

  private final Map<T, ICauldronContents> cache = new HashMap<>();
  private final Map<ICauldronContents,T> valueOverrides = new HashMap<>();
  private final Function<T, ICauldronContents> constructor = val -> new CauldronContents<>(this, val);

  /**
   * Gets a value of the given type
   * @param value  Type of value
   * @return  Value to fetch
   */
  public ICauldronContents of(T value) {
    return cache.computeIfAbsent(value, constructor);
  }


  /* Overrides */

  /**
   * Causes the given instance to return a value for this type.
   * @param instance  Contents instance for override
   * @param value     Value supplier
   */
  public void setValue(ICauldronContents instance, T value) {
    if (instance.getType() == this) {
      throw new IllegalArgumentException("Attempted to register override within the same type");
    }
    valueOverrides.put(instance, value);
  }

  /**
   * Causes a specific value from this function to instead return the given instance. Need to call {@link #setValue(ICauldronContents, Object)} before using
   * @param value     Value being replaced
   * @param instance  Instance to use for override
   */
  public void setResult(T value, ICauldronContents instance) {
    Optional<T> optional = instance.get(this);
    if (optional.isPresent()) {
      if (!optional.get().equals(value)) {
        throw new IllegalArgumentException("Override contents does not match the value type");
      }
    } else {
      // add a value override if missing
      valueOverrides.put(instance, value);
    }
    // just add it to the cache, so it will be fetched later
    cache.put(value, instance);
  }

  /**
   * Gets the overridden value for the given instance
   * @param instance  Instance
   * @return  Optional of override value, or empty if no override exists
   */
  public Optional<T> getOverrideValue(ICauldronContents instance) {
    return Optional.ofNullable(valueOverrides.get(instance));
  }


  /* Display methods */

  /**
   * Gets the texture name for the given type.
   * The name will be passed through {@link knightminer.inspirations.recipes.RecipesClientEvents#cauldronTextures} to convert from a generic location to a texture path
   * @param value  Value to fetch texture
   * @return  Texture location
   */
  public abstract ResourceLocation getTexture(T value);

  /**
   * Gets the tint color for the given type, used for tint indexes
   * @param value  Value
   * @return  Tint index
   */
  public int getColor(T value) {
    return -1;
  }

  /**
   * Gets the name from the given value
   * @param value  Value to fetch name
   * @return  Value name
   */
  public abstract ITextComponent getDisplayName(T value);

  /**
   * Gets tooltip information for these contents
   * @param value        Contents value
   * @param tooltip      Existing tooltip
   * @param tooltipFlag  Tooltip context flag
   */
  public void addInformation(T value, List<ITextComponent> tooltip, ITooltipFlag tooltipFlag) {}

  /**
   * Gets the mod ID for the given value
   * @param value  Value
   * @return  Mod ID
   */
  @Nullable
  public String getModId(T value) {
    return null;
  }


  /* Serializing and deserializing */

  /**
   * Gets the name of the given value for writing to JSON and NBT
   * @param value  Value
   * @return  Name of the value
   */
  public abstract String getName(T value);

  /**
   * Gets the base key used for ingredient JSON
   * @return  JSON and NBT key
   */
  public String getKey() {
    return "name";
  }

  /**
   * Reads the given type from NBT
   * @param tag  NBT tag
   * @return  Read value
   */
  @Nullable
  public abstract T read(CompoundNBT tag);

  /**
   * Writes the given type to NBT
   * @param tag       NBT tag
   */
  public void write(T value, CompoundNBT tag) {
    tag.putString(getKey(), getName(value));
  }

  /**
   * Gets the value from a JSON element
   * @param element  JSON element
   * @param key      Key to get
   * @return  Value
   */
  public abstract T getValue(JsonElement element, String key);

  /**
   * Writes the given type into a standalone JSON element
   * @param value  Value to write
   * @return  JsonElement representing this value
   */
  public JsonElement toJson(T value) {
    return new JsonPrimitive(getName(value));
  }

  /**
   * Reads the given type from JSON
   * @param json  JSON object
   * @return  Read value
   * @throws com.google.gson.JsonSyntaxException if the JSON is invalid
   */
  public T read(JsonObject json) {
    String key = getKey();
    return getValue(JsonHelper.getElement(json, key), key);
  }

  /**
   * Writes the given type to JSON
   * @param value  Value to write
   * @param json   JSON object
   */
  public void write(T value, JsonObject json) {
    json.addProperty(getKey(), getName(value));
  }

  /**
   * Reads the given type from the packet buffer
   * @param buffer  Packet buffer
   * @return  Read value
   * @throws DecoderException if the type is invalid
   */
  public abstract T read(PacketBuffer buffer);

  /**
   * Writes the given type to the packet buffer
   * @param value    Value to write
   * @param buffer    Packet buffer
   */
  public abstract void write(T value, PacketBuffer buffer);

  @Override
  public String toString() {
    return String.format("CauldronContentType[%s]", CauldronContentTypes.getName(this));
  }
}
