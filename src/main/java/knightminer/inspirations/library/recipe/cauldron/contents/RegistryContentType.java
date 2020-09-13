package knightminer.inspirations.library.recipe.cauldron.contents;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.DecoderException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;

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

  @Override
  public String getModId(T value) {
    return Objects.requireNonNull(value.getRegistryName()).getNamespace();
  }


  /* Serializing and deserializing */

  /**
   * Gets a value from a registry name
   * @param name  Value to fetch
   * @return  Registry value, null if missing
   */
  @Nullable
  private T getValue(ResourceLocation name) {
    if (registry.containsKey(name)) {
      return registry.getValue(name);
    }
    return null;
  }

  /**
   * Gets a nonnull value from a registry name, throwing the exception if missing
   * @param name       Value to fetch
   * @param exception  Exception function
   * @return  Registry value
   */
  private T getValue(ResourceLocation name, Function<String, RuntimeException> exception) {
    T value = getValue(name);
    if (value != null) {
      return value;
    }
    throw exception.apply("Invalid value '" + name + "' for registry " + registry.getRegistryName());
  }

  @Override
  public T getValue(JsonElement element, String key) {
    return getValue(new ResourceLocation(JSONUtils.getString(element, key)), JsonSyntaxException::new);
  }

  @Nullable
  @Override
  public T read(CompoundNBT tag) {
    if (tag.contains(getKey(), NBT.TAG_STRING)) {
      return getValue(new ResourceLocation(tag.getString(getKey())));
    }
    return null;
  }

  @Override
  public T read(PacketBuffer buffer) {
    return getValue(buffer.readResourceLocation(), DecoderException::new);
  }

  @Override
  public void write(T value, PacketBuffer buffer) {
    buffer.writeResourceLocation(Objects.requireNonNull(value.getRegistryName()));
  }
}
