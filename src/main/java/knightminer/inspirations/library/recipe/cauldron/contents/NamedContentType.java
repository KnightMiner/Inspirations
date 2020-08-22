package knightminer.inspirations.library.recipe.cauldron.contents;

import net.minecraft.util.IStringSerializable;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Cauldron content type based on an {@link IStringSerializable} object, typically an enum
 * @param <T>  Type class
 */
public abstract class NamedContentType<T extends IStringSerializable> extends CauldronContentType<T> {
  private final Function<String, ? extends T> lookup;

  /**
   * Creates a new type instance
   * @param lookup       Function to lookup a value from a string
   */
  public NamedContentType(Function<String,? extends T> lookup) {
    this.lookup = lookup;
  }

  /**
   * Gets the name of the given value for writing to JSON and NBT
   * @param value  Value
   * @return  Name of the value
   */
  @Override
  public String getName(T value) {
    return value.getString();
  }

  /**
   * Gets the entry for a given value
   * @param name  Name
   * @return  Entry, or null if missing
   */
  @Override
  @Nullable
  public T getEntry(String name) {
    return lookup.apply(name);
  }
}
