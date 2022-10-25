package knightminer.inspirations.building.item;

import knightminer.inspirations.common.item.HidableRetexturedBlockItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;

/**
 * Extension of a texture block item to make it burnable
 */
public class ShelfItem extends HidableRetexturedBlockItem {
  public ShelfItem(Block block) {
    super(block, ItemTags.WOODEN_SLABS, new Item.Properties().tab(ItemGroup.TAB_DECORATIONS));
  }

  @Override
  public int getBurnTime(ItemStack itemStack) {
    return 300;
  }
}
