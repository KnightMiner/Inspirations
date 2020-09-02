package knightminer.inspirations.recipes.recipe.cauldron.contents;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.cauldron.contents.NamedContentType;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;

/**
 * Content type for the 16 dye enum values
 */
public class DyeContentType extends NamedContentType<DyeColor> {
  private static final ResourceLocation TEXTURE_NAME = Inspirations.getResource("dye");

  /**
   * Creates a new type instance
   */
  @SuppressWarnings("ConstantConditions")
  public DyeContentType() {
    super(name -> DyeColor.byTranslationKey(name, null));
  }

  @Override
  public ResourceLocation getTexture(DyeColor value) {
    return TEXTURE_NAME;
  }

  @Override
  public int getColor(DyeColor value) {
    return value.getColorValue();
  }
}
