package knightminer.inspirations.common.network;

import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.recipes.tileentity.CauldronTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.mantle.util.TileEntityHelper;

import javax.annotation.Nullable;

/**
 * Packet to update the client when cauldron contents or level offset change
 */
public class CauldronStateUpdatePacket implements IThreadsafePacket {
  private final BlockPos pos;
  @Nullable
  private final ICauldronContents contents;
  private final int levelOffset;

  /**
   * Updates cauldron contents and level offset from the server
   * @param pos          Cauldron position
   * @param contents     New contents, null if no change
   * @param levelOffset  New level offset
   */
  public CauldronStateUpdatePacket(BlockPos pos, @Nullable ICauldronContents contents, int levelOffset) {
    this.pos = pos;
    this.contents = contents;
    this.levelOffset = levelOffset;
  }

  /**
   * Reads the packet from the buffer
   * @param buffer  Buffer instance
   */
  public CauldronStateUpdatePacket(PacketBuffer buffer) {
    this.pos = buffer.readBlockPos();
    if (buffer.readBoolean()) {
      this.contents = CauldronContentTypes.read(buffer);
    } else {
      this.contents = null;
    }
    this.levelOffset = buffer.readByte();
  }

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeBlockPos(pos);
    if (contents != null) {
      buffer.writeBoolean(true);
      contents.write(buffer);
    } else {
      buffer.writeBoolean(false);
    }
    buffer.writeByte(levelOffset);
  }

  @Override
  public void handleThreadsafe(Context context) {
    HandleClient.handle(this);
  }

  /** Once removed client class */
  private static class HandleClient {
    private static void handle(CauldronStateUpdatePacket packet) {
      TileEntityHelper.getTile(CauldronTileEntity.class, Minecraft.getInstance().world, packet.pos, true).ifPresent(te -> {
        if (te.updateStateAndData(packet.contents, packet.levelOffset)) {
          Util.notifyClientUpdate(te);
        }
      });
    }
  }
}
