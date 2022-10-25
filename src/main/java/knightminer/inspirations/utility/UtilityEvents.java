package knightminer.inspirations.utility;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
    if (!Config.enableCarpetedPressurePlate.get()) {
      return;
    }

    // must be using carpet
    ItemStack stack = event.getItemStack();
    Block carpetBlock = Block.byItem(stack.getItem());
    if (!(carpetBlock instanceof CarpetBlock)) {
      return;
    }

    // must be clicking a stone pressure plate or the block below one
    World world = event.getWorld();
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
    DyeColor color = ((CarpetBlock)carpetBlock).getColor();
    BlockState state = InspirationsUtility.carpetedPressurePlates.get(color).defaultBlockState();
    state = state.updateShape(Direction.DOWN, world.getBlockState(pos.below()), world, pos, pos.below());

    // play sound
    PlayerEntity player = event.getPlayer();
    SoundType sound = state.getBlock().getSoundType(state, world, pos, player);
    world.playSound(player, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

    // place the block
    if (!world.isClientSide) {
      // and place it
      world.setBlockAndUpdate(pos, state);

      // add statistic
      if (player instanceof ServerPlayerEntity) {
        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)player, pos, stack);
      }

      // take one carpet
      if (!player.isCreative()) {
        stack.shrink(1);
      }
    }
    event.setCanceled(true);
    event.setCancellationResult(ActionResultType.SUCCESS);
  }

  /**
   * Makes clicking a hopper with a pipe place the pipe instead of opening the hopper's GUI
   */
  @SubscribeEvent
  static void clickHopperWithPipe(RightClickBlock event) {
    if (!Config.enablePipe.get() || event.getItemStack().getItem() != InspirationsUtility.pipe.asItem()) {
      return;
    }
    World world = event.getWorld();
    if (world.isClientSide || !(world.getBlockState(event.getPos()).getBlock() instanceof HopperBlock)) {
      return;
    }

    event.setUseBlock(Event.Result.DENY);
    event.setUseItem(Event.Result.ALLOW);
  }
}
