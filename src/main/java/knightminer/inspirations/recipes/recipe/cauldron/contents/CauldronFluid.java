package knightminer.inspirations.recipes.recipe.cauldron.contents;

import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronFluid;
import knightminer.inspirations.library.recipe.cauldron.contenttype.CauldronContentType;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.registries.IRegistryDelegate;

public class CauldronFluid implements ICauldronFluid {
  private final IRegistryDelegate<Fluid> fluid;
  public CauldronFluid(Fluid fluid) {
    this.fluid = fluid.delegate;
  }

  @Override
  public CauldronContentType<?> getType() {
    return CauldronContentTypes.FLUID;
  }

  @Override
  public Fluid getFluid() {
    return fluid.get();
  }
}
