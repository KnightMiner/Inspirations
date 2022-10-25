package knightminer.inspirations.tools;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.network.InspirationsNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.LockCode;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ThornsEnchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;
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
    if (!Config.enableLock.getAsBoolean()) {
      return;
    }

    // first, ensure we have a valid item to use
    Player player = event.getPlayer();
    ItemStack stack = player.getItemInHand(event.getHand());

    boolean isKey = stack.getItem() == InspirationsTools.key;
    boolean isLock = stack.getItem() == InspirationsTools.lock;

    if (!isKey && !isLock) {
      return;
    }
    BlockEntity te = event.getWorld().getBlockEntity(event.getPos());

    if (te instanceof BaseContainerBlockEntity lockable) {

      LockCode heldCode = new LockCode(stack.getHoverName().getContents());

      // lock code
      if (isLock) {
        // already locked: display message
        if (lockable.lockKey != LockCode.NO_LOCK) {
          player.displayClientMessage(new TranslatableComponent(Inspirations.prefix("lock.fail.locked")), true);
        } else if (!stack.hasCustomHoverName()) {
          player.displayClientMessage(new TranslatableComponent(Inspirations.prefix("lock.fail.blank")), true);
        } else {
          // lock the container
          lockable.lockKey = heldCode;
          lockable.setChanged();
          if (!player.isCreative()) {
            stack.shrink(1);
          }
          player.displayClientMessage(new TranslatableComponent(Inspirations.prefix("lock.success")), true);
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        // if the player is not sneaking, just open the chest as normal with the key
      } else if (player.isCrouching()) {
        if (lockable.lockKey != LockCode.NO_LOCK) {
          // if the key matches the lock, take off the lock and give it to the player
          if (lockable.lockKey.unlocksWith(stack)) {
            LockCode code = lockable.lockKey;
            lockable.lockKey = LockCode.NO_LOCK;
            lockable.setChanged();
            ItemHandlerHelper.giveItemToPlayer(player,
                                               new ItemStack(InspirationsTools.lock).setHoverName(new TextComponent(code.key))
                                              );
            player.displayClientMessage(new TranslatableComponent(Inspirations.prefix("unlock.success")), true);
          } else {
            player.displayClientMessage(new TranslatableComponent(Inspirations.prefix("unlock.fail.no_match")), true);
          }
        } else {
          player.displayClientMessage(new TranslatableComponent(Inspirations.prefix("unlock.fail.unlocked")), true);
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public static void vineBreakEvent(BreakEvent event) {
    if (!Config.harvestHangingVines.getAsBoolean()) {
      return;
    }

    // stop if on client or already canceled
    if (event.isCanceled()) {
      return;
    }
    if (event.getWorld().isClientSide() || !(event.getWorld() instanceof ServerLevel world)) {
      return;
    }

    // check conditions: must be shearing vines and not creative
    Player player = event.getPlayer();
    if (player.isCreative()) {
      return;
    }
    Block block = event.getState().getBlock();
    if (!(block instanceof VineBlock vine)) {
      return;
    }
    ItemStack shears = player.getMainHandItem();
    if (shears.canPerformAction(ToolActions.SHEARS_DIG)) {
      return;
    }

    BlockPos pos = event.getPos().below();
    BlockState state = world.getBlockState(pos);

    // iterate down until we find either a non-vine or the vine can stay
    int count = 0;
    while (state.getBlock() == block && vine.isShearable(shears, world, pos) && !vineCanStay(world, state, pos)) {
      count++;
      for (ItemStack stack : state.getDrops(new LootContext.Builder(world)
                                                .withParameter(LootContextParams.TOOL, shears)
                                                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                                                .withParameter(LootContextParams.THIS_ENTITY, player)
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

  private static boolean vineCanStay(Level world, BlockState state, BlockPos pos) {
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
          ((LivingEntity)attacker).knockback(knockback * 0.5F, Mth.sin(target.getYRot() * 0.017453292F), -Mth.cos(target.getYRot() * 0.017453292F));
          if (attacker instanceof ServerPlayer) {
            InspirationsNetwork.sendPacket(attacker, new ClientboundSetEntityMotionPacket(attacker));
          }
        } else {
          attacker.push(-Mth.sin(target.getYRot() * 0.017453292F) * knockback * 0.5f, 0.1D, Mth.cos(target.getYRot() * 0.017453292F) * knockback * 0.5f);
        }
      }
    }
  }
}
