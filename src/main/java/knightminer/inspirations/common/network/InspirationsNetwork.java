package knightminer.inspirations.common.network;

import knightminer.inspirations.Inspirations;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;
import slimeknights.mantle.network.AbstractPacket;
import slimeknights.mantle.network.NetworkWrapper;

public class InspirationsNetwork extends NetworkWrapper {

	public static InspirationsNetwork instance = new InspirationsNetwork();

	private InspirationsNetwork() {
		super(Inspirations.modID+":"+"network");
	}

	public void setup() {
		// register all the packets

		// bookshelf
		registerPacket(InventorySlotSyncPacket.class, InventorySlotSyncPacket::encode, InventorySlotSyncPacket::decode, InventorySlotSyncPacket::handle);

		// milk cooldown
		registerPacket(MilkablePacket.class, MilkablePacket::encode, MilkablePacket::decode, MilkablePacket::handle);
	}

	/**
	 * Sends a packet to all players on the network
	 * @param packet  Packet
	 */
	public static void sendToAll(AbstractPacket packet) {
		instance.network.send(PacketDistributor.ALL.noArg(), packet);
	}
	/**
	 *
	 * Sends a packet to a specific player
	 * @param packet  Packet
	 * @param player  Player receiving packet
	 */
	public static void sendTo(AbstractPacket packet, ServerPlayerEntity player) {
		instance.network.send(PacketDistributor.PLAYER.with(() -> player), packet);
	}

	/**
	 * Sends a vanilla packet to a player
	 * @param player  Player receiving packet
	 * @param packet  Packet
	 */
	public static void sendPacket(Entity player, IPacket<?> packet) {
		if(player instanceof ServerPlayerEntity && ((ServerPlayerEntity) player).connection != null) {
			((ServerPlayerEntity) player).connection.sendPacket(packet);
		}
	}

	/**
	 * Sends a packet to all clients near a location
	 * @param world   World, does nothing if not a WorldServer
	 * @param pos     Players too far from this position will not receive the packet
	 * @param packet  Packet
	 */
	public static void sendToClients(World world, BlockPos pos, AbstractPacket packet) {
		if(world instanceof ServerWorld) {
			sendToClients((ServerWorld)world, pos, packet);
		}
	}

	/**
	 * Sends a packet to all clients near a location
	 * @param world   World
	 * @param pos     Players too far from this position will not receive the packet
	 * @param packet  Packet
	 */
	public static void sendToClients(ServerWorld world, BlockPos pos, AbstractPacket packet) {
		Chunk chunk = world.getChunkAt(pos);
		instance.network.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), packet);
	}
}
