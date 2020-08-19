package knightminer.inspirations.recipes.recipe.cauldron.contents;

import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronColor;
import knightminer.inspirations.library.recipe.cauldron.contenttype.MapContentType;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nullable;

/**
 * Content type for {@link CauldronColor}
 */
public class ColorContentType extends MapContentType<ICauldronColor, Integer> {
  public ColorContentType() {
    super(ICauldronColor.class, CauldronColor::new, ICauldronColor::getColor, "color");
  }

  @Override
  public String getName(Integer value) {
    return Integer.toHexString(value);
  }

  @Nullable
  @Override
  public Integer getEntry(String name) {
    try {
      return Integer.parseInt(name, 16);
    }
    catch (NumberFormatException e) {
      return null;
    }
  }

  @Override
  public ICauldronColor read(PacketBuffer buffer) {
    return of(buffer.readInt());
  }

  @Override
  public void write(ICauldronColor contents, PacketBuffer buffer) {
    buffer.writeInt(contents.getColor());
  }
}
