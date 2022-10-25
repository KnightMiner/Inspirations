package knightminer.inspirations.tools.item;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.item.HidableItem;
import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tools.block.RedstoneChargeBlock;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import static knightminer.inspirations.tools.InspirationsTools.redstoneCharge;

public class RedstoneChargerItem extends HidableItem {
  public RedstoneChargerItem() {
    super(new Item.Properties()
              .durability(120)
              .tab(CreativeModeTab.TAB_TOOLS), Config.enableRedstoneCharger);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    BlockPos pos = context.getClickedPos();
    Direction facing = context.getClickedFace();
    Level world = context.getLevel();

    // we clicked a block, but want the position in front of the block
    if (world.getBlockState(pos).canOcclude()) {
      pos = pos.relative(facing);
    }

    // stop if we cannot edit
    Player player = context.getPlayer();
    if (player == null || !player.mayUseItemAt(pos, facing, ItemStack.EMPTY)) {
      return InteractionResult.FAIL;
    }

    BlockState state = InspirationsTools.redstoneCharge.defaultBlockState()
                                                       .setValue(RedstoneChargeBlock.FACING, facing.getOpposite())
                                                       .setValue(RedstoneChargeBlock.QUICK, player.isCrouching());

    DirectionalPlaceContext blockContext = new DirectionalPlaceContext(world, pos, facing, ItemStack.EMPTY, facing);


    // try placing a redstone charge

    if (world.getBlockState(pos).canBeReplaced(blockContext)) {
      world.playSound(context.getPlayer(), pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, world.random.nextFloat() * 0.4F + 0.8F);
      world.setBlock(pos, state, Block.UPDATE_ALL_IMMEDIATE);
      redstoneCharge.setPlacedBy(world, pos, state, null, ItemStack.EMPTY);
    }

    // mark we used the item
    ItemStack stack = context.getItemInHand();
    if (context.getPlayer() instanceof ServerPlayer) {
      CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)context.getPlayer(), pos, stack);
    }

    // damage it and return
    stack.hurtAndBreak(1, context.getPlayer(), cPlayer -> cPlayer.broadcastBreakEvent(context.getHand()));
    return InteractionResult.SUCCESS;
  }

  @Override
  public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
    return repair.getItem() == Items.REDSTONE;
  }
}
