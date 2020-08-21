package knightminer.inspirations.library.recipe.cauldron;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.DecoderException;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronColor;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronDye;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronPotion;
import knightminer.inspirations.library.recipe.cauldron.contenttype.MapContentType;
import knightminer.inspirations.library.recipe.cauldron.ingredient.ContentMatchIngredient;
import knightminer.inspirations.library.recipe.cauldron.ingredient.ContentTypeIngredient;
import knightminer.inspirations.library.recipe.cauldron.ingredient.FluidCauldronIngredient;
import knightminer.inspirations.library.recipe.cauldron.ingredient.ICauldronIngredient;
import knightminer.inspirations.library.recipe.cauldron.ingredient.ICauldronIngredientSerializer;
import net.minecraft.item.DyeColor;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Potion;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

/**
 * Registry that helps with registering, serializing, and deserializing cauldron properties
 */
public class CauldronIngredients {
  private static final String KEY_TYPE = "type";
  private static final BiMap<ResourceLocation, ICauldronIngredientSerializer<?>> INGREDIENTS = HashBiMap.create();

  /* Public constants */

  /** Generic content match serializer */
  public static final ContentMatchIngredient.Serializer<?,?> MATCH = register("match_content", ContentMatchIngredient.Serializer.GENERIC);
  /** Generic content match serializer */
  public static final ContentTypeIngredient.Serializer TYPE = register("content_type", new ContentTypeIngredient.Serializer());

  /** Fluid content match serializer */
  public static final FluidCauldronIngredient.Serializer FLUID = register("fluid", new FluidCauldronIngredient.Serializer());
  /** Color content match serializer */
  public static final ContentMatchIngredient.Serializer<ICauldronColor,Integer> COLOR = registerMatch(CauldronContentTypes.COLOR);
  /** Dye content match serializer */
  public static final ContentMatchIngredient.Serializer<ICauldronDye,DyeColor> DYE = registerMatch(CauldronContentTypes.DYE);
  /** Fluid content match serializer */
  public static final ContentMatchIngredient.Serializer<ICauldronPotion,Potion> POTION = registerMatch(CauldronContentTypes.POTION);

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
   * Helper to register static types
   * @param name  Inspirations namespace name
   * @param type  Type to register
   * @param <T>   Output type
   * @return  Registered type
   */
  private static <T extends ICauldronIngredientSerializer<?>> T register(String name, T type) {
    register(Inspirations.getResource(name), type);
    return type;
  }

  /**
   * Registers a generic content match type for the given type
   * @param mapType  Map type instance
   * @param <C>  Content type
   * @param <T>  Map value type
   * @return  Registered serializer
   */
  public static <C extends ICauldronContents, T> ContentMatchIngredient.Serializer<C,T> registerMatch(MapContentType<C,T> mapType) {
    ContentMatchIngredient.Serializer<C,T> serializer = new ContentMatchIngredient.Serializer<>(mapType);
    register(CauldronContentTypes.getName(mapType), serializer);
    return serializer;
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
