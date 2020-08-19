package knightminer.inspirations.library.recipe.cauldron.contenttype;

import io.netty.handler.codec.DecoderException;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Cauldron content type based on an {@link IStringSerializable} object, typically an enum
 * @param <T>  Type class
 * @param <C>  Contents class
 */
public class NamedContentType<C extends ICauldronContents, T extends IStringSerializable> extends MapContentType<C, T> {
  private final Function<String, ? extends T> lookup;

  /**
   * Creates a new type instance
   * @param clazz        Contents class
   * @param constructor  Contents constructor
   * @param lookup       Function to lookup a value from a string
   * @param valueGetter  Function to get the value from a content type
   */
  public NamedContentType(Class<C> clazz, Function<? super T,? extends C> constructor, Function<String,? extends T> lookup, Function<C,T> valueGetter) {
    super(clazz, constructor, valueGetter);
    this.lookup = lookup;
  }

  @Override
  public String getName(T value) {
    return value.getString();
  }

  @Nullable
  @Override
  public T getEntry(String name) {
    return lookup.apply(name);
  }

  @Override
  public C read(PacketBuffer buffer) {
    String name = buffer.readString(Short.MAX_VALUE);
    T value = lookup.apply(name);
    if (value != null) {
      return of(value);
    }
    throw new DecoderException("Invalid name '" + name + "' for type " + this);
  }

  @Override
  public void write(C contents, PacketBuffer buffer) {
    buffer.writeString(getName(contents));
  }
}
