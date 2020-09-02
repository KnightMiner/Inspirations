package knightminer.inspirations.recipes.recipe.cauldron.contents;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.cauldron.contents.RegistryContentType;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Potion content type
 */
public class PotionContentType extends RegistryContentType<Potion> {
  public static final ResourceLocation TEXTURE_NAME = Inspirations.getResource("potion");

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
}
