package knightminer.inspirations.tweaks.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.mantle.util.BlockEntityHelper;

public record LecternBookPacket(BlockPos pos, ItemStack book) implements IThreadsafePacket {
  public LecternBookPacket(FriendlyByteBuf buffer) {
    this(buffer.readBlockPos(), buffer.readItem());
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeItem(book);
  }

  @Override
  public void handleThreadsafe(Context context) {
    Level level = SafeClientAccess.getLevel();
    if (BlockEntityHelper.isBlockLoaded(level, pos) && level.getBlockEntity(pos) instanceof LecternBlockEntity lectern) {
      lectern.setBook(book);
    }
  }
}
