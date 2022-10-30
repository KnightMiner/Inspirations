package knightminer.inspirations.recipes.recipe.cauldron.contents;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.cauldron.contents.CauldronContentType;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Content type for colors in the cauldron
 */
@Deprecated
public class ColorContentType extends CauldronContentType<Integer> {
  private static final ResourceLocation TEXTURE_NAME = Inspirations.getResource("color");
  private static final String TRANSLATION_KEY = Util.makeDescriptionId("cauldron_contents", Inspirations.getResource("color"));

  /**
   * Gets the color as a hex string
   * @param color  Color
   * @return  Hex string
   */
  public static String getColorString(int color) {
    return String.format("%06X", color);
  }

  /**
   * Gets a text component for the given color
   * @param color  Color
   * @return  Tooltip text component
   */
  public static Component getColorTooltip(int color) {
    return new TranslatableComponent("item.color", "#" + getColorString(color)).withStyle(ChatFormatting.GRAY);
  }


  /* Parsing */

  @Override
  public String getKey() {
    return "color";
  }

  @Override
  public String getName(Integer value) {
    return getColorString(value);
  }

  /**
   * Gets an color from a string
   * @param str  Color string
   * @return  Color integer
   */
  @Nullable
  private Integer getValue(String str) {
    try {
      return Integer.parseInt(str, 16);
    }
    catch (NumberFormatException e) {
      return null;
    }
  }

  @Override
  public Integer getValue(JsonElement element, String key) {
    String text = GsonHelper.convertToString(element, key);
    Integer value = getValue(text);
    if (value != null) {
      return value;
    }
    throw new JsonSyntaxException("Invalid color value '" + text + "'");
  }

  @Nullable
  @Override
  public Integer read(CompoundTag tag) {
    if (tag.contains(getKey(), Tag.TAG_STRING)) {
      return getValue(tag.getString(getKey()));
    }
    return null;
  }

  @Override
  public Integer read(FriendlyByteBuf buffer) {
    return buffer.readInt();
  }

  @Override
  public void write(Integer color, FriendlyByteBuf buffer) {
    buffer.writeInt(color);
  }


  /* Display */

  @Override
  public ResourceLocation getTexture(Integer value) {
    return TEXTURE_NAME;
  }

  @Override
  public int getColor(Integer value) {
    return value;
  }

  @Override
  public Component getDisplayName(Integer value) {
    return new TranslatableComponent(TRANSLATION_KEY);
  }

  @Override
  public void addInformation(Integer value, List<Component> tooltip, TooltipFlag tooltipFlag) {
    if (tooltipFlag.isAdvanced()) {
      tooltip.add(getColorTooltip(value));
    }
  }
}
