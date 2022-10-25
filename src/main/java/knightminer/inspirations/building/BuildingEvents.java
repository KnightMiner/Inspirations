package knightminer.inspirations.building;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.block.RopeBlock;
import knightminer.inspirations.common.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

@SuppressWarnings({"unused"})
@EventBusSubscriber(modid = Inspirations.modID, bus = Bus.FORGE)
public class BuildingEvents {
  /**
   * Rope ladder event is used to allow sneak right click interaction to have special logic, as opposed to the standard interaction method
   */
  @SubscribeEvent
  static void toggleRopeLadder(PlayerInteractEvent.RightClickBlock event) {
    if (!Config.enableRopeLadder.getAsBoolean() || event.getWorld().isClientSide()) {
      return;
    }

    Level world = event.getWorld();
    BlockPos pos = event.getPos();
    BlockState state = world.getBlockState(pos);
    if (!(state.getBlock() instanceof RopeBlock)) {
      return;
    }

    Player player = event.getPlayer();
    if (state.getValue(RopeBlock.RUNGS) != RopeBlock.Rungs.NONE) {
      if (removeRopeLadder(world, pos, state, player)) {
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
      }
      return;
    }

    if (makeRopeLadder(world, pos, state, event.getFace(), player, event.getItemStack())) {
      event.setCanceled(true);
      event.setCancellationResult(InteractionResult.SUCCESS);
    }
  }

  /**
   * Logic to remove rungs from a rope ladder
   */
  private static boolean removeRopeLadder(Level world, BlockPos pos, BlockState state, Player player) {
    // only remove rungs when sneaking
    if (!player.isCrouching()) {
      return false;
    }

    // remove rungs
    world.setBlockAndUpdate(pos, state.setValue(RopeBlock.RUNGS, RopeBlock.Rungs.NONE));
    RopeBlock rope = (RopeBlock)state.getBlock();
    SoundType soundtype = rope.getSoundType(state, world, pos, player);
    world.playSound(player, pos, soundtype.getBreakSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
    ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(rope.getRungsItem(), RopeBlock.RUNG_ITEM_COUNT), player.getInventory().selected);
    player.stopUsingItem();

    return true;
  }

  /**
   * Logic to add rungs to a rope ladder
   */
  private static boolean makeRopeLadder(Level world, BlockPos pos, BlockState state, @Nullable Direction side, Player player, ItemStack stack) {
    // must have not clicked the bottom and we must have 4 items
    if (side == null || side.getAxis() == Direction.Axis.Y || (stack.getCount() < RopeBlock.RUNG_ITEM_COUNT && !player.isCreative())) {
      return false;
    }

    // ensure we hae the right item
    RopeBlock rope = (RopeBlock)state.getBlock();
    if (stack.getItem() != rope.getRungsItem()) {
      return false;
    }

    // add rungs
    world.setBlockAndUpdate(pos, state.setValue(RopeBlock.RUNGS, RopeBlock.Rungs.fromAxis(side.getClockWise().getAxis())));
    SoundType soundtype = state.getBlock().getSoundType(state, world, pos, player);
    world.playSound(player, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
    if (!player.isCreative()) {
      stack.shrink(RopeBlock.RUNG_ITEM_COUNT);
    }

    return true;
  }
}
