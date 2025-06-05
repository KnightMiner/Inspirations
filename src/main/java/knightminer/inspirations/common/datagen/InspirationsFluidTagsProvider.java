package knightminer.inspirations.common.datagen;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import knightminer.inspirations.library.InspirationsTags;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.mantle.datagen.MantleTags;

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
    // local tags
    tag(InspirationsTags.Fluids.HONEY        ).add(InspirationsCaudrons.honey, InspirationsCaudrons.honey.getFlowing());
    tag(InspirationsTags.Fluids.BEETROOT_SOUP).add(InspirationsCaudrons.beetrootSoup, InspirationsCaudrons.beetrootSoup.getFlowing());
    tag(InspirationsTags.Fluids.MUSHROOM_STEW).add(InspirationsCaudrons.mushroomStew, InspirationsCaudrons.mushroomStew.getFlowing());
    tag(InspirationsTags.Fluids.RABBIT_STEW  ).add(InspirationsCaudrons.rabbitStew, InspirationsCaudrons.rabbitStew.getFlowing());
    tag(InspirationsTags.Fluids.POTATO_SOUP  ).add(InspirationsCaudrons.potatoSoup, InspirationsCaudrons.potatoSoup.getFlowing());
    // common tags
    tag(MantleTags.Fluids.HONEY).addTag(InspirationsTags.Fluids.HONEY);
    tag(MantleTags.Fluids.BEETROOT_SOUP).addTag(InspirationsTags.Fluids.BEETROOT_SOUP);
    tag(MantleTags.Fluids.MUSHROOM_STEW).addTag(InspirationsTags.Fluids.MUSHROOM_STEW);
    tag(MantleTags.Fluids.RABBIT_STEW).addTag(InspirationsTags.Fluids.RABBIT_STEW);
    // manntle adds the rest to soup, but potato is our own invention
    tag(MantleTags.Fluids.SOUP).addTag(InspirationsTags.Fluids.POTATO_SOUP);
  }
}
