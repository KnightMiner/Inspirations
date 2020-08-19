package knightminer.inspirations.library.recipe.cauldron.contents;

/**
 * Represents a cauldron containing an arbitrary color
 */
public interface ICauldronColor extends ICauldronContents {
  /**
   * Gets the color contained in these contents
   * @return  Contents color
   */
  int getColor();

  @Override
  default int getTintColor() {
    return getColor();
  }
}
