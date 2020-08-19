package knightminer.inspirations.recipes.recipe.cauldron.contents;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronColor;
import knightminer.inspirations.library.recipe.cauldron.contenttype.CauldronContentType;
import net.minecraft.util.ResourceLocation;

/**
 * Standard implementation of {@link ICauldronColor}
 */
public class CauldronColor implements ICauldronColor {
  public static final ResourceLocation TEXTURE = Inspirations.getResource("block/fluid_dye");

  private final int color;
  public CauldronColor(int color) {
    this.color = color;
  }

  @Override
  public int getColor() {
    return color;
  }

  @Override
  public ResourceLocation getTextureName() {
    return CauldronColor.TEXTURE;
  }

  @Override
  public CauldronContentType<?> getType() {
    return CauldronContentTypes.COLOR;
  }
}
