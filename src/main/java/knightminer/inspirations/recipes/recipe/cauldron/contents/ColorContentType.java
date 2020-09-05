package knightminer.inspirations.recipes.recipe.cauldron.contents;

import io.netty.handler.codec.DecoderException;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.cauldron.contents.CauldronContentType;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Content type for colors in the cauldron
 */
public class ColorContentType extends CauldronContentType<Integer> {
  private static final ResourceLocation TEXTURE_NAME = Inspirations.getResource("color");
  private static final String TRANSLATION_KEY = Util.makeTranslationKey("cauldron_contents", Inspirations.getResource("color"));

  @Override
  public String getKey() {
    return "color";
  }

  @Override
  public ResourceLocation getTexture(Integer value) {
    return TEXTURE_NAME;
  }

  @Override
  public int getColor(Integer value) {
    return value;
  }

  @Override
  public ITextComponent getDisplayName(Integer value) {
    return new TranslationTextComponent(TRANSLATION_KEY, getName(value));
  }

  @Override
  public String getName(Integer value) {
    return String.format("%06X", value);
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
  public ICauldronContents read(PacketBuffer buffer) {
    return of(buffer.readInt());
  }

  @Override
  public void write(ICauldronContents contents, PacketBuffer buffer) {
    Optional<Integer> optional = contents.get(this);
    if (optional.isPresent()) {
      buffer.writeInt(optional.get());
    } else {
      throw new DecoderException("Invalid class type for cauldron contents");
    }
  }
}
