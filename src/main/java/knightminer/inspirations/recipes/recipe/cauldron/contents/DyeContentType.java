package knightminer.inspirations.recipes.recipe.cauldron.contents;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.cauldron.contents.NamedContentType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.DyeColor;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Content type for the 16 dye enum values
 */
public class DyeContentType extends NamedContentType<DyeColor> {
  private static final ResourceLocation TEXTURE_NAME = Inspirations.getResource("dye");
  private static final String TRANSLATION_KEY = Util.makeTranslationKey("cauldron_contents", TEXTURE_NAME);

  @Override
  public ResourceLocation getTexture(DyeColor value) {
    return TEXTURE_NAME;
  }

  @Override
  public int getColor(DyeColor value) {
    return value.getColorValue();
  }

  @Override
  public ITextComponent getDisplayName(DyeColor value) {
    return new TranslationTextComponent(TRANSLATION_KEY, new TranslationTextComponent("color.minecraft." + value.getString()));
  }

  @Override
  public void addInformation(DyeColor value, List<ITextComponent> tooltip, ITooltipFlag tooltipFlag) {
    if (tooltipFlag.isAdvanced()) {
      tooltip.add(ColorContentType.getColorTooltip(value.getColorValue()));
    }
  }


  /* Serializing and deserializing */

  @SuppressWarnings("ConstantConditions")
  @Nullable
  @Override
  protected DyeColor getValue(String name) {
    return DyeColor.byTranslationKey(name, null);
  }

  @Override
  public DyeColor read(PacketBuffer buffer) {
    return buffer.readEnumValue(DyeColor.class);
  }

  @Override
  public void write(DyeColor value, PacketBuffer buffer) {
    buffer.writeEnumValue(value);
  }
}
