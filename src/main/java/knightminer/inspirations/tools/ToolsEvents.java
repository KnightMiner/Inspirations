package knightminer.inspirations.tools;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.VineBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.ThornsEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

@SuppressWarnings("unused")
public class ToolsEvents {
  @SubscribeEvent
  public static void lockAndUnlock(RightClickBlock event) {
    if (!Config.enableLock.get()) {
      return;
    }

    // first, ensure we have a valid item to use
    PlayerEntity player = event.getPlayer();
    ItemStack stack = player.getItemInHand(event.getHand());

    boolean isKey = stack.getItem() == InspirationsTools.key;
    boolean isLock = stack.getItem() == InspirationsTools.lock;

    if (!isKey && !isLock) {
      return;
    }
    TileEntity te = event.getWorld().getBlockEntity(event.getPos());

    if (te instanceof LockableTileEntity) {
      LockableTileEntity lockable = (LockableTileEntity)te;

      LockCode heldCode = new LockCode(stack.getHoverName().getContents());

      // lock code
      if (isLock) {
        // already locked: display message
        if (lockable.lockKey != LockCode.NO_LOCK) {
          player.displayClientMessage(new TranslationTextComponent(Inspirations.prefix("lock.fail.locked")), true);
        } else if (!stack.hasCustomHoverName()) {
          player.displayClientMessage(new TranslationTextComponent(Inspirations.prefix("lock.fail.blank")), true);
        } else {
          // lock the container
          lockable.lockKey = heldCode;
          lockable.setChanged();
          if (!player.isCreative()) {
            stack.shrink(1);
          }
          player.displayClientMessage(new TranslationTextComponent(Inspirations.prefix("lock.success")), true);
        }

        event.setCanceled(true);
        event.setCancellationResult(ActionResultType.SUCCESS);
        // if the player is not sneaking, just open the chest as normal with the key
      } else if (player.isCrouching()) {
        if (lockable.lockKey != LockCode.NO_LOCK) {
          // if the key matches the lock, take off the lock and give it to the player
          if (lockable.lockKey.unlocksWith(stack)) {
            LockCode code = lockable.lockKey;
            lockable.lockKey = LockCode.NO_LOCK;
            lockable.setChanged();
            ItemHandlerHelper.giveItemToPlayer(player,
                                               new ItemStack(InspirationsTools.lock).setHoverName(new StringTextComponent(code.key))
                                              );
            player.displayClientMessage(new TranslationTextComponent(Inspirations.prefix("unlock.success")), true);
          } else {
            player.displayClientMessage(new TranslationTextComponent(Inspirations.prefix("unlock.fail.no_match")), true);
          }
        } else {
          player.displayClientMessage(new TranslationTextComponent(Inspirations.prefix("unlock.fail.unlocked")), true);
        }

        event.setCanceled(true);
        event.setCancellationResult(ActionResultType.SUCCESS);
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public static void vineBreakEvent(BreakEvent event) {
    if (!Config.harvestHangingVines.get()) {
      return;
    }

    // stop if on client or already canceled
    if (event.isCanceled()) {
      return;
    }
    if (event.getWorld().isClientSide() || !(event.getWorld() instanceof ServerWorld)) {
      return;
    }
    ServerWorld world = (ServerWorld)event.getWorld();

    // check conditions: must be shearing vines and not creative
    PlayerEntity player = event.getPlayer();
    if (player.isCreative()) {
      return;
    }
    Block block = event.getState().getBlock();
    if (!(block instanceof VineBlock)) {
      return;
    }
    ItemStack shears = player.getMainHandItem();
    Item item = shears.getItem();
    if (!(item instanceof ShearsItem || item.getToolTypes(shears).contains(InspirationsRegistry.SHEAR_TYPE))) {
      return;
    }

    BlockPos pos = event.getPos().below();
    VineBlock vine = (VineBlock)block;
    BlockState state = world.getBlockState(pos);

    // iterate down until we find either a non-vine or the vine can stay
    int count = 0;
    while (state.getBlock() == block && vine.isShearable(shears, world, pos) && !vineCanStay(world, state, pos)) {
      count++;
      for (ItemStack stack : state.getDrops(new LootContext.Builder(world)
                                                .withParameter(LootParameters.TOOL, shears)
                                                .withParameter(LootParameters.ORIGIN, Vector3d.atCenterOf(pos))
                                                .withParameter(LootParameters.THIS_ENTITY, player)
                                           )) {
        Block.popResource(world, pos, stack);
      }
      pos = pos.below();
      state = world.getBlockState(pos);
    }
    // break all the vines we dropped as items,
    // mainly for safety even though vines should break it themselves
    for (int i = 0; i < count; i++) {
      pos = pos.above();
      world.removeBlock(pos, false);
    }
  }

  private static boolean vineCanStay(World world, BlockState state, BlockPos pos) {
    // check if any of the four sides allows the vine to stay
    for (Direction side : Direction.Plane.HORIZONTAL) {
      if (state.getValue(VineBlock.getPropertyForFace(side)) && VineBlock.isAcceptableNeighbour(world, pos.relative(side), side)) {
        return true;
      }
    }

    return false;
  }

  /* TODO: move to global loot tables
  @SubscribeEvent
  public static void dropMelon(HarvestDropsEvent event) {
    if (!Config.shearsReclaimMelons.get() || event.getState().getBlock() != Blocks.MELON) {
      return;
    }

    PlayerEntity player = event.getHarvester();
    if (player == null || player.isCreative()) {
      return;
    }

    ItemStack shears = player.getHeldItemMainhand();
    Item item = shears.getItem();
    if (!(item instanceof ShearsItem || item.getToolTypes(shears).contains(InspirationsRegistry.SHEAR_TYPE))) {
      return;
    }

    // ensure we have 9 melons drop
    List<ItemStack> drops = event.getDrops();
    Iterator<ItemStack> iterator = drops.iterator();
    boolean foundMelon = false;
    while (iterator.hasNext()) {
      ItemStack stack = iterator.next();
      if (stack.getItem() == Items.MELON_SLICE) {
        if (!foundMelon) {
          stack.setCount(9);
          foundMelon = true;
        } else {
          iterator.remove();
        }
      }
    }
  }
   */

  @SubscribeEvent
  static void onShieldHit(LivingAttackEvent event) {
    if (!Config.moreShieldEnchantments.get()) {
      return;
    }
    LivingEntity target = event.getEntityLiving();
    if (target.level.isClientSide || !target.isBlocking()) {
      return;
    }
    ItemStack stack = target.getUseItem();
    int thorns = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.THORNS, stack);
    int fire = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, stack);
    int knockback = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.KNOCKBACK, stack);
    if (thorns == 0 && fire == 0 && knockback == 0) {
      return;
    }

    DamageSource source = event.getSource();
    Entity attacker = source.getDirectEntity();
    // Apply shield enchantments if the player can be hurt by the source,
    // and they are have blocked it.
    if (attacker != null && !target.isInvulnerableTo(source) && target.isDamageSourceBlocked(source)) {
      if (thorns > 0 && ThornsEnchantment.shouldHit(thorns, target.level.random)) {
        attacker.hurt(DamageSource.thorns(target), ThornsEnchantment.getDamage(thorns, target.level.random));
        stack.hurtAndBreak(1, target, (play) -> play.broadcastBreakEvent(target.getUsedItemHand()));
      }
      if (fire > 0) {
        attacker.setSecondsOnFire(fire * 4);
      }
      if (knockback > 0) {
        if (attacker instanceof LivingEntity) {
          ((LivingEntity)attacker).knockback(knockback * 0.5F, MathHelper.sin(target.yRot * 0.017453292F), -MathHelper.cos(target.yRot * 0.017453292F));
          if (attacker instanceof ServerPlayerEntity) {
            InspirationsNetwork.sendPacket(attacker, new SEntityVelocityPacket(attacker));
          }
        } else {
          attacker.push(-MathHelper.sin(target.yRot * 0.017453292F) * knockback * 0.5f, 0.1D, MathHelper.cos(target.yRot * 0.017453292F) * knockback * 0.5f);
        }
      }
    }
  }
}
