package knightminer.inspirations.library.recipe.cauldron;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.DecoderException;
import knightminer.inspirations.library.recipe.cauldron.ingredient.ICauldronIngredient;
import knightminer.inspirations.library.recipe.cauldron.ingredient.ICauldronIngredientSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

/**
 * Registry that helps with registering, serializing, and deserializing cauldron properties
 */
public class CauldronIngredients {
  private static final String KEY_TYPE = "type";
  private static final BiMap<ResourceLocation, ICauldronIngredientSerializer<?>> INGREDIENTS = HashBiMap.create();

  /* Public constants */



  /**
   * Registers a new content type
   * @param name  Name
   * @param type  Type to register
   */
  public static void register(ResourceLocation name, ICauldronIngredientSerializer<?> type) {
    if (INGREDIENTS.containsKey(name)) {
      throw new IllegalArgumentException("Duplicate cauldron ingredient serializer '" + name + "'");
    }
    INGREDIENTS.put(name, type);
  }

  /**
   * Gets the type for the given cauldron contents
   * @param contents  Contents object
   * @param <T>  Contents type class
   * @return  Type with the proper generics
   */
  @SuppressWarnings("unchecked")
  private static <T extends ICauldronIngredient> ICauldronIngredientSerializer<T> getSerializer(T contents) {
    return (ICauldronIngredientSerializer<T>)contents.getSerializer();
  }

  /**
   * Gets the name for a content type
   * @param serializer  Serializer name
   * @return  Type registry name
   */
  public static ResourceLocation getName(ICauldronIngredientSerializer<?> serializer) {
    ResourceLocation name = INGREDIENTS.inverse().get(serializer);
    if (name == null) {
      throw new IllegalArgumentException("Unregistered cauldron serializer");
    }
    return name;
  }

  /**
   * Converts the given contents to JSON
   * @param contents  Contents
   * @param <T>  Contents type
   * @return  JSON
   */
  public static <T extends ICauldronIngredient> JsonObject toJson(T contents) {
    JsonObject json = new JsonObject();
    ICauldronIngredientSerializer<T> serializer = getSerializer(contents);
    json.addProperty(KEY_TYPE, getName(serializer).toString());
    serializer.write(contents, json);
    return json;
  }

  /**
   * Reads the cauldron contents from JSON
   * @param json  JSON to read
   * @return  Cauldron contents
   */
  public static ICauldronIngredient read(JsonObject json) {
    ResourceLocation location = new ResourceLocation(JSONUtils.getString(json, KEY_TYPE));
    ICauldronIngredientSerializer<?> serializer = INGREDIENTS.get(location);
    if (serializer != null) {
      return serializer.read(json);
    }
    throw new JsonSyntaxException("Invalid cauldron ingredient type '" + location + "'");
  }

  /**
   * Writes the given contents to NBT
   * @param contents  Contents to write
   * @param buffer    Buffer instance
   */
  public static <T extends ICauldronIngredient> void write(T contents, PacketBuffer buffer) {
    ICauldronIngredientSerializer<T> serializer = getSerializer(contents);
    buffer.writeResourceLocation(getName(serializer));
    serializer.write(contents, buffer);
  }

  /**
   * Reads the given contents from NBT
   * @param buffer Buffer instance
   * @return  Cauldron contents
   */
  public static ICauldronIngredient read(PacketBuffer buffer) {
    ResourceLocation name = buffer.readResourceLocation();
    ICauldronIngredientSerializer<?> serializer = INGREDIENTS.get(name);
    if (serializer == null) {
      throw new DecoderException("Invalid cauldron ingredient type '" + name + "'");
    }

    return serializer.read(buffer);
  }
}
