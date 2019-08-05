package knightminer.inspirations.tweaks.client;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * Logic to handle getting the color for a portal from stained glass below
 */
public class PortalColorHandler implements IBlockColor, IWorldEventListener {
  private static final int DEFAULT_COLOR = 0x9928FF;
  private static final Set<Block> BEACON_COLOR_BLACKLIST = new HashSet<>();
  public static final PortalColorHandler INSTANCE = new PortalColorHandler();

  private PortalColorHandler() {}

  @Override
  public int colorMultiplier(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int tintIndex) {
    if (world == null || pos == null) {
      return DEFAULT_COLOR;
    }

    // iterate down until the first non-portal block
    // can skip every other block as it takes at least 2 from a portal to below a portal
    pos = pos.down();
    while(world.getBlockState(pos).getBlock() == Blocks.PORTAL) {
      pos = pos.down();
    }

    return getColorValue(world, pos.down());
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

  @Override
  public void notifyBlockUpdate(World world, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
    // during world load viewFrustum is null, but its used by notifyBlockUpdate without safety checks
    if (Minecraft.getMinecraft().renderGlobal.viewFrustum == null) {
      return;
    }
    pos = pos.up(2);
    if (world.getBlockState(pos).getBlock() == Blocks.PORTAL) {
      updatePortal(world, pos, flags);
    }
  }

  private void updatePortal(World world, BlockPos pos, int flags) {
    RenderGlobal render = Minecraft.getMinecraft().renderGlobal;
    int relative = pos.getY() & 15;
    // at Y=2, the color block is in the same chunk as the portal
    // at Y=1, the color block is next to the border so it updates this chunk
    // ay Y=0, this chunk does not update
    if(relative == 0) {
      render.notifyBlockUpdate(null, pos, null, null, flags);
    }

    // update the bottom of the chunk above, lowest position that might have the portal
    BlockPos update = pos.up(16 - relative);
    if(world.getBlockState(update).getBlock() == Blocks.PORTAL) {
      render.notifyBlockUpdate(null, update, null, null, flags);
      // portals are at most 21 blocks tall, only possible at positions 12 and above
      if(relative >= 12) {
        update = update.up(16);
        if(world.getBlockState(update).getBlock() == Blocks.PORTAL) {
          render.notifyBlockUpdate(null, update, null, null, flags);
        }
      }
    }
  }

  /* Necessary methods for the interface */
  @Override
  public void notifyLightSet(BlockPos pos) {}

  @Override
  public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) { }

  @Override
  public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) { }

  @Override
  public void playRecord(SoundEvent soundIn, BlockPos pos) { }

  @Override
  public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) { }

  @Override
  public void spawnParticle(int id, boolean ignoreRange, boolean b, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters) { }

  @Override
  public void onEntityAdded(Entity entityIn) {}

  @Override
  public void onEntityRemoved(Entity entityIn) {}

  @Override
  public void broadcastSound(int soundID, BlockPos pos, int data) {}

  @Override
  public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {}

  @Override
  public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {}
}
