package knightminer.inspirations.recipes.recipe.cauldron.contents;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.library.recipe.cauldron.contents.NamedContentType;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Content type for the 16 dye enum values
 */
public class DyeContentType extends NamedContentType<DyeColor> {
  private static final ResourceLocation TEXTURE_NAME = Inspirations.getResource("dye");
  private static final String TRANSLATION_KEY = Util.makeDescriptionId("cauldron_contents", TEXTURE_NAME);

  @Override
  public ResourceLocation getTexture(DyeColor value) {
    return TEXTURE_NAME;
  }

  @Override
  public int getColor(DyeColor value) {
    return MiscUtil.getColor(value);
  }

  @Override
  public Component getDisplayName(DyeColor value) {
    return new TranslatableComponent(TRANSLATION_KEY, new TranslatableComponent("color.minecraft." + value.getSerializedName()));
  }

  @Override
  public void addInformation(DyeColor value, List<Component> tooltip, TooltipFlag tooltipFlag) {
    if (tooltipFlag.isAdvanced()) {
      tooltip.add(ColorContentType.getColorTooltip(MiscUtil.getColor(value)));
    }
  }


  /* Serializing and deserializing */

  @SuppressWarnings("ConstantConditions")
  @Nullable
  @Override
  protected DyeColor getValue(String name) {
    return DyeColor.byName(name, null);
  }

  @Override
  public DyeColor read(FriendlyByteBuf buffer) {
    return buffer.readEnum(DyeColor.class);
  }

  @Override
  public void write(DyeColor value, FriendlyByteBuf buffer) {
    buffer.writeEnum(value);
  }
}
