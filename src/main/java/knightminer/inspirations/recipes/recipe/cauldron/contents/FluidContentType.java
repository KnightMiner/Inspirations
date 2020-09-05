package knightminer.inspirations.recipes.recipe.cauldron.contents;

import knightminer.inspirations.library.recipe.cauldron.contents.RegistryContentType;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Content type for a fluid
 */
public class FluidContentType extends RegistryContentType<Fluid> {
  /**
   * Creates a new instance
   */
  public FluidContentType() {
    super(ForgeRegistries.FLUIDS);
  }

  @Override
  public ResourceLocation getTexture(Fluid fluid) {
    return fluid.getAttributes().getStillTexture();
  }

  @Override
  public int getColor(Fluid fluid) {
    return fluid.getAttributes().getColor();
  }

  @Override
  public ITextComponent getDisplayName(Fluid value) {
    return new TranslationTextComponent(value.getAttributes().getTranslationKey());
  }
}
