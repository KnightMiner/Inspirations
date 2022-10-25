package knightminer.inspirations.utility.item;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.util.NonNullList;

import net.minecraft.item.Item.Properties;

public class TorchLeverItem extends WallOrFloorItem implements IHidable {
  public TorchLeverItem(Block floorBlock, Block wallBlock, Properties properties) {
    super(floorBlock, wallBlock, properties);
  }

  @Override
  public boolean isEnabled() {
    return Config.enableTorchLever.get();
  }

  @Override
  public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
    if (shouldAddtoItemGroup(group)) {
      super.fillItemCategory(group, items);
    }
  }
}
