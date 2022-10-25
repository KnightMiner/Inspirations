package knightminer.inspirations.tools.item;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import knightminer.inspirations.tools.entity.RedstoneArrow;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RedstoneArrowItem extends ArrowItem implements IHidable {

  public RedstoneArrowItem(Properties builder) {
    super(builder);
  }

  @Override
  public AbstractArrow createArrow(Level world, ItemStack stack, LivingEntity shooter) {
    return new RedstoneArrow(world, shooter);
  }

  @Override
  public boolean isInfinite(ItemStack stack, ItemStack bow, Player player) {
    return false;
  }

  @Override
  public boolean isEnabled() {
    return Config.enableRedstoneCharger.getAsBoolean();
  }

  @Override
  public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
    if (shouldAddtoItemGroup(group)) {
      super.fillItemCategory(group, items);
    }
  }
}
