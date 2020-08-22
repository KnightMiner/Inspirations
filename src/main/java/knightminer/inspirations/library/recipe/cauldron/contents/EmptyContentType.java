package knightminer.inspirations.library.recipe.cauldron.contents;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Content type for the empty contents, exists to prevent errors from the empty contents having no type
 */
public class EmptyContentType extends CauldronContentType<Void> {
  @Override
  public ResourceLocation getTexture(Void type) {
    return CauldronContentType.NO_TEXTURE;
  }

  @Override
  public String getName(Void value) {
    return "empty";
  }

  @Nullable
  @Override
  public Void getEntry(String name) {
    return null;
  }

  @Override
  public void write(ICauldronContents contents, PacketBuffer buffer) {}

  @Override
  public EmptyCauldronContents read(PacketBuffer buffer) {
    return EmptyCauldronContents.INSTANCE;
  }

}
