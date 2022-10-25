package knightminer.inspirations.common.network;

import knightminer.inspirations.Inspirations;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
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
  public static void sendPacket(Entity player, Packet<?> packet) {
    INSTANCE.sendVanillaPacket(packet, player);
  }

  /**
   * Sends a packet to all clients near a location
   * @param world  World, does nothing if not a WorldServer
   * @param pos    Players too far from this position will not receive the packet
   * @param packet Packet
   */
  public static void sendToClients(@Nullable Level world, BlockPos pos, ISimplePacket packet) {
    if (world instanceof ServerLevel) {
      sendToClients((ServerLevel)world, pos, packet);
    }
  }

  /**
   * Sends a packet to all clients near a location
   * @param world  World
   * @param pos    Players too far from this position will not receive the packet
   * @param packet Packet
   */
  public static void sendToClients(ServerLevel world, BlockPos pos, ISimplePacket packet) {
    INSTANCE.sendToClientsAround(packet, world, pos);
  }
}
