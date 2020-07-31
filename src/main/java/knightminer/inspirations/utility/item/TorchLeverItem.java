package knightminer.inspirations.utility.item;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.util.NonNullList;

public class TorchLeverItem extends WallOrFloorItem implements IHidable {
  public TorchLeverItem() {
    super(
        InspirationsUtility.torchLeverFloor,
        InspirationsUtility.torchLeverWall,
        new Item.Properties().group(ItemGroup.REDSTONE)
         );
  }

  @Override
  public boolean isEnabled() {
    return Config.enableTorchLever.get();
  }

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    if (shouldAddtoItemGroup(group)) {
      super.fillItemGroup(group, items);
    }
  }
}
