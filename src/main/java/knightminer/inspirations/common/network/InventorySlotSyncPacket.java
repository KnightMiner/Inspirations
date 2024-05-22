package knightminer.inspirations.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;

public class InventorySlotSyncPacket implements IThreadsafePacket {

  private final ItemStack itemStack;
  private final int slot;
  private final BlockPos pos;

  public InventorySlotSyncPacket(ItemStack itemStack, int slot, BlockPos pos) {
    this.itemStack = itemStack;
    this.pos = pos;
    this.slot = slot;
  }

  public InventorySlotSyncPacket(FriendlyByteBuf buf) {
    this.pos = buf.readBlockPos();
    this.slot = buf.readShort();
    this.itemStack = buf.readItem();
  }

  @Override
  public void encode(FriendlyByteBuf buf) {
    buf.writeBlockPos(pos);
    buf.writeShort(slot);
    buf.writeItemStack(itemStack, false);
  }

  @Override
  public void handleThreadsafe(Context context) {
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
      BlockEntity tileEntity = Minecraft.getInstance().level.getBlockEntity(packet.pos);
      if (tileEntity != null) {
        tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
          if (handler instanceof IItemHandlerModifiable itemHandler) {
            itemHandler.setStackInSlot(packet.slot, packet.itemStack);
//            Minecraft minecraft = Minecraft.getInstance();
//            tileEntity.requestModelDataUpdate();
//            BlockState state = tileEntity.getBlockState();
//            minecraft.levelRenderer.blockChanged(minecraft.level, packet.pos, state, state, 3);
          }
        });
      }
    }
  }
}
