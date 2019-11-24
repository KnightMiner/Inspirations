package knightminer.inspirations.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import slimeknights.mantle.network.AbstractPacket;
import slimeknights.mantle.tileentity.InventoryTileEntity;

import java.util.function.Supplier;

public class InventorySlotSyncPacket extends AbstractPacket {

  public ItemStack itemStack;
  public int slot;
  public BlockPos pos;

  private InventorySlotSyncPacket() {
  }

  public InventorySlotSyncPacket(ItemStack itemStack, int slot, BlockPos pos) {
    this.itemStack = itemStack;
    this.pos = pos;
    this.slot = slot;
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> context) {
    // only send to clients.
    switch (context.get().getDirection()) {
      case LOGIN_TO_SERVER:
      case PLAY_TO_SERVER:
        throw new UnsupportedOperationException("Clientside only");
    }
    context.get().setPacketHandled(true);

    // Only ever sent to players in the same dimension as the position
    // This should never be called on servers, but protect access to the clientside MC.
    TileEntity tileEntity = DistExecutor.callWhenOn(Dist.CLIENT, () -> () ->
            Minecraft.getInstance().player.getEntityWorld().getTileEntity(pos)
    );
    if(!(tileEntity instanceof InventoryTileEntity)) {
      return;
    }

    InventoryTileEntity tile = (InventoryTileEntity) tileEntity;
    tile.setInventorySlotContents(slot, itemStack);
    Minecraft.getInstance().worldRenderer.notifyBlockUpdate(null, pos, null, null, 3);
    ModelDataManager.requestModelDataRefresh(tile);
  }

  public static InventorySlotSyncPacket decode(PacketBuffer buf) {
    InventorySlotSyncPacket packet = new InventorySlotSyncPacket();
    packet.pos = packet.readPos(buf);
    packet.slot = buf.readShort();
    packet.itemStack = buf.readItemStack();
    return packet;
  }

  @Override
  public void encode(PacketBuffer buf) {
    writePos(pos, buf);
    buf.writeShort(slot);
    buf.writeItemStack(itemStack, false);
  }
}
