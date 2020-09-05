package knightminer.inspirations.library.recipe.cauldron.contents;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Content type that mirrors a Forge registry
 * @param <T>  Value type
 */
public abstract class RegistryContentType<T extends IForgeRegistryEntry<T>> extends CauldronContentType<T> {
  private final IForgeRegistry<T> registry;

  /**
   * Creates a new instance
   * @param registry     Forge registry instance for lookups
   */
  public RegistryContentType(IForgeRegistry<T> registry) {
    this.registry = registry;
  }

  @Override
  public String getName(T value) {
    return Objects.requireNonNull(value.getRegistryName()).toString();
  }

  @Nullable
  @Override
  public T getEntry(String name) {
    ResourceLocation location = ResourceLocation.tryCreate(name);
    if (location != null) {
      // swap default value for null
      T value = registry.getValue(location);
      ResourceLocation defaultKey = registry.getDefaultKey();
      if (value != null && defaultKey != null && !defaultKey.equals(value.getRegistryName())) {
        return value;
      }
    }
    return null;
  }

  @Override
  public String getModId(T value) {
    return Objects.requireNonNull(value.getRegistryName()).getNamespace();
  }
}
