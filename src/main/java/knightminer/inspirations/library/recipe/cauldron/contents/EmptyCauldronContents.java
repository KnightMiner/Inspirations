package knightminer.inspirations.library.recipe.cauldron.contents;

import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contenttype.CauldronContentType;
import net.minecraft.util.ResourceLocation;

public class EmptyCauldronContents implements ICauldronContents {
  public static final EmptyCauldronContents INSTANCE = new EmptyCauldronContents();

  private EmptyCauldronContents() {}

  @Override
  public ResourceLocation getTextureName() {
    return NO_TEXTURE;
  }

  @Override
  public CauldronContentType<?> getType() {
    return CauldronContentTypes.EMPTY;
  }
}
