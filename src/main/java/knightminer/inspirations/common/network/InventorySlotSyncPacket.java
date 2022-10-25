package knightminer.inspirations.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import slimeknights.mantle.network.packet.IThreadsafePacket;

@SuppressWarnings("WeakerAccess")
public class InventorySlotSyncPacket implements IThreadsafePacket {

  private final ItemStack itemStack;
  private final int slot;
  private final BlockPos pos;

  public InventorySlotSyncPacket(ItemStack itemStack, int slot, BlockPos pos) {
    this.itemStack = itemStack;
    this.pos = pos;
    this.slot = slot;
  }

  public InventorySlotSyncPacket(PacketBuffer buf) {
    this.pos = buf.readBlockPos();
    this.slot = buf.readShort();
    this.itemStack = buf.readItem();
  }

  @Override
  public void encode(PacketBuffer buf) {
    buf.writeBlockPos(pos);
    buf.writeShort(slot);
    buf.writeItemStack(itemStack, false);
  }

  @Override
  public void handleThreadsafe(NetworkEvent.Context context) {
    HandleClient.handle(this);
  }

  /**
   * Simply a separate class to safely load the client side logic
   */
  private static class HandleClient {
    private static void handle(InventorySlotSyncPacket packet) {
      // Only ever sent to players in the same dimension as the position
      // This should never be called on servers, but protect access to the clientside MC.
      assert Minecraft.getInstance().level != null;
      TileEntity tileEntity = Minecraft.getInstance().level.getBlockEntity(packet.pos);
      if (tileEntity != null) {
        tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                  .filter(handler -> handler instanceof IItemHandlerModifiable)
                  .ifPresent(handler -> {
                    ((IItemHandlerModifiable) handler).setStackInSlot(packet.slot, packet.itemStack);
                    Minecraft.getInstance().levelRenderer.blockChanged(null, packet.pos, null, null, 3);
                    ModelDataManager.requestModelDataRefresh(tileEntity);
                  });
      }
    }
  }
}
