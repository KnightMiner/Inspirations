package knightminer.inspirations.recipes.recipe.cauldron.contents;

import knightminer.inspirations.library.recipe.cauldron.contents.CauldronContentType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

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

  @Override
  public String getName(ResourceLocation value) {
    return value.toString();
  }

  @Nullable
  @Override
  public ResourceLocation getEntry(String name) {
    return ResourceLocation.tryCreate(name);
  }
}
