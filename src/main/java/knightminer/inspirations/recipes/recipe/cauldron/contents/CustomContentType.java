package knightminer.inspirations.recipes.recipe.cauldron.contents;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import knightminer.inspirations.library.recipe.cauldron.contents.CauldronContentType;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nullable;

/**
 * Content type that supports arbitrary resource location names
 */
public class CustomContentType extends CauldronContentType<ResourceLocation> {
  @Override
  public ResourceLocation getTexture(ResourceLocation value) {
    return value;
  }

  @Override
  public Component getDisplayName(ResourceLocation value) {
    return new TranslatableComponent(Util.makeDescriptionId("cauldron_contents", value));
  }

  @Override
  public String getModId(ResourceLocation value) {
    return value.getNamespace();
  }

  /* Serializing */

  @Override
  public String getName(ResourceLocation value) {
    return value.toString();
  }

  @Override
  public ResourceLocation getValue(JsonElement element, String key) {
    String name = GsonHelper.convertToString(element, key);
    ResourceLocation location = ResourceLocation.tryParse(name);
    if (location != null) {
      return location;
    }
    throw new JsonSyntaxException("Invalid resource location '" + name + "'");
  }

  @Nullable
  @Override
  public ResourceLocation read(CompoundTag tag) {
    if (tag.contains(getKey(), Tag.TAG_STRING)) {
      return ResourceLocation.tryParse(tag.getString(getKey()));
    }
    return null;
  }

  @Override
  public ResourceLocation read(FriendlyByteBuf buffer) {
    return buffer.readResourceLocation();
  }

  @Override
  public void write(ResourceLocation value, FriendlyByteBuf buffer) {
    buffer.writeResourceLocation(value);
  }
}
