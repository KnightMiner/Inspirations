package knightminer.inspirations.common.block;

import knightminer.inspirations.common.IHidable;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.function.BooleanSupplier;

public class HidableBlock extends Block implements IHidable {
  private final BooleanSupplier enabled;

  public HidableBlock(Properties properties, BooleanSupplier isEnabled) {
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
