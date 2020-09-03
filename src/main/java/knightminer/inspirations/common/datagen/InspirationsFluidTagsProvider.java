package knightminer.inspirations.common.datagen;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.FluidTagsProvider;

public class InspirationsFluidTagsProvider extends FluidTagsProvider {
  public InspirationsFluidTagsProvider(DataGenerator gen) {
    super(gen);
    this.modId = Inspirations.modID;
  }

  @Override
  public String getName() {
    return "Inspirations Fluid Tags";
  }

  @Override
  protected void registerTags() {
    this.getOrCreateBuilder(InspirationsTags.Fluids.MILK).add(InspirationsRecipes.milk, InspirationsRecipes.milk.getFlowingFluid());
  }
}
