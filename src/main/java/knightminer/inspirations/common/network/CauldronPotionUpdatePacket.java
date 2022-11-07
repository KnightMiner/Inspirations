package knightminer.inspirations.common.network;

import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.cauldrons.block.entity.PotionCauldronBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.mantle.util.BlockEntityHelper;

/** Packet to update the potion in a cauldron */
public class CauldronPotionUpdatePacket implements IThreadsafePacket {
	private final BlockPos pos;
	private final Potion potion;

	public CauldronPotionUpdatePacket(BlockPos pos, Potion potion) {
		this.pos = pos;
		this.potion = potion;
	}

	public CauldronPotionUpdatePacket(FriendlyByteBuf buffer) {
		this.pos = buffer.readBlockPos();
		this.potion = buffer.readRegistryIdUnsafe(ForgeRegistries.POTIONS);
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeRegistryIdUnsafe(ForgeRegistries.POTIONS, potion);
	}

	@Override
	public void handleThreadsafe(Context context) {
		HandleClient.handle(this);
	}

	/** Once removed client class */
	private static class HandleClient {
		private static void handle(CauldronPotionUpdatePacket packet) {
			BlockEntityHelper.get(PotionCauldronBlockEntity.class, Minecraft.getInstance().level, packet.pos, true).ifPresent(te -> {
				if (te.setPotion(packet.potion)) {
					MiscUtil.notifyClientUpdate(te);
				}
			});
		}
	}
}
