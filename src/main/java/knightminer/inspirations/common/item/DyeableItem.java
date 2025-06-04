package knightminer.inspirations.common.item;

import knightminer.inspirations.library.MiscUtil;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

/** Hidable item that is dyeable */
public class DyeableItem extends Item implements DyeableLeatherItem {
  public DyeableItem(Properties properties) {
    super(properties);
  }

  /** Adds all variants to the given consumer */
  public void addVariants(Consumer<ItemStack> consumer) {
    for (DyeColor color : DyeColor.values()) {
      ItemStack stack = new ItemStack(this);
      setColor(stack, MiscUtil.getColor(color));
      consumer.accept(stack);
    }
  }
}
