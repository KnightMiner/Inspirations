package knightminer.inspirations.library.recipe.cauldron.contents;

import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * Base interface for all cauldron contents
 */
public interface ICauldronContents {
  /**
   * Gets this value as the given type
   * @param type  Type to get
   * @param <T>  Type of return
   * @return  Value, or empty optional if this does not have the given type
   */
  <T> Optional <T> get(CauldronContentType<T> type);

  /**
   * Gets the main type of these contents, used for serializing
   * @return  Main content type
   */
  CauldronContentType<?> getType();


  /* Display */

  /**
   * Gets the name of the texture for these contents. Should generally delegate to the type.
   * The name will be passed through {@link knightminer.inspirations.recipes.RecipesClientEvents#cauldronTextures} to convert from a generic location to a texture path
   * @return  Texture location for this contents
   */
  ResourceLocation getTextureName();

  /**
   * Gets the color for this content type for tinting. Should generally delegate to the type
   * @return  Tint color
   */
  int getTintColor();

  /**
   * Gets the text component to display for this contents name
   * @return  Name text component
   */
  ITextComponent getDisplayName();

  /**
   * Gets tooltip information for these contents
   * @param tooltip      Existing tooltip
   * @param tooltipFlag  Tooltip context flag
   */
  default void addInformation(List<ITextComponent> tooltip, ITooltipFlag tooltipFlag) {}

  /**
   * Gets the relevant mod ID for this contents
   * @return  Contents mod ID
   */
  @Nullable
  default String getModId() {
    return null;
  }

  /**
   * Gets the unique name relative to the ingredient type
   * @return  Resource searching name
   */
  String getName();


  /* Mapping */

  /**
   * Checks if these cauldron contents match the given arguments. Use this for {@link #equals(Object)} implementations
   * @param type   Content type
   * @param value  Value of the content
   * @param <T>  Content class
   * @return True if they match
   */
  <T> boolean matches(CauldronContentType<T> type, T value);

  /**
   * For consistency, hash code should be {@code 31 * type.hashCode() + value.hashCode()}
   * @return  Hash code for the given type and value
   */
  @Override
  int hashCode();

  /**
   * Checks if the contents contain the given type
   * @param type   Content type
   * @param <T>  Content class
   * @return  True if get would return this value
   */
  default <T> boolean contains(CauldronContentType<T> type) {
    return get(type).isPresent();
  }

  /**
   * Checks if the contents contain the given value. Unlike {@link #matches(CauldronContentType, Object)}, supports overrides.
   * @param type   Content type
   * @param value  Value of the content
   * @param <T>  Content class
   * @return  True if get would return this value
   */
  default <T> boolean contains(CauldronContentType<T> type, T value) {
    return get(type).map(value::equals).orElse(false);
  }

  /**
   * If true, this is a simple content type, meaning it can be held in the vanilla cauldron
   * @return  True if the content type is simple
   */
  default boolean isSimple() {
    return contains(CauldronContentTypes.FLUID, Fluids.WATER);
  }
}
