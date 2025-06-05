package knightminer.inspirations.tweaks.network;

import knightminer.inspirations.common.network.InspirationsNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.mantle.util.BlockEntityHelper;

/** Packet for a client to request the lectern book */
public record RequestLecternBookPacket(BlockPos pos) implements IThreadsafePacket {
  public RequestLecternBookPacket(FriendlyByteBuf buffer) {
    this(buffer.readBlockPos());
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
  }

  @Override
  public void handleThreadsafe(Context context) {
    ServerPlayer player = context.getSender();
    if (player != null && BlockEntityHelper.isBlockLoaded(player.level(), pos) && player.level().getBlockEntity(pos) instanceof LecternBlockEntity lectern) {
      ItemStack book = lectern.getBook();
      // skip syncing written books, they are big packets, and we are not going to show them anyways
      if (!book.isEmpty() && !book.is(Items.WRITABLE_BOOK) && !book.is(Items.WRITTEN_BOOK)) {
        InspirationsNetwork.INSTANCE.sendTo(new LecternBookPacket(pos, book), player);
      }
    }
  }
}
