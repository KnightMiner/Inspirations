package knightminer.inspirations.common.item;

import knightminer.inspirations.library.MiscUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;

import java.util.function.BooleanSupplier;

/** Hidable item that is dyeable */
public class DyeableItem extends HidableItem implements DyeableLeatherItem {
  public DyeableItem(Properties properties, BooleanSupplier condition) {
    super(properties, condition);
  }

  @Override
  public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
    if (shouldAddtoItemGroup(group) && allowedIn(group)) {
      for (DyeColor color : DyeColor.values()) {
        ItemStack stack = new ItemStack(this);
        setColor(stack, MiscUtil.getColor(color));
        items.add(stack);
      }
    }
  }
}
