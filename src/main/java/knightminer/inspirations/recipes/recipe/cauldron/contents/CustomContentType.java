package knightminer.inspirations.recipes.recipe.cauldron.contents;

import knightminer.inspirations.library.recipe.cauldron.contents.CauldronContentType;
import net.minecraft.util.ResourceLocation;

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
  public String getName(ResourceLocation value) {
    return value.toString();
  }

  @Nullable
  @Override
  public ResourceLocation getEntry(String name) {
    return ResourceLocation.tryCreate(name);
  }
}
