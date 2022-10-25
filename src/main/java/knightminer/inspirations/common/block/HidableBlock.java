package knightminer.inspirations.common.block;

import knightminer.inspirations.common.IHidable;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.function.Supplier;

import net.minecraft.block.AbstractBlock.Properties;

public class HidableBlock extends Block implements IHidable {
  private final Supplier<Boolean> enabled;

  public HidableBlock(Properties properties, Supplier<Boolean> isEnabled) {
    super(properties);
    this.enabled = isEnabled;
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
