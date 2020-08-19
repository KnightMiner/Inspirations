package knightminer.inspirations.library.recipe.cauldron.contenttype;

import com.google.gson.JsonObject;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Represents a type of contents that can be stored in the cauldron
 * @param <C>  {@link ICauldronContents} implementation for this type
 */
public abstract class CauldronContentType<C extends ICauldronContents> {
  private final Class<C> clazz;

  /**
   * Creates a new instance
   * @param clazz  Content type class for validation
   */
  protected CauldronContentType(Class<C> clazz) {
    this.clazz = clazz;
  }

  /**
   * Checks if the given contents matches this type
   * @param contents  Contents to check
   * @return True if the type matches
   */
  public boolean matches(ICauldronContents contents) {
    return clazz.isInstance(contents);
  }

  /**
   * Gets the contents as this type
   * @param contents  Contents to fetch
   * @return  Type to get
   */
  public Optional<C> get(ICauldronContents contents) {
    if (clazz.isInstance(contents)) {
      return Optional.of(clazz.cast(contents));
    }
    return Optional.empty();
  }

  /**
   * Reads the given type from NBT
   * @param tag  NBT tag
   * @return  Read value
   */
  @Nullable
  public abstract C read(CompoundNBT tag);

  /**
   * Reads the given type from JSON
   * @param json  JSON object
   * @return  Read value=
   * @throws com.google.gson.JsonSyntaxException if the JSON is invalid
   */
  public abstract C read(JsonObject json);

  /**
   * Reads the given type from the packet buffer
   * @param buffer  Packet buffer
   * @return  Read value
   * @throws io.netty.handler.codec.DecoderException if the type is invalid
   */
  public abstract C read(PacketBuffer buffer);

  /**
   * Writes the given type to NBT
   * @param contents  Contents to write
   * @param tag       NBT tag
   */
  public abstract void write(C contents, CompoundNBT tag);

  /**
   * Writes the given type to JSON
   * @param contents  Contents to write
   * @param json      JSON object
   */
  public abstract void write(C contents, JsonObject json);

  /**
   * Writes the given type to the packet buffer
   * @param contents  Contents to write
   * @param buffer    Packet buffer
   */
  public abstract void write(C contents, PacketBuffer buffer);

  @Override
  public String toString() {
    return String.format("CauldronContentType[%s]", CauldronContentTypes.getName(this));
  }
}
