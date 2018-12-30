package knightminer.inspirations.recipes;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.common.network.MilkablePacket;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import knightminer.inspirations.shared.SharedEvents;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

public class RecipesEvents {

	/**
	 * Event to handle cauldron clicking.
	 * Done though an event instead of the block so we can ensure it runs before other cauldron handlers, since we cancel for non-water
	 * @param event
	 */
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void clickCauldron(RightClickBlock event) {
		if(!Config.enableCauldronRecipes) {
			return;
		}

		EntityPlayer player = event.getEntityPlayer();
		if(player.isSneaking()) {
			return;
		}

		// basic properties
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		IBlockState state = world.getBlockState(pos);
		if(state.getBlock() != Blocks.CAULDRON) {
			return;
		}
		ItemStack stack = event.getItemStack();
		if(stack.isEmpty()) {
			return;
		}

		boolean result = TileCauldron.interact(world, pos, state, player, event.getHand());
		if(result || InspirationsRegistry.isCauldronBlacklist(stack)) {
			event.setCanceled(true);
			event.setCancellationResult(EnumActionResult.SUCCESS);
		}
	}

	@SubscribeEvent
	public static void milkSquid(EntityInteract event) {
		if(!Config.milkSquids) {
			return;
		}

		// only care about cows
		Entity target = event.getTarget();
		if(!(target instanceof EntitySquid)) {
			return;
		}

		// must be holding a glass bottle
		EntityPlayer player = event.getEntityPlayer();
		ItemStack stack = player.getHeldItem(event.getHand());
		if(stack.getItem() == Items.GLASS_BOTTLE) {
			// if has tag, cannot be milked
			NBTTagCompound tags = target.getEntityData();
			if (tags.getShort(SharedEvents.TAG_MILKCOOLDOWN) == 0) {
				// give ink bottle to player
				if (!player.capabilities.isCreativeMode) {
					stack.shrink(1);
				}
				ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(InspirationsRecipes.dyedWaterBottle, 1, EnumDyeColor.BLACK.getDyeDamage()), player.inventory.currentItem);

				// sound
				player.playSound(SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, 1.0F, 1.0F);

				// set cooldown
				tags.setShort(SharedEvents.TAG_MILKCOOLDOWN, Config.milkSquidCooldown);
				if (!event.getWorld().isRemote) {
					InspirationsNetwork.sendToClients(event.getWorld(), target.getPosition(), new MilkablePacket(target, false));
				}
				// set success
				event.setCancellationResult(EnumActionResult.SUCCESS);
				event.setCanceled(true);
			}
		}
	}
}
