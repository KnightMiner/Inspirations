package knightminer.inspirations.common.datagen;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.recipes.InspirationsRecipes;
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
    this.tag(InspirationsTags.Fluids.HONEY        ).add(InspirationsRecipes.honey,        InspirationsRecipes.honey.getFlowing());
    this.tag(InspirationsTags.Fluids.BEETROOT_SOUP).add(InspirationsRecipes.beetrootSoup, InspirationsRecipes.beetrootSoup.getFlowing());
    this.tag(InspirationsTags.Fluids.MUSHROOM_STEW).add(InspirationsRecipes.mushroomStew, InspirationsRecipes.mushroomStew.getFlowing());
    this.tag(InspirationsTags.Fluids.RABBIT_STEW  ).add(InspirationsRecipes.rabbitStew,   InspirationsRecipes.rabbitStew.getFlowing());
  }
}
