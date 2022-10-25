package knightminer.inspirations.building.item;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

/**
 * Extension of a door item to make it hidable and not burnable
 */
public class GlassDoorBlockItem extends DoubleHighBlockItem implements IHidable {
  public GlassDoorBlockItem(Block blockIn, Properties builder) {
    super(blockIn, builder);
  }

  @Override
  public int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
    return 0;
  }

  @Override
  public boolean isEnabled() {
    return Config.enableGlassDoor.getAsBoolean();
  }

  @Override
  public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
    if (shouldAddtoItemGroup(group)) {
      super.fillItemCategory(group, items);
    }
  }
}
