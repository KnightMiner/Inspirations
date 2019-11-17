package knightminer.inspirations.tools;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.tools.item.WaypointCompassItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.ThornsEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShearsItem;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Iterator;
import java.util.List;

public class ToolsEvents {

	@SubscribeEvent
	public static void lockAndUnlock(RightClickBlock event) {
		if(!Config.enableLock.get()) {
			return;
		}

		// first, ensure we have a valid item to use
		PlayerEntity player = event.getEntityPlayer();
		ItemStack stack = player.getHeldItem(event.getHand());

		boolean isKey = stack.getItem() == InspirationsTools.key;
		boolean isLock = stack.getItem() == InspirationsTools.lock;

		if(!isKey && !isLock) {
			return;
		}
		TileEntity te = event.getWorld().getTileEntity(event.getPos());

		if(te instanceof LockableTileEntity) {
			LockableTileEntity lockable = (LockableTileEntity) te;

			LockCode heldCode = new LockCode(stack.getDisplayName().getUnformattedComponentText());

			// lock code
			if(isLock) {
				// already locked: display message
				if(lockable.code != LockCode.EMPTY_CODE) {
					player.sendStatusMessage(new TranslationTextComponent(Util.prefix("lock.fail.locked")), true);
				} else if(!stack.hasDisplayName()) {
					player.sendStatusMessage(new TranslationTextComponent(Util.prefix("lock.fail.blank")), true);
				} else {
					// lock the container
					lockable.code = heldCode;
					lockable.markDirty();
					if(!player.isCreative()) {
						stack.shrink(1);
					}
					player.sendStatusMessage(new TranslationTextComponent(Util.prefix("lock.success")), true);
				}

				event.setCanceled(true);
				event.setCancellationResult(ActionResultType.SUCCESS);
				// if the player is not sneaking, just open the chest as normal with the key
			} else if(player.isSneaking()) {
				if(lockable.code != LockCode.EMPTY_CODE) {
					// if the key matches the lock, take off the lock and give it to the player
					if(lockable.code.func_219964_a(stack)) {
						LockCode code = lockable.code;
						lockable.code = LockCode.EMPTY_CODE;
						lockable.markDirty();
						ItemHandlerHelper.giveItemToPlayer(player,
								new ItemStack(InspirationsTools.lock).setDisplayName(new StringTextComponent(code.lock))
						);
						player.sendStatusMessage(new TranslationTextComponent(Util.prefix("unlock.success")), true);
					} else {
						player.sendStatusMessage(new TranslationTextComponent(Util.prefix("unlock.fail.no_match")), true);
					}
				} else {
					player.sendStatusMessage(new TranslationTextComponent(Util.prefix("unlock.fail.unlocked")), true);
				}

				event.setCanceled(true);
				event.setCancellationResult(ActionResultType.SUCCESS);
			}
		}
	}

	@SubscribeEvent(priority=EventPriority.LOW)
	public static void vineBreakEvent(BreakEvent event) {
		if(!Config.harvestHangingVines.get()) {
			return;
		}

		// stop if on client or already canceled
		if(event.isCanceled()) {
			return;
		}
		if(event.getWorld().isRemote() || !(event.getWorld() instanceof ServerWorld)) {
			return;
		}
		ServerWorld world = (ServerWorld) event.getWorld();

		// check conditions: must be shearing vines and not creative
		PlayerEntity player = event.getPlayer();
		if(player.isCreative()) {
			return;
		}
		Block block = event.getState().getBlock();
		if(!(block instanceof VineBlock)) {
			return;
		}
		ItemStack shears = player.getHeldItemMainhand();
		Item item = shears.getItem();
		if(!(item instanceof ShearsItem || item.getToolTypes(shears).contains(InspirationsRegistry.SHEAR_TYPE))) {
			return;
		}

		BlockPos pos = event.getPos().down();
		VineBlock vine = (VineBlock) block;
		BlockState state = world.getBlockState(pos);

		// iterate down until we find either a non-vine or the vine can stay
		int count = 0;
		while(state.getBlock() == block && vine.isShearable(shears, world, pos) && !vineCanStay(world, state, pos)) {
			count++;
			for(ItemStack stack : vine.getDrops(state, new LootContext.Builder(world)
					.withParameter(LootParameters.TOOL, shears)
					.withParameter(LootParameters.POSITION, pos)
					.withParameter(LootParameters.THIS_ENTITY, player)
			)) {
				Block.spawnAsEntity(world, pos, stack);
			}
			pos = pos.down();
			state = world.getBlockState(pos);
		}
		// break all the vines we dropped as items,
		// mainly for safety even though vines should break it themselves
		for(int i = 0; i < count; i++) {
			pos = pos.up();
			world.removeBlock(pos, false);
		}
	}

	private static boolean vineCanStay(World world, BlockState state, BlockPos pos) {
		// check if any of the four sides allows the vine to stay
		for (Direction side : Direction.Plane.HORIZONTAL) {
			if (state.get(VineBlock.getPropertyFor(side)) && VineBlock.canAttachTo(world, pos.offset(side), side)) {
				return true;
			}
		}

		return false;
	}

	@SubscribeEvent
	public static void dropMelon(HarvestDropsEvent event) {
		if(!Config.shearsReclaimMelons.get() || event.getState().getBlock() != Blocks.MELON) {
			return;
		}

		PlayerEntity player = event.getHarvester();
		if(player == null || player.isCreative()) {
			return;
		}

		ItemStack shears = player.getHeldItemMainhand();
		Item item = shears.getItem();
		if(!(item instanceof ShearsItem || item.getToolTypes(shears).contains(InspirationsRegistry.SHEAR_TYPE))) {
			return;
		}

		// ensure we have 9 melons drop
		List<ItemStack> drops = event.getDrops();
		Iterator<ItemStack> iterator = drops.iterator();
		boolean foundMelon = false;
		while(iterator.hasNext()) {
			ItemStack stack = iterator.next();
			if(stack.getItem() == Items.MELON_SLICE) {
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
	public static void setWaypoint(RightClickBlock event) {
		ItemStack stack = event.getItemStack();

		if (!WaypointCompassItem.isWaypointCompass(stack)) {
			return;
		}
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		TileEntity te = world.getTileEntity(pos);
		if (WaypointCompassItem.beaconIsComplete(te)) {
			if (!world.isRemote) {
				// give the player the linked compass
				ItemStack newStack;
				if (stack.getItem() instanceof WaypointCompassItem) {
					newStack = stack.copy();
				} else {
					newStack = new ItemStack(InspirationsTools.waypointCompasses[DyeColor.WHITE.getId()]);
				}
				WaypointCompassItem.setNBT(newStack, world, pos);
				if (stack.hasDisplayName()) {
					newStack.setDisplayName(stack.getDisplayName());
				}

				// handle stacks of compasses
				stack.shrink(1);
				PlayerEntity player = event.getEntityPlayer();
				if (stack.isEmpty()) {
					player.setHeldItem(event.getHand(), newStack);
				} else {
					ItemHandlerHelper.giveItemToPlayer(player, newStack);
				}
			}
			event.setCanceled(true);
			event.setCancellationResult(ActionResultType.SUCCESS);
		}
	}

	@SubscribeEvent
	public static void onShieldHit(LivingAttackEvent event) {
		if (!Config.moreShieldEnchantments.get()) {
			return;
		}
		LivingEntity target = event.getEntityLiving();
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
		// Apply shield enchantments if the player can be hurt by the source,
		// and they are have blocked it.
		if (attacker != null && !target.isInvulnerableTo(source) && target.canBlockDamageSource(source)) {
			if (thorns > 0 && ThornsEnchantment.shouldHit(thorns, target.world.rand)) {
				attacker.attackEntityFrom(DamageSource.causeThornsDamage(target), ThornsEnchantment.getDamage(thorns, target.world.rand));
				stack.damageItem(1, target, (play) -> play.sendBreakAnimation(target.getActiveHand()));
			}
			if (fire > 0) {
				attacker.setFire(fire * 4);
			}
			if (knockback > 0) {
				if (attacker instanceof LivingEntity) {
					((LivingEntity)attacker).knockBack(target, knockback * 0.5F, MathHelper.sin(target.rotationYaw * 0.017453292F), -MathHelper.cos(target.rotationYaw * 0.017453292F));
				} else {
					attacker.addVelocity(-MathHelper.sin(target.rotationYaw * 0.017453292F) * knockback * 0.5f, 0.1D, MathHelper.cos(target.rotationYaw * 0.017453292F) * knockback * 0.5f);
				}
			}
		}
	}
}
