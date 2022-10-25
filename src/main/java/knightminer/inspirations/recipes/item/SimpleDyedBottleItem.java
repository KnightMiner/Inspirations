package knightminer.inspirations.recipes.item;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraft.item.Item.Properties;

/**
 * Item for a dye item that is also a bottle.
 */
public class SimpleDyedBottleItem extends DyeItem implements IHidable {
  /**
   *
   * @param props    Item properties
   * @param vanilla  Vanilla dye this is based on. Used to correct the dye map and set the color
   */
  public SimpleDyedBottleItem(Properties props, DyeItem vanilla) {
    super(vanilla.getDyeColor(), props);
    ITEM_BY_COLOR.put(vanilla.getDyeColor(), vanilla);
  }

  @Override
  public boolean isEnabled() {
    return Config.enableCauldronDyeing.getAsBoolean();
  }

  @Override
  public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
    if (shouldAddtoItemGroup(group)) {
      super.fillItemCategory(group, items);
    }
  }

  @Override
  public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
    // this method reimplements many entity dye behaviors to properly return bottles
    DyeColor color = getDyeColor();

    // dye sheep
    if (target instanceof SheepEntity) {
      SheepEntity sheep = (SheepEntity)target;
      if (!sheep.isSheared() && sheep.getColor() != color) {
        sheep.setColor(color);
        player.playSound(SoundEvents.BOTTLE_EMPTY, 1.0F, 1.0F);

        // give back bottle;
        consumeItem(player, hand, stack);
      }
      return ActionResultType.SUCCESS;
    }

    return ActionResultType.PASS;
  }

  /**
   * Helper to consume an item, returning the bottle containr
   * @param player  Player entity
   * @param hand    Hand used
   * @param stack   Consumed stack
   */
  private static void consumeItem(PlayerEntity player, Hand hand, ItemStack stack) {
    if (!player.isCreative()) {
      ItemStack bottle = stack.getContainerItem().copy();
      if (stack.getCount() == 1) {
        player.setItemInHand(hand, bottle);
      } else {
        stack.shrink(1);
        ItemHandlerHelper.giveItemToPlayer(player, bottle);
      }
    }
  }
}
