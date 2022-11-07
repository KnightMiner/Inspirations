package knightminer.inspirations.common.network;

import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.cauldrons.block.entity.DyeCauldronBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.mantle.util.BlockEntityHelper;

/** Packet to update the color in a cauldron */
public class CauldronColorUpdatePacket implements IThreadsafePacket {
	private final BlockPos pos;
	private final int color;

	public CauldronColorUpdatePacket(BlockPos pos, int color) {
		this.pos = pos;
		this.color = color;
	}

	public CauldronColorUpdatePacket(FriendlyByteBuf buffer) {
		this.pos = buffer.readBlockPos();
		this.color = buffer.readInt();
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeInt(color);
	}

	@Override
	public void handleThreadsafe(Context context) {
		HandleClient.handle(this);
	}

	/** Once removed client class */
	private static class HandleClient {
		private static void handle(CauldronColorUpdatePacket packet) {
			BlockEntityHelper.get(DyeCauldronBlockEntity.class, Minecraft.getInstance().level, packet.pos, true).ifPresent(te -> {
				if (te.setColor(packet.color)) {
					MiscUtil.notifyClientUpdate(te);
				}
			});
		}
	}
}
