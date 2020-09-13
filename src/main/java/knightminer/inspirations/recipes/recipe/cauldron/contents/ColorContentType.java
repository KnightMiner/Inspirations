package knightminer.inspirations.recipes.recipe.cauldron.contents;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.cauldron.contents.CauldronContentType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants.NBT;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Content type for colors in the cauldron
 */
public class ColorContentType extends CauldronContentType<Integer> {
  private static final ResourceLocation TEXTURE_NAME = Inspirations.getResource("color");
  private static final String TRANSLATION_KEY = Util.makeTranslationKey("cauldron_contents", Inspirations.getResource("color"));

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
  public static ITextComponent getColorTooltip(int color) {
    return new TranslationTextComponent("item.color", "#" + getColorString(color)).mergeStyle(TextFormatting.GRAY);
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
    String text = JSONUtils.getString(element, key);
    Integer value = getValue(text);
    if (value != null) {
      return value;
    }
    throw new JsonSyntaxException("Invalid color value '" + text + "'");
  }

  @Nullable
  @Override
  public Integer read(CompoundNBT tag) {
    if (tag.contains(getKey(), NBT.TAG_STRING)) {
      return getValue(tag.getString(getKey()));
    }
    return null;
  }

  @Override
  public Integer read(PacketBuffer buffer) {
    return buffer.readInt();
  }

  @Override
  public void write(Integer color, PacketBuffer buffer) {
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
  public ITextComponent getDisplayName(Integer value) {
    return new TranslationTextComponent(TRANSLATION_KEY);
  }

  @Override
  public void addInformation(Integer value, List<ITextComponent> tooltip, ITooltipFlag tooltipFlag) {
    if (tooltipFlag.isAdvanced()) {
      tooltip.add(getColorTooltip(value));
    }
  }
}
