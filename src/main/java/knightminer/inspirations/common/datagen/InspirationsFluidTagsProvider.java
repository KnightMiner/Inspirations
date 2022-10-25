package knightminer.inspirations.common.datagen;

import knightminer.inspirations.Inspirations;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class InspirationsFluidTagsProvider extends FluidTagsProvider {
  public InspirationsFluidTagsProvider(DataGenerator gen, ExistingFileHelper existing) {
    super(gen, Inspirations.modID, existing);
  }

  @Override
  public String getName() {
    return "Inspirations Fluid Tags";
  }

  @Override
  protected void addTags() {
  }
}
