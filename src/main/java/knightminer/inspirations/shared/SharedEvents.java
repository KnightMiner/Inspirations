package knightminer.inspirations.shared;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.common.network.MilkablePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = Inspirations.modID, bus = Bus.FORGE)
public class SharedEvents {
  public static final String TAG_MILKCOOLDOWN = "milk_cooldown";

  @SubscribeEvent
  static void updateMilkCooldown(LivingUpdateEvent event) {
    LivingEntity entity = event.getEntityLiving();
    Level world = entity.getCommandSenderWorld();
    // only run every 20 ticks on serverside
    if (world.isClientSide || (world.getGameTime() % 20) != 0) {
      return;
    }

    // runs for both adult cows and squids, based on config
    if ((Config.milkCooldown.get() && entity instanceof Cow && !entity.isBaby())) {
      // if not already cooled down, cool down
      CompoundTag tags = entity.getPersistentData();
      short cooldown = tags.getShort(TAG_MILKCOOLDOWN);
      if (cooldown > 0) {
        tags.putShort(TAG_MILKCOOLDOWN, (short)(cooldown - 1));

        // reached 0, send pack so client knows
        if (cooldown == 1) {
          InspirationsNetwork.sendToClients(world, entity.blockPosition(), new MilkablePacket(entity, true));
        }
      }
    }
  }
}
