package knightminer.inspirations.library.recipe.cauldron.contents;

import knightminer.inspirations.library.recipe.cauldron.contenttype.CauldronContentType;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;

/**
 * Base interface for all cauldron contents
 */
public interface ICauldronContents {
  /**
   * Copy of {@link net.minecraft.client.renderer.texture.MissingTextureSprite#getLocation} for reference on a server.
   * Used as an error state so {@link #getTextureName()} will not crash a server
   */
  ResourceLocation NO_TEXTURE = new ResourceLocation("missingno");

  /* Display */

  /**
   * Gets the name of the texture for these contents.
   * Note that this method exists on the server. Use {@link net.minecraftforge.fml.DistExecutor} and return {@link #NO_TEXTURE} on the server if needed to prevent clientside access
   * @return  Texture location for this contents
   */
  ResourceLocation getTextureName();

  /**
   * Gets the color for this content type for tinting
   * @return  Tint color
   */
  default int getTintColor() {
    return -1;
  }


  /* Serializing */

  /**
   * Gets the type of this contents
   * @return  Content type
   */
  CauldronContentType<?> getType();


  /* Helper methods */

  /**
   * Checks if this is the given type
   * @param type  Type to check
   * @return  True if it matches
   */
  default boolean is(CauldronContentType<?> type) {
    return type.matches(this);
  }

  /**
   * Gets this type as the given value
   * @param type  Type to get
   * @param <C>   Content type
   * @return  Optional of the given type, empty if wrong type
   */
  default <C extends ICauldronContents> Optional<C> as(CauldronContentType<C> type) {
    return type.get(this);
  }
}
