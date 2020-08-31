package knightminer.inspirations.common.network;

import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.recipes.tileentity.CauldronTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.mantle.util.TileEntityHelper;

/**
 * Packet to update the client when cauldron contents change
 */
public class CauldronContentUpatePacket implements IThreadsafePacket {
  private final BlockPos pos;
  private final ICauldronContents contents;

  public CauldronContentUpatePacket(BlockPos pos, ICauldronContents contents) {
    this.pos = pos;
    this.contents = contents;
  }

  public CauldronContentUpatePacket(PacketBuffer buffer) {
    this.pos = buffer.readBlockPos();
    this.contents = CauldronContentTypes.read(buffer);
  }

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeBlockPos(pos);
    CauldronContentTypes.write(contents, buffer);
  }

  @Override
  public void handleThreadsafe(Context context) {
    HandleClient.handle(this);
  }

  /** Once removed client class */
  private static class HandleClient {
    private static void handle(CauldronContentUpatePacket packet) {
      TileEntityHelper.getTile(CauldronTileEntity.class, Minecraft.getInstance().world, packet.pos, true).ifPresent(te -> {
        te.setContents(packet.contents);
      });
    }
  }
}
