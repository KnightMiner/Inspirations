package knightminer.inspirations.recipes.recipe.cauldron.contents;

import knightminer.inspirations.library.recipe.cauldron.contents.RegistryContentType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Potion content type
 */
public class PotionContentType extends RegistryContentType<Potion> {
  private static final String PREFIX = "item.minecraft.potion.effect.";

  private final ResourceLocation textureName;
  @Nullable
  private final String wrapName;

  /**
   * Creates a new instance
   * @param textureName  Name of the texture for this type
   * @param wrapName     If true, name will be wrapped using a translation key from the texture name
   */
  public PotionContentType(ResourceLocation textureName, boolean wrapName) {
    super(ForgeRegistries.POTION_TYPES);
    this.textureName = textureName;
    if (wrapName) {
      this.wrapName = Util.makeDescriptionId("cauldron_contents", textureName);
    } else {
      this.wrapName = null;
    }
  }

  @Override
  public ResourceLocation getTexture(Potion value) {
    return textureName;
  }

  @Override
  public int getColor(Potion value) {
    return PotionUtils.getColor(value);
  }

  @Override
  public ITextComponent getDisplayName(Potion value) {
    ITextComponent component = new TranslationTextComponent(value.getName(PREFIX));
    if (wrapName != null) {
      return new TranslationTextComponent(wrapName, component);
    }
    return component;
  }

  @Override
  public void addInformation(Potion potion, List<ITextComponent> tooltip, ITooltipFlag tooltipFlag) {
    // strongly based on PotionUtil version, but takes a potion instead of a stack. main difference is it skips the no effects tooltip
    for (EffectInstance instance : potion.getEffects()) {
      IFormattableTextComponent effectString = new TranslationTextComponent(instance.getEffect().getDescriptionId());
      Effect effect = instance.getEffect();
      if (instance.getAmplifier() > 0) {
        effectString.append(" ");
        effectString.append(new TranslationTextComponent("potion.potency." + instance.getAmplifier()));
      }
      if (instance.getDuration() > 20) {
        effectString.append(new StringTextComponent(" (" + EffectUtils.formatDuration(instance, 1.0f) + ")"));
      }
      effectString.withStyle(effect.isBeneficial() ? TextFormatting.BLUE : TextFormatting.RED);
      tooltip.add(effectString);
    }
    // add tooltip for unfermented
    if (wrapName != null) {
      tooltip.add(new TranslationTextComponent(wrapName + ".tooltip").withStyle(TextFormatting.GRAY, TextFormatting.ITALIC));
    }
  }
}
