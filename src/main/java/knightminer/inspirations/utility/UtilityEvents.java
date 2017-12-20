package knightminer.inspirations.utility;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.Util;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

import static knightminer.inspirations.shared.InspirationsShared.lock;

import static knightminer.inspirations.shared.InspirationsShared.key;

public class UtilityEvents {

	@SubscribeEvent
	public static void lockAndUnlock(RightClickBlock event) {
		if(!Config.enableLock) {
			return;
		}

		// first, ensure we have a valid item to use
		EntityPlayer player = event.getEntityPlayer();
		ItemStack stack = player.getHeldItem(event.getHand());
		if(stack.isEmpty()) {
			return;
		}
		ItemStack compare = stack.copy();
		compare.setCount(1);
		boolean isLock = lock.isItemEqual(compare);
		if(isLock || key.isItemEqual(compare)) {
			TileEntity te = event.getWorld().getTileEntity(event.getPos());
			if(te instanceof ILockableContainer) {
				ILockableContainer lockable = (ILockableContainer) te;

				// lock code
				if(isLock) {
					// already locked: display message
					if(lockable.isLocked()) {
						player.sendStatusMessage(new TextComponentTranslation(Util.prefix("lock.fail.locked")), true);
					} else if(!stack.hasDisplayName()) {
						player.sendStatusMessage(new TextComponentTranslation(Util.prefix("lock.fail.blank")), true);
					} else {
						// lock the container
						lockable.setLockCode(new LockCode(stack.getDisplayName()));
						lockable.markDirty();
						if(!player.capabilities.isCreativeMode) {
							stack.shrink(1);
						}
						player.sendStatusMessage(new TextComponentTranslation(Util.prefix("lock.success")), true);
					}

					event.setCanceled(true);
					event.setCancellationResult(EnumActionResult.SUCCESS);
					// if the player is not sneaking, just open the chest as normal with the key
				} else if(player.isSneaking()) {
					if(lockable.isLocked()) {
						// if the key matches the lock, take off the lock and give it to the player
						if(stack.hasDisplayName() && stack.getDisplayName().equals(lockable.getLockCode().getLock())) {
							LockCode code = lockable.getLockCode();
							lockable.setLockCode(LockCode.EMPTY_CODE);
							lockable.markDirty();
							ItemHandlerHelper.giveItemToPlayer(player, lock.copy().setStackDisplayName(code.getLock()));
							player.sendStatusMessage(new TextComponentTranslation(Util.prefix("unlock.success")), true);
						} else {
							player.sendStatusMessage(new TextComponentTranslation(Util.prefix("unlock.fail.no_match")), true);
						}
					} else {
						player.sendStatusMessage(new TextComponentTranslation(Util.prefix("unlock.fail.unlocked")), true);
					}

					event.setCanceled(true);
					event.setCancellationResult(EnumActionResult.SUCCESS);
				}
			}
		}
	}
}
