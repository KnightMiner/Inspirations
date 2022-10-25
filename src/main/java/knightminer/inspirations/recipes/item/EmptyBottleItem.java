package knightminer.inspirations.recipes.item;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

import net.minecraft.world.item.Item.Properties;

/**
 * Item representing an empty potion bottle. Can be filled with water from a source block
 */
public class EmptyBottleItem extends BottleItem implements IHidable {
  private final Supplier<Item> filled;

  /**
   * Creates a new bottle instance
   * @param properties  Item properties 
   * @param filled      Supplier returning the filled potion item
   */
  public EmptyBottleItem(Properties properties, Supplier<Item> filled) {
    super(properties);
    this.filled = filled;
  }

  @Override
  public boolean isEnabled() {
    return Config.enableCauldronPotions.getAsBoolean();
  }

  @Override
  public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
    if (shouldAddtoItemGroup(group)) {
      super.fillItemCategory(group, items);
    }
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);

    // must hit a block
    BlockHitResult trace = getPlayerPOVHitResult(world, player, ClipContext.Fluid.SOURCE_ONLY);
    if (trace.getType() == HitResult.Type.BLOCK) {
      BlockPos pos = trace.getBlockPos();
      if (!world.mayInteract(player, pos)) {
        return InteractionResultHolder.pass(stack);
      }

      // if the block contains water, give filled stack
      if (world.getFluidState(pos).is(FluidTags.WATER)) {
        world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
        return InteractionResultHolder.sidedSuccess(this.turnBottleIntoItem(stack, player, PotionUtils.setPotion(new ItemStack(filled.get()), Potions.WATER)), world.isClientSide());
      }
    }

    return InteractionResultHolder.pass(stack);
  }
}
