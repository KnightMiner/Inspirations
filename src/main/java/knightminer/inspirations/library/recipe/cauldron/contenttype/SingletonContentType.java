package knightminer.inspirations.library.recipe.cauldron.contenttype;

import com.google.gson.JsonObject;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

/**
 * Content type that only contains a single value
 * @param <C>  Content class type
 */
public class SingletonContentType<C extends ICauldronContents> extends CauldronContentType<C> {
  private final C instance;

  /**
   * Creates a new instance
   * @param clazz     Content type class for validation
   * @param instance  Instance to always return
   */
  public SingletonContentType(Class<C> clazz, C instance) {
    super(clazz);
    this.instance = instance;
  }

  /**
   * Gets the single instance of this type
   * @return  Instance
   */
  public C get() {
    return instance;
  }

  @Override
  public C read(CompoundNBT tag) {
    return get();
  }

  @Override
  public C read(JsonObject json) {
    return get();
  }

  @Override
  public C read(PacketBuffer buffer) {
    return get();
  }

  @Override
  public void write(C contents, CompoundNBT tag) {}

  @Override
  public void write(C contents, JsonObject json) {}

  @Override
  public void write(C contents, PacketBuffer buffer) {}
}
