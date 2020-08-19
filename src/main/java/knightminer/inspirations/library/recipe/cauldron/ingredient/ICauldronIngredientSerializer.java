package knightminer.inspirations.library.recipe.cauldron.ingredient;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;

/**
 * Serializer for a cauldron ingredient type
 * @param <T>  Ingredient class
 */
public interface ICauldronIngredientSerializer<T extends ICauldronIngredient> {
  /**
   * Reads the ingredient from JSON
   * @param json  Json object
   * @return  Read ingredient
   */
  T read(JsonObject json);

  /**
   * Writes the ingredient to the packet buffer
   * @param ingredient  Ingredient to write
   * @param json  Json object
   */
  void write(T ingredient, JsonObject json);

  /**
   * Reads the ingredient from the packet buffer
   * @param buffer  Buffer instance
   * @return  Read ingredient
   */
  T read(PacketBuffer buffer);

  /**
   * Writes the ingredient to the packet buffer
   * @param ingredient  Ingredient to write
   * @param buffer      Buffer instance
   */
  void write(T ingredient, PacketBuffer buffer);
}
