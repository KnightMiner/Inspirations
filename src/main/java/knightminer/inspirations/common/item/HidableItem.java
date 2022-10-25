package knightminer.inspirations.common.item;

import knightminer.inspirations.common.IHidable;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.item.TooltipItem;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Item which is hidden if the config value is disabled.
 */
public class HidableItem extends TooltipItem implements IHidable {
  private final BooleanSupplier enabled;

  public HidableItem(Properties properties, Supplier<Boolean> isEnabled) {
    super(properties);
    this.enabled = isEnabled::get;
  }

  public HidableItem(Properties properties, BooleanSupplier isEnabled) {
    super(properties);
    this.enabled = isEnabled;
  }

  @Override
  public boolean isEnabled() {
    return enabled.getAsBoolean();
  }

  @Override
  public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
    if (shouldAddtoItemGroup(group)) {
      super.fillItemCategory(group, items);
    }
  }
}
