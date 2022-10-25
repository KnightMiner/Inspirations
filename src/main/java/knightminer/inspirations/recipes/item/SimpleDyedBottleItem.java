package knightminer.inspirations.recipes.item;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraft.world.item.Item.Properties;

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
  public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
    if (shouldAddtoItemGroup(group)) {
      super.fillItemCategory(group, items);
    }
  }

  @Override
  public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
    // this method reimplements many entity dye behaviors to properly return bottles
    DyeColor color = getDyeColor();

    // dye sheep
    if (target instanceof Sheep) {
      Sheep sheep = (Sheep)target;
      if (!sheep.isSheared() && sheep.getColor() != color) {
        sheep.setColor(color);
        player.playSound(SoundEvents.BOTTLE_EMPTY, 1.0F, 1.0F);

        // give back bottle;
        consumeItem(player, hand, stack);
      }
      return InteractionResult.SUCCESS;
    }

    return InteractionResult.PASS;
  }

  /**
   * Helper to consume an item, returning the bottle containr
   * @param player  Player entity
   * @param hand    Hand used
   * @param stack   Consumed stack
   */
  private static void consumeItem(Player player, InteractionHand hand, ItemStack stack) {
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
