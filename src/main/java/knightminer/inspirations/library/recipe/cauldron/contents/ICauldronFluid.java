package knightminer.inspirations.library.recipe.cauldron.contents;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;

/**
 * Represents a cauldron containing a specific fluid
 */
public interface ICauldronFluid extends ICauldronContents {
  /**
   * Gets the fluid relevant to this cauldron contents
   * @return  Relevant fluid
   */
  Fluid getFluid();

  @Override
  default int getTintColor() {
    return getFluid().getAttributes().getColor();
  }

  @Override
  default ResourceLocation getTextureName() {
    return getFluid().getAttributes().getStillTexture();
  }
}
