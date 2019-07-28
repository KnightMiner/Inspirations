package knightminer.inspirations.tools;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.tools.item.ItemWaypointCompass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentThorns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static knightminer.inspirations.shared.InspirationsShared.key;
import static knightminer.inspirations.shared.InspirationsShared.lock;
import static knightminer.inspirations.shared.InspirationsShared.materials;

public class ToolsEvents {

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

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void vineBreakEvent(BreakEvent event) {
		if(!Config.harvestHangingVines) {
			return;
		}

		// stop if on client or already canceled
		if(event.isCanceled()) {
			return;
		}
		World world = event.getWorld();
		if(world.isRemote) {
			return;
		}

		// check conditions: must be shearing vines and not creative
		EntityPlayer player = event.getPlayer();
		if(player.capabilities.isCreativeMode) {
			return;
		}
		Block block = event.getState().getBlock();
		if(!(block instanceof BlockVine)) {
			return;
		}
		ItemStack shears = player.getHeldItemMainhand();
		Item item = shears.getItem();
		if(!(item instanceof ItemShears || item.getToolClasses(shears).contains("shears"))) {
			return;
		}

		BlockPos pos = event.getPos().down();
		BlockVine vine = (BlockVine) block;
		IBlockState state = world.getBlockState(pos);

		// iterate down until we find either a non-vine or the vine can stay
		int count = 0;
		while(state.getBlock() == block && vine.isShearable(shears, world, pos) && !vineCanStay(vine, world, state, pos)) {
			count++;
			for(ItemStack stack : vine.onSheared(shears, world, pos, 0)) {
				Block.spawnAsEntity(world, pos, stack);
			}
			pos = pos.down();
			state = world.getBlockState(pos);
		}
		// break all the vines we dropped as items,
		// mainly for safety even though vines should break it themselves
		for(int i = 0; i < count; i++) {
			pos = pos.up();
			world.setBlockToAir(pos);
		}
	}

	private static boolean vineCanStay(BlockVine vine, World world, IBlockState state, BlockPos pos) {
		// check if any of the four sides allows the vine to stay
		for (EnumFacing side : EnumFacing.Plane.HORIZONTAL) {
			if (state.getValue(BlockVine.getPropertyFor(side)) && vine.canAttachTo(world, pos, side.getOpposite())) {
				return true;
			}
		}

		return false;
	}

	@SubscribeEvent
	public static void dropMelon(HarvestDropsEvent event) {
		if(!Config.shearsReclaimMelons || event.getState().getBlock() != Blocks.MELON_BLOCK) {
			return;
		}

		EntityPlayer player = event.getHarvester();
		if(player == null || player.capabilities.isCreativeMode) {
			return;
		}

		ItemStack shears = player.getHeldItemMainhand();
		Item item = shears.getItem();
		if(!(item instanceof ItemShears || item.getToolClasses(shears).contains("shears"))) {
			return;
		}

		// ensure we have 9 melons drop
		List<ItemStack> drops = event.getDrops();
		Iterator<ItemStack> iterator = drops.iterator();
		boolean foundMelon = false;
		while(iterator.hasNext()) {
			ItemStack stack = iterator.next();
			if(stack.getItem() == Items.MELON) {
				if(!foundMelon) {
					stack.setCount(9);
					foundMelon = true;
				} else {
					iterator.remove();
				}
			}
		}
	}

	@SubscribeEvent
	public static void dropExtraSapling(HarvestDropsEvent event) {
		if(!Config.enableCrook) {
			return;
		}

		// must be leaves
		IBlockState state = event.getState();
		Block block = state.getBlock();
		World world = event.getWorld();
		if(!block.isLeaves(state, world, event.getPos())) {
			return;
		}

		EntityPlayer player = event.getHarvester();
		if(player == null || player.capabilities.isCreativeMode) {
			return;
		}

		// must be a crook
		ItemStack crook = player.getHeldItemMainhand();
		Item item = crook.getItem();
		// though if in hoe mode it must be a hoe
		if(Config.hoeCrook) {
			if(!(item instanceof ItemHoe || item.getToolClasses(crook).contains("hoe"))) {
				return;
			}
		} else if(!item.getToolClasses(crook).contains("crook")) {
			return;
		}

		// damage the hoe, breaking leaves does that now
		if(!world.isRemote && Config.hoeCrook) {
			crook.damageItem(1, player);
		}

		// find the sapling
		Random rand = world.rand;
		ItemStack sapling = new ItemStack(
				block.getItemDropped(state, rand, event.getFortuneLevel()),
				1, block.damageDropped(state));

		// ensure there is not already a sapling
		List<ItemStack> drops = event.getDrops();
		for(ItemStack stack : drops) {
			// as soon as we find one, chance to replace it
			if(OreDictionary.itemMatches(sapling, stack, false)) {
				return;
			}
		}

		// did not find it? add it with a chance
		if(rand.nextInt(Config.crookChance) == 0) {
			drops.add(sapling);
		}
	}

	@SubscribeEvent
	public static void breakLeavesFast(BreakSpeed event) {
		// must be leaves
		if(!Config.hoeCrook) {
			return;
		}

		EntityPlayer player = event.getEntityPlayer();
		if(player == null || player.capabilities.isCreativeMode) {
			return;
		}

		IBlockState state = event.getState();
		if(!state.getBlock().isLeaves(state, player.getEntityWorld(), event.getPos())) {
			return;
		}

		// must be a hoe
		ItemStack stack = player.getHeldItemMainhand();
		Item item = stack.getItem();
		if(!(item instanceof ItemHoe || item.getToolClasses(stack).contains("hoe"))) {
			return;
		}

		// set the speed based on the material
		// hoes are dumb, no getter for the material and its protected so an AT could crash other mods
		// so just use a constant speed of 3
		event.setNewSpeed(2);
	}

	@SubscribeEvent
	public static void setWaypoint(RightClickBlock event) {
		ItemStack stack = event.getItemStack();
		Item item = stack.getItem();
		if (!ItemWaypointCompass.isWaypointCompass(stack)) {
			return;
		}
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityBeacon && ((TileEntityBeacon)te).isComplete) {
			if (!world.isRemote) {
				// give the player the linked compass
				ItemStack newStack = new ItemStack(InspirationsTools.waypointCompass, 1, stack.getMetadata());
				ItemWaypointCompass.setNBT(newStack, world, pos);
				if (stack.hasDisplayName()) {
					newStack.setStackDisplayName(stack.getDisplayName());
				}

				// handle stacks of compasses
				stack.shrink(1);
				EntityPlayer player = event.getEntityPlayer();
				if (stack.isEmpty()) {
					player.setHeldItem(event.getHand(), newStack);
				} else {
					ItemHandlerHelper.giveItemToPlayer(player, newStack);
				}
			}
			event.setCanceled(true);
			event.setCancellationResult(EnumActionResult.SUCCESS);
		}
	}

	@SubscribeEvent
	public static void onShieldHit(LivingAttackEvent event) {
		if (!Config.moreShieldEnchantments) {
			return;
		}
		EntityLivingBase target = event.getEntityLiving();
		if (target.world.isRemote || !target.isActiveItemStackBlocking()) {
			return;
		}
		ItemStack stack = target.getActiveItemStack();
		int thorns = EnchantmentHelper.getEnchantmentLevel(Enchantments.THORNS, stack);
		int fire = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, stack);
		int knockback = EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, stack);
		if (thorns == 0 && fire == 0 && knockback == 0) {
			return;
		}

		DamageSource source = event.getSource();
		Entity attacker = source.getImmediateSource();
		if (attacker != null && !target.isEntityInvulnerable(source) && target.canBlockDamageSource(source)) {
			if (thorns > 0 && EnchantmentThorns.shouldHit(thorns, target.world.rand)) {
				attacker.attackEntityFrom(DamageSource.causeThornsDamage(target), EnchantmentThorns.getDamage(thorns, target.world.rand));
				stack.damageItem(1, target);
			}
			if (fire > 0) {
				attacker.setFire(fire * 4);
			}
			if (knockback > 0) {
				if (attacker instanceof EntityLivingBase) {
					((EntityLivingBase)attacker).knockBack(target, knockback * 0.5F, MathHelper.sin(target.rotationYaw * 0.017453292F), -MathHelper.cos(target.rotationYaw * 0.017453292F));
				} else {
					attacker.addVelocity(-MathHelper.sin(target.rotationYaw * 0.017453292F) * knockback * 0.5f, 0.1D, MathHelper.cos(target.rotationYaw * 0.017453292F) * knockback * 0.5f);
				}
			}
		}
	}
}
