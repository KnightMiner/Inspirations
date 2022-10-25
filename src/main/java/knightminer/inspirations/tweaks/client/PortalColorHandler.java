package knightminer.inspirations.tweaks.client;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.MiscUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * Logic to handle getting the color for a portal from stained glass below
 */
public class PortalColorHandler implements BlockColor {
  private static final int DEFAULT_COLOR = 0x9928FF;
  private static final Set<Block> BEACON_COLOR_BLACKLIST = new HashSet<>();
  public static final PortalColorHandler INSTANCE = new PortalColorHandler();

  private PortalColorHandler() {}

  @Override
  public int getColor(BlockState state, @Nullable BlockAndTintGetter world, @Nullable BlockPos pos, int tintValue) {
    if (!Config.customPortalColor.get()) {
      return -1;
    }
    if (world == null || pos == null) {
      return DEFAULT_COLOR;
    }

    // Get the real world, not the fake one so we can look at the blocks far enough below us.
    if (world instanceof RenderChunkRegion) {
      world = ((RenderChunkRegion)world).level;
    }

    // if we are at the top of the chunk, notify the portal above that it needs to update
    if (pos.getY() % 16 == 15) {
      BlockPos above = pos.above();
      if (world.getBlockState(above).getBlock() == Blocks.NETHER_PORTAL) {
        Minecraft mc = Minecraft.getInstance();
        BlockGetter blockGetter = world;
        mc.submitAsync(() -> mc.levelRenderer.blockChanged(blockGetter, above, state, state, 8));
      }
    }

    // iterate down until the first non-portal block
    // can skip every other block as it takes at least 2 from a portal to below a portal
    pos = pos.below();
    while (world.getBlockState(pos).getBlock() == Blocks.NETHER_PORTAL) {
      pos = pos.below();
    }

    return getColorValue(world, pos.below());
  }

  /**
   * Gets the color for a block in the world, uses the same logic as beacon beam colors
   * @param access Block access
   * @param pos    Block pos
   */
  private static int getColorValue(BlockAndTintGetter access, BlockPos pos) {
    BlockState state = access.getBlockState(pos);
    Block block = state.getBlock();
    // stained glass
    if (block instanceof BeaconBeamBlock beacon) {
      return MiscUtil.getColor(beacon.getColor());
    }
    // beacon color fallback
    if (!BEACON_COLOR_BLACKLIST.contains(block)) {
      if (access instanceof LevelReader world) {
        try {
          float[] color = block.getBeaconColorMultiplier(state, world, pos, pos);
          if (color != null && color.length == 3) {
            return MiscUtil.getColorInteger(color);
          }
        } catch (ClassCastException e) {
          Inspirations.log.error("Error getting beacon color for block", e);
          BEACON_COLOR_BLACKLIST.add(block);
        }
      }
    }
    return DEFAULT_COLOR;
  }
}
