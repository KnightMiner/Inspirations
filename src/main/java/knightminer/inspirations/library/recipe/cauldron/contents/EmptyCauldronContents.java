package knightminer.inspirations.library.recipe.cauldron.contents;

import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;

/**
 * Cauldron contents containing nothing. Used as a default in many cases.
 */
public class EmptyCauldronContents implements ICauldronContents {
  public static final EmptyCauldronContents INSTANCE = new EmptyCauldronContents();
  private EmptyCauldronContents() {}

  @Override
  public <T> Optional<T> get(CauldronContentType<T> type) {
    return Optional.empty();
  }

  @Override
  public CauldronContentType<?> getType() {
    return CauldronContentTypes.EMPTY;
  }

  @Override
  public ResourceLocation getTextureName() {
    return CauldronContentType.NO_TEXTURE;
  }

  @Override
  public int getTintColor() {
    return -1;
  }

  @Override
  public <T> boolean matches(CauldronContentType<T> type, T value) {
    return type == CauldronContentTypes.EMPTY;
  }

  @Override
  public boolean isSimple() {
    return true;
  }
}
