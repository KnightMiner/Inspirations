package knightminer.inspirations.shared;

import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.common.network.MilkablePacket;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SharedEvents {
	public static final String TAG_MILKCOOLDOWN = "milk_cooldown";

	@SubscribeEvent
	public static void updateMilkCooldown(LivingUpdateEvent event) {
		if(!InspirationsShared.milkCooldownCow && !InspirationsShared.milkCooldownSquid) {
			return;
		}

		EntityLivingBase entity = event.getEntityLiving();
		World world = entity.getEntityWorld();
		// only run every 20 ticks on serverside
		if (world.isRemote || (world.getTotalWorldTime() % 20) != 0) {
			return;
		}

		// runs for both adult cows and squids, based on config
		if ((InspirationsShared.milkCooldownCow && entity instanceof EntityCow && !((EntityCow)entity).isChild())
				|| (InspirationsShared.milkCooldownSquid && entity instanceof EntitySquid)) {

			// if not already cooled down, cool down
			NBTTagCompound tags = entity.getEntityData();
			short cooldown = tags.getShort(TAG_MILKCOOLDOWN);
			if(cooldown > 0) {
				tags.setShort(TAG_MILKCOOLDOWN, (short)(cooldown - 1));

				// reached 0, send pack so client knows
				if(cooldown == 1) {
					InspirationsNetwork.sendToClients(world, entity.getPosition(), new MilkablePacket(entity, true));
				}
			}
		}

	}
}
