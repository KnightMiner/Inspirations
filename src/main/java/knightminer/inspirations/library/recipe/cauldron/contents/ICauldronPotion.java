package knightminer.inspirations.library.recipe.cauldron.contents;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;

/**
 * Represents a cauldron containing a fluid
 */
public interface ICauldronPotion extends ICauldronContents {
  /**
   * Gets the contained potion
   * @return  Cauldron potion
   */
  Potion getPotion();

  @Override
  default int getTintColor() {
    return PotionUtils.getPotionColor(getPotion());
  }
}
