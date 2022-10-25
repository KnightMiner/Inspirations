package knightminer.inspirations.common.item;

import knightminer.inspirations.common.IHidable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;

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
  public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
    if (shouldAddtoItemGroup(group)) {
      super.fillItemCategory(group, items);
    }
  }
}
