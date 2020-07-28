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

import static net.minecraft.state.properties.BlockStateProperties.AGE_0_15;

public class SmoothGrowthListener implements Consumer<Pre> {

  private final Supplier<Block> crop, seed;
  private final boolean source;

  /**
   * Creates a new event listener for smooth growth
   * @param crop    Original crop to listen for
   * @param seed    Seed to replace for crop growth
   * @param source  If true, the event is fired at the source, if false it is fired at the destination
   */
  public SmoothGrowthListener(Block crop, Block seed, boolean source) {
    this.crop = crop.delegate;
    this.seed = seed.delegate;
    this.source = source;
  }

  @Override
  public void accept(BlockEvent.CropGrowEvent.Pre event) {
    if (!Config.smoothBlockCropGrowth()) {
      return;
    }

    BlockState current = event.getState();
    // at half growth place the seed, gives us 8 ticks on the block, 8 on the seed instead of 16 on the block
    if (event.getState().getBlock() != crop.get() || !current.hasProperty(AGE_0_15) || current.get(AGE_0_15) < 7) {
      return;
    }

    // first, place the seed
    IWorld world = event.getWorld();
    BlockPos dest, source;

    // sugar cane fires the event at the source, cactus at the destination
    if (this.source) {
      source = event.getPos();
      dest = source.up();
    } else {
      dest = event.getPos();
      source = dest.down();
    }
    BlockState state = seed.get().getDefaultState();
    world.setBlockState(dest, state, 3);

    // clear age on the block below
    if (world.getBlockState(source).getBlock() == crop.get()) {
      world.setBlockState(source, crop.get().getDefaultState(), 4);
    }

    // prevent normal growth logic
    event.setResult(Result.DENY);

    // update the block above and fire relevant events
    if (world instanceof World) {
      World casted = (World) world;
      state.neighborChanged(casted, dest, seed.get(), source, false);
      ForgeHooks.onCropsGrowPost(casted, source, state);
    }
  }
}
