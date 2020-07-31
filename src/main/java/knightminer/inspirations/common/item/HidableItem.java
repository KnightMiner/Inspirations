package knightminer.inspirations.common.item;

import knightminer.inspirations.common.IHidable;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.mantle.item.TooltipItem;

import java.util.function.Supplier;

/**
 * Item which is hidden if the config value is disabled.
 */
public class HidableItem extends TooltipItem implements IHidable {
  private final Supplier<Boolean> enabled;

  public HidableItem(Properties properties, Supplier<Boolean> isEnabled) {
    super(properties);
    this.enabled = isEnabled;
  }

  @Override
  public boolean isEnabled() {
    return enabled.get();
  }

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    if (shouldAddtoItemGroup(group)) {
      super.fillItemGroup(group, items);
    }
  }
}
