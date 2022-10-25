package knightminer.inspirations.building.item;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TallBlockItem;
import net.minecraft.util.NonNullList;

import net.minecraft.item.Item.Properties;

/**
 * Extension of a door item to make it hidable and not burnable
 */
public class GlassDoorBlockItem extends TallBlockItem implements IHidable {
  public GlassDoorBlockItem(Block blockIn, Properties builder) {
    super(blockIn, builder);
  }

  @Override
  public int getBurnTime(ItemStack itemStack) {
    return 0;
  }

  @Override
  public boolean isEnabled() {
    return Config.enableGlassDoor.get();
  }

  @Override
  public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
    if (shouldAddtoItemGroup(group)) {
      super.fillItemCategory(group, items);
    }
  }
}
