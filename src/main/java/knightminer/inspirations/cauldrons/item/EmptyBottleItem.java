package knightminer.inspirations.cauldrons.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.function.Supplier;

/**
 * Item representing an empty potion bottle. Can be filled with water from a source block
 */
public class EmptyBottleItem extends BottleItem {
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
