package knightminer.inspirations.recipes.recipe.cauldron.contents;

import knightminer.inspirations.library.recipe.cauldron.contents.RegistryContentType;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Potion content type
 */
@Deprecated
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
    super(ForgeRegistries.POTIONS);
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
  public Component getDisplayName(Potion value) {
    Component component = new TranslatableComponent(value.getName(PREFIX));
    if (wrapName != null) {
      return new TranslatableComponent(wrapName, component);
    }
    return component;
  }

  @Override
  public void addInformation(Potion potion, List<Component> tooltip, TooltipFlag tooltipFlag) {
    // strongly based on PotionUtil version, but takes a potion instead of a stack. main difference is it skips the no effects tooltip
    for (MobEffectInstance instance : potion.getEffects()) {
      MutableComponent effectString = new TranslatableComponent(instance.getEffect().getDescriptionId());
      MobEffect effect = instance.getEffect();
      if (instance.getAmplifier() > 0) {
        effectString.append(" ");
        effectString.append(new TranslatableComponent("potion.potency." + instance.getAmplifier()));
      }
      if (instance.getDuration() > 20) {
        effectString.append(new TextComponent(" (" + MobEffectUtil.formatDuration(instance, 1.0f) + ")"));
      }
      effectString.withStyle(effect.isBeneficial() ? ChatFormatting.BLUE : ChatFormatting.RED);
      tooltip.add(effectString);
    }
    // add tooltip for unfermented
    if (wrapName != null) {
      tooltip.add(new TranslatableComponent(wrapName + ".tooltip").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }
  }
}
