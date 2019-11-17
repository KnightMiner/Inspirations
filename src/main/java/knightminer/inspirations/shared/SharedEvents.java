package knightminer.inspirations.shared;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.common.network.MilkablePacket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class SharedEvents {
	public static final String TAG_MILKCOOLDOWN = "milk_cooldown";

	public static void updateMilkCooldown(LivingUpdateEvent event) {
		LivingEntity entity = event.getEntityLiving();
		World world = entity.getEntityWorld();
		// only run every 20 ticks on serverside
		if (world.isRemote || (world.getGameTime() % 20) != 0) {
			return;
		}

		// runs for both adult cows and squids, based on config
		if ((Config.milkCooldown.get() && entity instanceof CowEntity && !entity.isChild())
				|| (Config.milkSquids.get() && entity instanceof SquidEntity)) {

			// if not already cooled down, cool down
			CompoundNBT tags = entity.getPersistentData();
			short cooldown = tags.getShort(TAG_MILKCOOLDOWN);
			if(cooldown > 0) {
				tags.putShort(TAG_MILKCOOLDOWN, (short)(cooldown - 1));

				// reached 0, send pack so client knows
				if(cooldown == 1) {
					InspirationsNetwork.sendToClients(world, entity.getPosition(), new MilkablePacket(entity, true));
				}
			}
		}

	}
}
