package knightminer.inspirations.tweaks.item;

import knightminer.inspirations.common.item.HidableBlockItem;
import net.minecraft.block.Block;

import net.minecraft.item.Item.Properties;

public class SeedItem extends HidableBlockItem {

  public SeedItem(Block block, Properties props) {
    super(block, props);
  }

  @Override
  public String getDescriptionId() {
    return getOrCreateDescriptionId();
  }
}
