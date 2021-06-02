package knightminer.inspirations.common.network;

import knightminer.inspirations.Inspirations;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import slimeknights.mantle.network.NetworkWrapper;
import slimeknights.mantle.network.packet.ISimplePacket;

import javax.annotation.Nullable;

public class InspirationsNetwork extends NetworkWrapper {
  public static final InspirationsNetwork INSTANCE = new InspirationsNetwork();

  private InspirationsNetwork() {
    super(Inspirations.getResource("network"));
  }

  /**
   * Called during mod construction to register all packets
   */
  public void setup() {
    registerPacket(InventorySlotSyncPacket.class, InventorySlotSyncPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    registerPacket(MilkablePacket.class, MilkablePacket::new, NetworkDirection.PLAY_TO_CLIENT);
    registerPacket(CauldronStateUpdatePacket.class, CauldronStateUpdatePacket::new, NetworkDirection.PLAY_TO_CLIENT);
    registerPacket(CauldronTransformUpatePacket.class, CauldronTransformUpatePacket::new, NetworkDirection.PLAY_TO_CLIENT);
    registerPacket(DimensionCompassPositionPacket.class, DimensionCompassPositionPacket::new, NetworkDirection.PLAY_TO_CLIENT);
  }

  /**
   * Sends a vanilla packet to a player
   * @param player Player receiving packet
   * @param packet Packet
   */
  public static void sendPacket(Entity player, IPacket<?> packet) {
    INSTANCE.sendVanillaPacket(packet, player);
  }

  /**
   * Sends a packet to all clients near a location
   * @param world  World, does nothing if not a WorldServer
   * @param pos    Players too far from this position will not receive the packet
   * @param packet Packet
   */
  public static void sendToClients(@Nullable World world, BlockPos pos, ISimplePacket packet) {
    if (world instanceof ServerWorld) {
      sendToClients((ServerWorld)world, pos, packet);
    }
  }

  /**
   * Sends a packet to all clients near a location
   * @param world  World
   * @param pos    Players too far from this position will not receive the packet
   * @param packet Packet
   */
  public static void sendToClients(ServerWorld world, BlockPos pos, ISimplePacket packet) {
    INSTANCE.sendToClientsAround(packet, world, pos);
  }
}
