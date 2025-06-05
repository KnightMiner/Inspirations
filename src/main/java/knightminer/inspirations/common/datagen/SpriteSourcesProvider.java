package knightminer.inspirations.common.datagen;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.tweaks.client.TintedLecternRenderer;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;

import java.util.Optional;

public class SpriteSourcesProvider extends SpriteSourceProvider {
  public SpriteSourcesProvider(PackOutput output, ExistingFileHelper fileHelper) {
    super(output, fileHelper, Inspirations.modID);
  }

  @Override
  protected void addSources() {
    atlas(BLOCKS_ATLAS)
      // we load our fluid textures from fluids
      .addSource(new DirectoryLister("fluid", "fluid/"))
      // tinted lectern book renderer
      .addSource(new SingleFile(TintedLecternRenderer.BOOK_LOCATION.texture(), Optional.empty()));
  }
}
