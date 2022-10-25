package knightminer.inspirations.utility.item;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;

public class TorchLeverItem extends StandingAndWallBlockItem implements IHidable {
  public TorchLeverItem(Block floorBlock, Block wallBlock, Properties properties) {
    super(floorBlock, wallBlock, properties);
  }

  @Override
  public boolean isEnabled() {
    return Config.enableTorchLever.getAsBoolean();
  }

  @Override
  public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
    if (shouldAddtoItemGroup(group)) {
      super.fillItemCategory(group, items);
    }
  }
}
