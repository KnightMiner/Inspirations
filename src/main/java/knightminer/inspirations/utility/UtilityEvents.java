package knightminer.inspirations.utility;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = Inspirations.modID, bus = Bus.FORGE)
public class UtilityEvents {
  @SubscribeEvent
  public static void placeCarpetOnPressurePlate(RightClickBlock event) {
    if (!Config.enableCarpetedPressurePlate.getAsBoolean()) {
      return;
    }

    // must be using carpet
    ItemStack stack = event.getItemStack();
    Block carpetBlock = Block.byItem(stack.getItem());
    if (!(carpetBlock instanceof WoolCarpetBlock)) {
      return;
    }

    // must be clicking a stone pressure plate or the block below one
    Level world = event.getWorld();
    BlockPos pos = event.getPos();
    BlockState current = world.getBlockState(pos);
    if (current.getBlock() != Blocks.STONE_PRESSURE_PLATE) {
      pos = pos.above();
      current = world.getBlockState(pos);
      if (current.getBlock() != Blocks.STONE_PRESSURE_PLATE) {
        return;
      }
    }

    // determine the state to place
    DyeColor color = ((WoolCarpetBlock)carpetBlock).getColor();
    BlockState state = InspirationsUtility.carpetedPressurePlates.get(color).defaultBlockState();
    state = state.updateShape(Direction.DOWN, world.getBlockState(pos.below()), world, pos, pos.below());

    // play sound
    Player player = event.getPlayer();
    SoundType sound = state.getBlock().getSoundType(state, world, pos, player);
    world.playSound(player, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

    // place the block
    if (!world.isClientSide) {
      // and place it
      world.setBlockAndUpdate(pos, state);

      // add statistic
      if (player instanceof ServerPlayer) {
        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, pos, stack);
      }

      // take one carpet
      if (!player.isCreative()) {
        stack.shrink(1);
      }
    }
    event.setCanceled(true);
    event.setCancellationResult(InteractionResult.SUCCESS);
  }

  /**
   * Makes clicking a hopper with a pipe place the pipe instead of opening the hopper's GUI
   */
  @SubscribeEvent
  static void clickHopperWithPipe(RightClickBlock event) {
    if (!Config.enablePipe.getAsBoolean() || event.getItemStack().getItem() != InspirationsUtility.pipe.asItem()) {
      return;
    }
    Level world = event.getWorld();
    if (world.isClientSide || !(world.getBlockState(event.getPos()).getBlock() instanceof HopperBlock)) {
      return;
    }

    event.setUseBlock(Event.Result.DENY);
    event.setUseItem(Event.Result.ALLOW);
  }
}
