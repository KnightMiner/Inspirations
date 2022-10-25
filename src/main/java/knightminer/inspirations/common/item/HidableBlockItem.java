package knightminer.inspirations.common.item;

import knightminer.inspirations.common.IHidable;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.function.Supplier;

public class HidableBlockItem extends BlockItem implements IHidable {
  private final Supplier<Boolean> enabled;

  public HidableBlockItem(Block block, Item.Properties builder) {
    super(block, builder);

    if (block instanceof IHidable) {
      enabled = ((IHidable)block)::isEnabled;
    } else {
      enabled = () -> true;
    }
  }

  @Override
  public boolean isEnabled() {
    return enabled.get();
  }

  @Override
  public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
    if (shouldAddtoItemGroup(group)) {
      super.fillItemCategory(group, items);
    }
  }
}
