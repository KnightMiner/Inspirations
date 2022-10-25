package knightminer.inspirations.tools.item;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.item.HidableItem;
import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tools.block.RedstoneChargeBlock;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import static knightminer.inspirations.tools.InspirationsTools.redstoneCharge;

public class RedstoneChargerItem extends HidableItem {
  public RedstoneChargerItem() {
    super(new Item.Properties()
              .durability(120)
              .tab(ItemGroup.TAB_TOOLS), Config.enableRedstoneCharger);
  }

  @Override
  public ActionResultType useOn(ItemUseContext context) {
    BlockPos pos = context.getClickedPos();
    Direction facing = context.getClickedFace();
    World world = context.getLevel();

    // we clicked a block, but want the position in front of the block
    if (world.getBlockState(pos).canOcclude()) {
      pos = pos.relative(facing);
    }

    // stop if we cannot edit
    PlayerEntity player = context.getPlayer();
    if (player == null || !player.mayUseItemAt(pos, facing, ItemStack.EMPTY)) {
      return ActionResultType.FAIL;
    }

    BlockState state = InspirationsTools.redstoneCharge.defaultBlockState()
                                                       .setValue(RedstoneChargeBlock.FACING, facing.getOpposite())
                                                       .setValue(RedstoneChargeBlock.QUICK, player.isCrouching());

    DirectionalPlaceContext blockContext = new DirectionalPlaceContext(world, pos, facing, ItemStack.EMPTY, facing);


    // try placing a redstone charge

    if (world.getBlockState(pos).canBeReplaced(blockContext)) {
      world.playSound(context.getPlayer(), pos, SoundEvents.FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.4F + 0.8F);
      world.setBlock(pos, state, Constants.BlockFlags.DEFAULT_AND_RERENDER);
      redstoneCharge.setPlacedBy(world, pos, state, null, ItemStack.EMPTY);
    }

    // mark we used the item
    ItemStack stack = context.getItemInHand();
    if (context.getPlayer() instanceof ServerPlayerEntity) {
      CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)context.getPlayer(), pos, stack);
    }

    // damage it and return
    stack.hurtAndBreak(1, context.getPlayer(), cPlayer -> cPlayer.broadcastBreakEvent(context.getHand()));
    return ActionResultType.SUCCESS;
  }

  @Override
  public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
    return repair.getItem() == Items.REDSTONE;
  }
}
