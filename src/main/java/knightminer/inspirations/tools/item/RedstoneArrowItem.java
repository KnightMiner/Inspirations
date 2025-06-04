package knightminer.inspirations.tools.item;

import knightminer.inspirations.tools.entity.RedstoneArrow;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RedstoneArrowItem extends ArrowItem {
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
}
