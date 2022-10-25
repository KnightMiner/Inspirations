package knightminer.inspirations.common.network;

import knightminer.inspirations.tools.capability.DimensionCompass;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;

import javax.annotation.Nullable;

/**
 * Packet to sync the dimension compass position
 */
public class DimensionCompassPositionPacket implements IThreadsafePacket {
	@Nullable
	private final BlockPos pos;

	public DimensionCompassPositionPacket(@Nullable BlockPos pos) {
		this.pos = pos;
	}

	public DimensionCompassPositionPacket(FriendlyByteBuf buffer) {
		if (buffer.readBoolean()) {
			this.pos = buffer.readBlockPos();
		} else {
			this.pos = null;
		}
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		if (pos == null) {
			buffer.writeBoolean(false);
		} else {
			buffer.writeBoolean(true);
			buffer.writeBlockPos(pos);
		}
	}

	@Override
	public void handleThreadsafe(Context context) {
		HandleClient.handle(this);
	}

	private static class HandleClient {
		private static void handle(DimensionCompassPositionPacket packet) {
			Player player = Minecraft.getInstance().player;
			if (player != null) {
				player.getCapability(DimensionCompass.CAPABILITY).ifPresent(compass -> compass.setEnteredPosition(packet.pos));
			}
		}
	}
}
