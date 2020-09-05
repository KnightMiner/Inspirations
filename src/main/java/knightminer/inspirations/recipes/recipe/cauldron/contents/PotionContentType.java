package knightminer.inspirations.recipes.recipe.cauldron.contents;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.cauldron.contents.RegistryContentType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

/**
 * Potion content type
 */
public class PotionContentType extends RegistryContentType<Potion> {
  public static final ResourceLocation TEXTURE_NAME = Inspirations.getResource("potion");
  private static final String PREFIX = "item.minecraft.potion.effect.";

  /**
   * Creates a new instance
   */
  public PotionContentType() {
    super(ForgeRegistries.POTION_TYPES);
  }

  @Override
  public ResourceLocation getTexture(Potion value) {
    return TEXTURE_NAME;
  }

  @Override
  public int getColor(Potion value) {
    return PotionUtils.getPotionColor(value);
  }

  @Override
  public ITextComponent getDisplayName(Potion value) {
    return new TranslationTextComponent(value.getNamePrefixed(PREFIX));
  }

  @Override
  public void addInformation(Potion potion, List<ITextComponent> tooltip, ITooltipFlag tooltipFlag) {
    // strongly based on PotionUtil version, but takes a potion instead of a stack. main difference is it skips the no effects tooltip
    for (EffectInstance instance : potion.getEffects()) {
      IFormattableTextComponent effectString = new TranslationTextComponent(instance.getPotion().getName());
      Effect effect = instance.getPotion();
      if (instance.getAmplifier() > 0) {
        effectString.appendString(" ");
        effectString.append(new TranslationTextComponent("potion.potency." + instance.getAmplifier()));
      }
      if (instance.getDuration() > 20) {
        effectString.append(new StringTextComponent(" (" + EffectUtils.getPotionDurationString(instance, 1.0f) + ")"));
      }
      effectString.mergeStyle(effect.isBeneficial() ? TextFormatting.BLUE : TextFormatting.RED);
      tooltip.add(effectString);
    }
  }
}
