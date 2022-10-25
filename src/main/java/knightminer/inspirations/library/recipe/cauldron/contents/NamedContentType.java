package knightminer.inspirations.library.recipe.cauldron.contents;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.common.util.Constants.NBT;

import javax.annotation.Nullable;

/**
 * Cauldron content type based on {@link IStringSerializable}, typically an enum
 * @param <T>  Type class
 */
public abstract class NamedContentType<T extends IStringSerializable> extends CauldronContentType<T> {
  /**
   * Gets the name of the given value for writing to JSON and NBT
   * @param value  Value
   * @return  Name of the value
   */
  @Override
  public String getName(T value) {
    return value.getSerializedName();
  }

  /**
   * Gets an enum value from a string
   * @param name  String
   * @return  Value, or null if the name does not match
   */
  @Nullable
  protected abstract T getValue(String name);

  /**
   * Gets the value from a JSON element
   * @param element  JSON element
   * @param key      Key to get
   * @return  Value
   */
  @Override
  public T getValue(JsonElement element, String key) {
    String name = JSONUtils.convertToString(element, key);
    T value = getValue(name);
    if (value == null) {
      throw new JsonSyntaxException("Invalid value '" + name + "' for enum");
    }
    return value;
  }

  @Nullable
  @Override
  public T read(CompoundNBT tag) {
    if (tag.contains(getKey(), NBT.TAG_STRING)) {
      return getValue(tag.getString(getKey()));
    }
    return null;
  }
}
