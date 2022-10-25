package knightminer.inspirations.common.network;

import knightminer.inspirations.shared.SharedEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import slimeknights.mantle.network.packet.IThreadsafePacket;

@SuppressWarnings("WeakerAccess")
public class MilkablePacket implements IThreadsafePacket {

  private final int entityID;
  private final boolean milkable;

  public MilkablePacket(Entity entity, boolean milkable) {
    this.entityID = entity.getId();
    this.milkable = milkable;
  }

  public MilkablePacket(FriendlyByteBuf buf) {
    this.entityID = buf.readInt();
    this.milkable = buf.readBoolean();
  }

  @Override
  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(entityID);
    buf.writeBoolean(milkable);
  }

  @Override
  public void handleThreadsafe(NetworkEvent.Context context) {
    HandleClient.handle(this);
  }

  /**
   * Simply a separate class to safely load the client side logic
   */
  private static class HandleClient {
    private static void handle(MilkablePacket packet) {
      assert Minecraft.getInstance().level != null;
      Entity entity = Minecraft.getInstance().level.getEntity(packet.entityID);
      if (entity == null) {
        return;
      }

      // value for not milkable does not matter as long as its greater than 0
      entity.getPersistentData().putShort(SharedEvents.TAG_MILKCOOLDOWN, (short)(packet.milkable ? 0 : 100));
    }
  }
}
