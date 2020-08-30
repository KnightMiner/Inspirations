package knightminer.inspirations.tweaks.client;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IBeaconBeamColorProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * Logic to handle getting the color for a portal from stained glass below
 */
public class PortalColorHandler implements IBlockColor {
  private static final int DEFAULT_COLOR = 0x9928FF;
  private static final Set<Block> BEACON_COLOR_BLACKLIST = new HashSet<>();
  public static final PortalColorHandler INSTANCE = new PortalColorHandler();

  private PortalColorHandler() {}

  @Override
  public int getColor(BlockState state, @Nullable IBlockDisplayReader world, @Nullable BlockPos pos, int tintValue) {
    if (!Config.customPortalColor.get()) {
      return -1;
    }
    if (world == null || pos == null) {
      return DEFAULT_COLOR;
    }

    // Get the real world, not the fake one so we can look at the blocks far enough below us.
    if (world instanceof ChunkRenderCache) {
      world = ((ChunkRenderCache)world).world;
    }

    // if we are at the top of the chunk, notify the portal above that it needs to update
    if (pos.getY() % 16 == 15) {
      BlockPos above = pos.up();
      if (world.getBlockState(above).getBlock() == Blocks.NETHER_PORTAL) {
        Minecraft mc = Minecraft.getInstance();
        mc.deferTask(() -> mc.worldRenderer.notifyBlockUpdate(null, above, null, null, 8));
      }
    }

    // iterate down until the first non-portal block
    // can skip every other block as it takes at least 2 from a portal to below a portal
    pos = pos.down();
    while (world.getBlockState(pos).getBlock() == Blocks.NETHER_PORTAL) {
      pos = pos.down();
    }

    return getColorValue(world, pos.down());
  }

  /**
   * Gets the color for a block in the world, uses the same logic as beacon beam colors
   * @param access Block access
   * @param pos    Block pos
   */
  private static int getColorValue(IBlockDisplayReader access, BlockPos pos) {
    BlockState state = access.getBlockState(pos);
    Block block = state.getBlock();
    // stained glass
    if (block instanceof IBeaconBeamColorProvider) {
      return ((IBeaconBeamColorProvider)block).getColor().getColorValue();
    }
    // beacon color fallback
    if (!BEACON_COLOR_BLACKLIST.contains(block)) {
      if (access instanceof IWorldReader) {
        IWorldReader world = (IWorldReader)access;
        try {
          float[] color = block.getBeaconColorMultiplier(state, world, pos, pos);
          if (color != null && color.length == 3) {
            return Util.getColorInteger(color);
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
