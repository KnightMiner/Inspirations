package knightminer.inspirations.common.datagen;

import knightminer.inspirations.Inspirations;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;

public class SpriteSourcesProvider extends SpriteSourceProvider {
  public SpriteSourcesProvider(PackOutput output, ExistingFileHelper fileHelper) {
    super(output, fileHelper, Inspirations.modID);
  }

  @Override
  protected void addSources() {
    // we load our fluid textures from fluids
    atlas(BLOCKS_ATLAS).addSource(new DirectoryLister("fluid", "fluid/"));
  }
}
