package knightminer.inspirations.common.datagen;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import knightminer.inspirations.library.InspirationsTags;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class InspirationsFluidTagsProvider extends FluidTagsProvider {
  public InspirationsFluidTagsProvider(PackOutput packOutput, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existing) {
    super(packOutput, lookupProvider, Inspirations.modID, existing);
  }

  @Override
  public String getName() {
    return "Inspirations Fluid Tags";
  }

  @Override
  protected void addTags(Provider pProvider) {
    this.tag(InspirationsTags.Fluids.HONEY        ).add(InspirationsCaudrons.honey, InspirationsCaudrons.honey.getFlowing());
    this.tag(InspirationsTags.Fluids.BEETROOT_SOUP).add(InspirationsCaudrons.beetrootSoup, InspirationsCaudrons.beetrootSoup.getFlowing());
    this.tag(InspirationsTags.Fluids.MUSHROOM_STEW).add(InspirationsCaudrons.mushroomStew, InspirationsCaudrons.mushroomStew.getFlowing());
    this.tag(InspirationsTags.Fluids.RABBIT_STEW  ).add(InspirationsCaudrons.rabbitStew, InspirationsCaudrons.rabbitStew.getFlowing());
  }
}
