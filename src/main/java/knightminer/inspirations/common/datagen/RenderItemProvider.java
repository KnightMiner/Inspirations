package knightminer.inspirations.common.datagen;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.InspirationsBuilding;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import slimeknights.mantle.client.render.RenderItem;
import slimeknights.mantle.data.datamap.BlockStateDataMapProvider;

import java.util.ArrayList;
import java.util.List;

public class RenderItemProvider extends BlockStateDataMapProvider<List<RenderItem>> {
  public RenderItemProvider(PackOutput output) {
    super(output, Target.RESOURCE_PACK, RenderItem.STATE_REGISTRY, Inspirations.modID);
  }

  @Override
  protected void addEntries() {
    String shelf = "template/shelf";
    List<RenderItem> items = new ArrayList<>();
    for (int y = 11; y >= 3; y -= 8) {
      for (int x = 2; x <= 16; x += 2) {
        items.add(RenderItem.builder().center(x, y, 3).size(3.75f).build());
      }
    }
    entry(shelf, items);
    InspirationsBuilding.shelf.forEach(shelfBlock -> block(shelfBlock).variant(shelf));
  }

  @Override
  public String getName() {
    return "Inspirations render item provider";
  }
}
