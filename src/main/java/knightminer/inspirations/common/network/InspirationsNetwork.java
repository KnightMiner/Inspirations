package knightminer.inspirations.common.network;

import knightminer.inspirations.Inspirations;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import slimeknights.mantle.network.AbstractPacket;
import slimeknights.mantle.network.NetworkWrapper;

public class InspirationsNetwork extends NetworkWrapper {

	public static InspirationsNetwork instance = new InspirationsNetwork();

	public InspirationsNetwork() {
		super(Inspirations.modID);
	}

	public void setup() {
		// register all the packets

		// bookshelf
		registerPacketClient(InventorySlotSyncPacket.class);

		// milk cooldown
		registerPacketClient(MilkablePacket.class);

		// generic block update
		registerPacketClient(RenderBlockUpdatePacket.class);

		// portal colors
		registerPacketClient(PortalColorUpdatePacket.class);
	}

	public static void sendToAll(AbstractPacket packet) {
		instance.network.sendToAll(packet);
	}

	public static void sendTo(AbstractPacket packet, EntityPlayerMP player) {
		instance.network.sendTo(packet, player);
	}

	public static void sendToClients(World world, BlockPos pos, AbstractPacket packet) {
		if(world instanceof WorldServer) {
			sendToClients((WorldServer)world, pos, packet);
		}
	}

	public static void sendToClients(WorldServer world, BlockPos pos, AbstractPacket packet) {
		Chunk chunk = world.getChunkFromBlockCoords(pos);
		for(EntityPlayer player : world.playerEntities) {
			// only send to relevant players
			if(!(player instanceof EntityPlayerMP)) {
				continue;
			}
			EntityPlayerMP playerMP = (EntityPlayerMP) player;
			if(world.getPlayerChunkMap().isPlayerWatchingChunk(playerMP, chunk.x, chunk.z)) {
				InspirationsNetwork.sendTo(packet, playerMP);
			}
		}
	}
}
