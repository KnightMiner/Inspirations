package knightminer.inspirations.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PortalColorUpdatePacket extends RenderBlockUpdatePacket {

  public PortalColorUpdatePacket() {}

  public PortalColorUpdatePacket(BlockPos pos) {
    super(pos);
  }

  @Override
  public void handleClientSafe(NetHandlerPlayClient netHandler) {
    int relative = pos.getY() & 15;
    if (relative == 0) {
      Minecraft.getMinecraft().renderGlobal.notifyBlockUpdate(null, pos, null, null, 0);
    }

    World world = Minecraft.getMinecraft().world;
    BlockPos update = pos.up(16 - relative);
    if (world.getBlockState(update).getBlock() == Blocks.PORTAL) {
      Minecraft.getMinecraft().renderGlobal.notifyBlockUpdate(null, update, null, null, 0);
      if (relative >= 12) {
        update = update.up(16);
        if (world.getBlockState(update).getBlock() == Blocks.PORTAL) {
          Minecraft.getMinecraft().renderGlobal.notifyBlockUpdate(null, update, null, null, 0);
        }
      }
    }
  }
}
