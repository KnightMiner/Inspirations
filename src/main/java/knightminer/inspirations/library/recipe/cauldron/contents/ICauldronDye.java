package knightminer.inspirations.library.recipe.cauldron.contents;

import net.minecraft.item.DyeColor;

/**
 * Represents a cauldron containing a specific dye
 */
public interface ICauldronDye extends ICauldronColor {
  /**
   * Gets the relevant dye for this color
   * @return  Dye color
   */
  DyeColor getDye();

  @Override
  default int getColor() {
    return getDye().getColorValue();
  }
}
