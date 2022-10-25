package knightminer.inspirations.building.item;

import knightminer.inspirations.common.item.HidableRetexturedBlockItem;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

/**
 * Extension of a texture block item to make it burnable
 */
public class ShelfItem extends HidableRetexturedBlockItem {
  public ShelfItem(Block block) {
    super(block, ItemTags.WOODEN_SLABS, new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS));
  }

  @Override
  public int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
    return 300;
  }
}
