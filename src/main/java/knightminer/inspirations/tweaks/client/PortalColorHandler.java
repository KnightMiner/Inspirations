package knightminer.inspirations.tweaks.client;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * Logic to handle getting the color for a portal from stained glass below
 */
public class PortalColorHandler implements IBlockColor {
  private static final int DEFAULT_COLOR = 0x9928FF;
  private static final Set<Block> BEACON_COLOR_BLACKLIST = new HashSet<>();

  @Override
  public int colorMultiplier(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int tintIndex) {
    if (world == null || pos == null) {
      return DEFAULT_COLOR;
    }

    // iterate down until the first non-portal block
    // can skip every other block as it takes at least 2 from a portal to below a portal
    pos = pos.down(2);
    IBlockState colorState = world.getBlockState(pos);
    Block block = colorState.getBlock();
    while(block == Blocks.PORTAL || block == Blocks.OBSIDIAN) {
      // update iterator
      pos = pos.down(block == Blocks.PORTAL ? 2 : 1);
      colorState = world.getBlockState(pos);
      block = colorState.getBlock();
    }

    return getColorValue(world, pos);
  }

  /**
   * Gets the color for a block in the world, uses the same logic as beacon beam colors
   * @param access  Block access
   * @param pos     Block pos
   * @return
   */
  private static int getColorValue(IBlockAccess access, BlockPos pos) {
    IBlockState state = access.getBlockState(pos);
    Block block = state.getBlock();
    // stained glass
    if (block == Blocks.STAINED_GLASS) {
      return state.getValue(BlockStainedGlass.COLOR).colorValue;
    }
    if (block == Blocks.STAINED_GLASS_PANE) {
      return state.getValue(BlockStainedGlassPane.COLOR).colorValue;
    }
    // beacon color fallback
    if (!BEACON_COLOR_BLACKLIST.contains(block)) {
      World world = null;
      if (access instanceof World) {
        world = (World)access;
      } else if (access instanceof ChunkCache) {
        world = ((ChunkCache)access).world;
      }
      if (world != null) {
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
