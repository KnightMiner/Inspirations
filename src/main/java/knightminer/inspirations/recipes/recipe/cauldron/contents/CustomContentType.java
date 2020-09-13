package knightminer.inspirations.recipes.recipe.cauldron.contents;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import knightminer.inspirations.library.recipe.cauldron.contents.CauldronContentType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants.NBT;

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
  public ITextComponent getDisplayName(ResourceLocation value) {
    return new TranslationTextComponent(Util.makeTranslationKey("cauldron_contents", value));
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
    String name = JSONUtils.getString(element, key);
    ResourceLocation location = ResourceLocation.tryCreate(name);
    if (location != null) {
      return location;
    }
    throw new JsonSyntaxException("Invalid resource location '" + name + "'");
  }

  @Nullable
  @Override
  public ResourceLocation read(CompoundNBT tag) {
    if (tag.contains(getKey(), NBT.TAG_STRING)) {
      return ResourceLocation.tryCreate(tag.getString(getKey()));
    }
    return null;
  }

  @Override
  public ResourceLocation read(PacketBuffer buffer) {
    return buffer.readResourceLocation();
  }

  @Override
  public void write(ResourceLocation value, PacketBuffer buffer) {
    buffer.writeResourceLocation(value);
  }
}
