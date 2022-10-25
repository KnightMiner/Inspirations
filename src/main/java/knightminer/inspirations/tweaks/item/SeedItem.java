package knightminer.inspirations.tweaks.item;

import knightminer.inspirations.common.item.HidableBlockItem;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.item.Item.Properties;

public class SeedItem extends HidableBlockItem {

  public SeedItem(Block block, Properties props) {
    super(block, props);
  }

  @Override
  public String getDescriptionId() {
    return getOrCreateDescriptionId();
  }
}
