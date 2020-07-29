package knightminer.inspirations.common.network;

import knightminer.inspirations.shared.SharedEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import slimeknights.mantle.network.packet.IThreadsafePacket;

@SuppressWarnings("WeakerAccess")
public class MilkablePacket implements IThreadsafePacket {

	private final int entityID;
	private final boolean milkable;

	public MilkablePacket(Entity entity, boolean milkable) {
		this.entityID = entity.getEntityId();
		this.milkable = milkable;
	}

	public MilkablePacket(PacketBuffer buf) {
		this.entityID = buf.readInt();
		this.milkable = buf.readBoolean();
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeInt(entityID);
		buf.writeBoolean(milkable);
	}

	@Override
	public void handleThreadsafe(NetworkEvent.Context context) {
		HandleClient.handle(this);
	}

	/** Simply a separate class to safely load the client side logic */
	private static class HandleClient {
		private static void handle(MilkablePacket packet) {
			assert Minecraft.getInstance().world != null;
			Entity entity = Minecraft.getInstance().world.getEntityByID(packet.entityID);
			if(entity == null) {
				return;
			}

			// value for not milkable does not matter as long as its greater than 0
			entity.getPersistentData().putShort(SharedEvents.TAG_MILKCOOLDOWN, (short)(packet.milkable ? 0 : 100));
		}
	}
}
