package knightminer.inspirations.building.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

/**
 * Extension of a texture block item to make it burnable
 */
public class ShelfItem extends BlockItem {
  public ShelfItem(Block block, Item.Properties properties) {
    super(block, properties);
  }

  @Override
  public int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
    return 300;
  }
}
