package knightminer.inspirations.tweaks.util;

import knightminer.inspirations.common.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.CropGrowEvent.Pre;
import net.minecraftforge.eventbus.api.Event.Result;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SmoothGrowthListener implements Consumer<Pre> {

  private final Supplier<Block> crop, seed;

  /**
   * Creates a new event listener for smooth growth
   * @param crop   Original crop to listen for
   * @param seed   Seed to replace for crop growth
   */
  public SmoothGrowthListener(Block crop, Block seed) {
    this.crop = crop.delegate;
    this.seed = seed.delegate;
  }

  @Override
  public void accept(BlockEvent.CropGrowEvent.Pre event) {
    if (!Config.smoothBlockCropGrowth.get()) {
      return;
    }

    // at half growth place the seed, gives us 8 ticks on the block, 8 on the seed instead of 16 on the block
    Block crop = this.crop.get();
    if (event.getState().getBlock() != crop) {
      return;
    }

    // first, place the seed
    IWorld world = event.getWorld();
    BlockPos dest, source;

    // sugar cane fires the event at the source, cactus at the destination
    // however, configurable cane does not fire consistently with Forge, so we just use if the block is at the position as our flag
    BlockPos pos = event.getPos();
    if (world.getBlockState(pos).getBlock() == crop) {
      source = pos;
      dest = pos.above();
    } else {
      // we probably have air at the position, so the crop is one block down
      source = pos.below();
      dest = pos;
    }
    BlockState state = seed.get().defaultBlockState();
    world.setBlock(dest, state, 3);

    // clear age on the block below
    if (world.getBlockState(source).getBlock() == crop) {
      world.setBlock(source, crop.defaultBlockState(), 4);
    }

    // prevent normal growth logic
    event.setResult(Result.DENY);

    // update the block above and fire relevant events
    if (world instanceof World) {
      World casted = (World)world;
      state.neighborChanged(casted, dest, seed.get(), source, false);
      ForgeHooks.onCropsGrowPost(casted, source, state);
    }
  }
}
