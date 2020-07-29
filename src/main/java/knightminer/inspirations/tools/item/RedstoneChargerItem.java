package knightminer.inspirations.tools.item;

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

public class RedstoneChargerItem extends Item {
	public RedstoneChargerItem() {
		super(new Item.Properties()
			.maxDamage(120)
			.group(ItemGroup.TOOLS)
		);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		BlockPos pos = context.getPos();
		Direction facing = context.getFace();
		World world = context.getWorld();
		
		// we clicked a block, but want the position in front of the block
		if(world.getBlockState(pos).isSolid()) {
			pos = pos.offset(facing);
		}

		// stop if we cannot edit
		PlayerEntity player = context.getPlayer();
		if (player == null || !player.canPlayerEdit(pos, facing, ItemStack.EMPTY)) {
			return ActionResultType.FAIL;
		}
		
		BlockState state = InspirationsTools.redstoneCharge.getDefaultState()
			.with(RedstoneChargeBlock.FACING, facing.getOpposite())
			.with(RedstoneChargeBlock.QUICK, player.isCrouching());
		
		DirectionalPlaceContext blockContext = new DirectionalPlaceContext(world, pos, facing, ItemStack.EMPTY, facing);
		

		// try placing a redstone charge

		if (world.getBlockState(pos).isReplaceable(blockContext)) {
			world.playSound(context.getPlayer(), pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
			world.setBlockState(pos, state, Constants.BlockFlags.DEFAULT_AND_RERENDER);
			redstoneCharge.onBlockPlacedBy(world, pos, state, null, ItemStack.EMPTY);
		}

		// mark we used the item
		ItemStack stack = context.getItem();
		if (context.getPlayer() instanceof ServerPlayerEntity) {
			CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)context.getPlayer(), pos, stack);
		}

		// damage it and return
		stack.damageItem(1, context.getPlayer(), cPlayer -> cPlayer.sendBreakAnimation(context.getHand()));
		return ActionResultType.SUCCESS;
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return repair.getItem() == Items.REDSTONE;
	}
}
