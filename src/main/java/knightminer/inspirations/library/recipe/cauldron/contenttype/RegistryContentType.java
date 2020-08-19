package knightminer.inspirations.library.recipe.cauldron.contenttype;

import io.netty.handler.codec.DecoderException;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;

/**
 * Content type that mirrors a Forge registry
 * @param <T>  Value type
 * @param <C>  Cauldron content type
 */
public class RegistryContentType<C extends ICauldronContents, T extends IForgeRegistryEntry<T>> extends MapContentType<C, T> {
  private final IForgeRegistry<T> registry;

  /**
   * Creates a new instance
   * @param clazz        Class for validation
   * @param constructor  Class constructor
   * @param registry     Forge registry instance for lookups
   * @param valueGetter  Function to get the value from a content type
   */
  public RegistryContentType(Class<C> clazz, Function<? super T,? extends C> constructor, IForgeRegistry<T> registry, Function<C,T> valueGetter) {
    super(clazz, constructor, valueGetter);
    this.registry = registry;
  }

  @Override
  public String getName(T value) {
    return Objects.requireNonNull(value.getRegistryName()).toString();
  }

  /**
   * Gets a value for the given name
   * @param name  Name to fetch
   * @return  Value, or null if the value is the default object or not found
   */
  @Nullable
  private T getValue(ResourceLocation name) {
    T value = registry.getValue(name);

    // swap default value for null
    ResourceLocation defaultKey = registry.getDefaultKey();
    if (value != null && defaultKey != null && !defaultKey.equals(value.getRegistryName())) {
      return value;
    }
    return null;
  }

  @Nullable
  @Override
  public T getEntry(String name) {
    ResourceLocation location = ResourceLocation.tryCreate(name);
    if (location != null) {
      return getValue(location);
    }
    return null;
  }

  @Override
  public C read(PacketBuffer buffer) {
    ResourceLocation location = buffer.readResourceLocation();
    T value = getValue(location);
    if (value != null) {
      return of(value);
    }
    throw new DecoderException("Invalid ID '" + location + "' for cauldron content type " + this);
  }

  @Override
  public void write(C contents, PacketBuffer buffer) {
    buffer.writeString(getName(contents));
  }
}
