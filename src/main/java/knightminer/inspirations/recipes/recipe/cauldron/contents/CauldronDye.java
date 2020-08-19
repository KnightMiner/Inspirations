package knightminer.inspirations.recipes.recipe.cauldron.contents;

import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronDye;
import knightminer.inspirations.library.recipe.cauldron.contenttype.CauldronContentType;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;

/**
 * Standard implementation of {@link ICauldronDye}
 */
public class CauldronDye implements ICauldronDye {
  private final DyeColor dye;
  public CauldronDye(DyeColor dye) {
    this.dye = dye;
  }

  @Override
  public DyeColor getDye() {
    return dye;
  }

  @Override
  public ResourceLocation getTextureName() {
    return CauldronColor.TEXTURE;
  }

  @Override
  public CauldronContentType<?> getType() {
    return CauldronContentTypes.DYE;
  }
}
