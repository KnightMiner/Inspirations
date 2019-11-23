package knightminer.inspirations.common.network;

import knightminer.inspirations.shared.SharedEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import slimeknights.mantle.network.AbstractPacket;

import java.util.function.Supplier;

public class MilkablePacket extends AbstractPacket {

	private int entityID;
	private boolean milkable;

	private MilkablePacket() {}

	public MilkablePacket(Entity entity, boolean milkable) {
		entityID = entity.getEntityId();
		this.milkable = milkable;
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeInt(entityID);
		buf.writeBoolean(milkable);
	}

	public static MilkablePacket decode(PacketBuffer buf) {
		MilkablePacket packet = new MilkablePacket();
		packet.entityID = buf.readInt();
		packet.milkable = buf.readBoolean();
		return packet;
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

		// This should never be called on servers, but protect access to the clientside MC.
		Entity entity = DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().world.getEntityByID(entityID));
		if(entity == null) {
			return;
		}

		CompoundNBT tags = entity.getPersistentData();
		// value for not milkable does not matter as long as its greater than 0
		tags.putShort(SharedEvents.TAG_MILKCOOLDOWN, (short)(milkable ? 0 : 100));
	}

}
