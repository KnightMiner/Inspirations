package knightminer.inspirations.common.network;

import io.netty.buffer.ByteBuf;
import knightminer.inspirations.tweaks.TweaksEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import slimeknights.mantle.network.AbstractPacketThreadsafe;

public class MilkablePacket extends AbstractPacketThreadsafe {

	private int entityID;
	private boolean milkable;

	public MilkablePacket() {}
	public MilkablePacket(Entity entity, boolean milkable) {
		entityID = entity.getEntityId();
		this.milkable = milkable;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(entityID);
		buf.writeBoolean(milkable);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityID = buf.readInt();
		milkable = buf.readBoolean();
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		Entity entity = Minecraft.getMinecraft().world.getEntityByID(entityID);
		if(entity == null) {
			return;
		}

		NBTTagCompound tags = entity.getEntityData();
		// value for not milkable does not matter as long as its greater than 0
		tags.setShort(TweaksEvents.TAG_MILKCOOLDOWN, (short)(milkable ? 0 : 100));
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		// only send to clients
		throw new UnsupportedOperationException("Clientside only");
	}

}
