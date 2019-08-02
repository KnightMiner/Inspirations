package knightminer.inspirations.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.fml.network.NetworkEvent;

import slimeknights.mantle.network.AbstractPacketThreadsafe;
import slimeknights.mantle.tileentity.InventoryTileEntity;

import java.util.function.Supplier;

public class InventorySlotSyncPacket extends AbstractPacketThreadsafe {

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

    // only ever sent to players in the same dimension as the position
    TileEntity tileEntity = Minecraft.getInstance().player.getEntityWorld().getTileEntity(pos);
    if(!(tileEntity instanceof InventoryTileEntity)) {
      return;
    }

    InventoryTileEntity tile = (InventoryTileEntity) tileEntity;
    tile.setInventorySlotContents(slot, itemStack);
    ModelDataManager.requestModelDataRefresh(tile);
  }

  public static InventorySlotSyncPacket decode(PacketBuffer buf) {
    InventorySlotSyncPacket packet = new InventorySlotSyncPacket();
    packet.pos = packet.readPos(buf);
    packet.slot = buf.readShort();
    packet.itemStack = buf.readItemStack();
    return packet;
  }

  public void encode(PacketBuffer buf) {
    writePos(pos, buf);
    buf.writeShort(slot);
    buf.writeItemStack(itemStack, false);
  }
}
