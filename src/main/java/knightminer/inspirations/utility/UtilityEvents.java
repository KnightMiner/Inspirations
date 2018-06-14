package knightminer.inspirations.utility;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.utility.block.BlockCarpetedPressurePlate;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

import static knightminer.inspirations.shared.InspirationsShared.materials;
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
		if(stack.isEmpty() || stack.getItem() != materials) {
			return;
		}
		int meta = stack.getMetadata();
		boolean isLock = meta == lock.getItemDamage();
		if(isLock || meta == key.getItemDamage()) {
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

	@SubscribeEvent
	public static void placeCarpetOnPressurePlate(RightClickBlock event) {
		if(!Config.enableCarpetedPressurePlate) {
			return;
		}

		// must be using carpet
		ItemStack stack = event.getItemStack();
		if(stack.getItem() != Item.getItemFromBlock(Blocks.CARPET)) {
			return;
		}

		// must be clicking a stone pressure plate or the block below one
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		IBlockState current = world.getBlockState(pos);
		if(current.getBlock() != Blocks.STONE_PRESSURE_PLATE) {
			pos = pos.up();
			current = world.getBlockState(pos);
			if(current.getBlock() != Blocks.STONE_PRESSURE_PLATE) {
				return;
			}
		}

		// determine the state to place
		IBlockState state;
		int meta = stack.getMetadata();
		EnumDyeColor color = EnumDyeColor.byMetadata(meta);
		if(meta < 8) {
			state = InspirationsUtility.carpetedPressurePlate1.getDefaultState()
					.withProperty(BlockCarpetedPressurePlate.COLOR1, color);
		} else {
			state = InspirationsUtility.carpetedPressurePlate2.getDefaultState()
					.withProperty(BlockCarpetedPressurePlate.COLOR2, color);
		}

		// play sound
		EntityPlayer player = event.getEntityPlayer();
		SoundType sound = state.getBlock().getSoundType(state, world, pos, player);
		world.playSound(player, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

		// place the block
		if(!world.isRemote) {
			// and place it
			world.setBlockState(pos, state);

			// add statistic
			if (player instanceof EntityPlayerMP) {
				CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
			}

			// take one carpet
			if(!player.capabilities.isCreativeMode) {
				stack.shrink(1);
			}
		}
		event.setCanceled(true);
		event.setCancellationResult(EnumActionResult.SUCCESS);
	}
}
