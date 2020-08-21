package knightminer.inspirations.recipes.item;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.item.HidableItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.items.ItemHandlerHelper;

public class SimpleDyedBottleItem extends HidableItem {
  private final DyeColor color;
  public SimpleDyedBottleItem(Properties props, DyeColor color) {
    super(props, Config::enableCauldronDyeing);
    this.color = color;
  }

  public DyeColor getColor() {
    return color;
  }

  /**
   * Dye sheep on right click with a bottle
   */
  @Override
  public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
    if (target instanceof SheepEntity) {
      SheepEntity sheep = (SheepEntity)target;
      if (!sheep.getSheared() && sheep.getFleeceColor() != color) {
        sheep.setFleeceColor(color);
        player.playSound(SoundEvents.ITEM_BOTTLE_EMPTY, 1.0F, 1.0F);

        // give back bottle;
        ItemStack bottle = stack.getContainerItem();
        if (stack.getCount() == 1) {
          player.setHeldItem(hand, bottle);
        } else {
          stack.shrink(1);
          ItemHandlerHelper.giveItemToPlayer(player, bottle);
        }
      }
      return ActionResultType.SUCCESS;
    }
    return ActionResultType.PASS;
  }
}
