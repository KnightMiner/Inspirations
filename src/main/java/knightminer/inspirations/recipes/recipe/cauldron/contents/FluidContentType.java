package knightminer.inspirations.recipes.recipe.cauldron.contents;

import knightminer.inspirations.library.recipe.cauldron.contents.RegistryContentType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
  public Component getDisplayName(Fluid value) {
    return new TranslatableComponent(value.getAttributes().getTranslationKey());
  }
}
