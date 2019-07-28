package knightminer.inspirations.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.math.BlockPos;
import slimeknights.mantle.network.AbstractPacketThreadsafe;

public class RenderBlockUpdatePacket extends AbstractPacketThreadsafe {

  protected BlockPos pos;
  public RenderBlockUpdatePacket() {}

  public RenderBlockUpdatePacket(BlockPos pos) {
    this.pos = pos;
  }

  @Override
  public void handleClientSafe(NetHandlerPlayClient netHandler) {
    Minecraft.getMinecraft().renderGlobal.notifyBlockUpdate(null, pos, null, null, 0);
  }

  @Override
  public void handleServerSafe(NetHandlerPlayServer netHandler) {
    // only send to clients
    throw new UnsupportedOperationException("Clientside only");
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    this.pos = readPos(buf);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    writePos(pos, buf);
  }
}
