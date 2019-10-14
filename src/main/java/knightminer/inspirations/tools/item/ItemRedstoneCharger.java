package knightminer.inspirations.tools.item;

import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tools.block.BlockRedstoneCharge;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;

import static knightminer.inspirations.tools.InspirationsTools.redstoneCharge;

public class ItemRedstoneCharger extends Item {
	public ItemRedstoneCharger() {
		super(new Item.Properties()
			.maxDamage(120)
			.group(ItemGroup.TOOLS)
		);
	}

	/**
	 * Called when a Block is right-clicked with this Item
	 */
	@Nonnull
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		BlockPos pos = context.getPos();
		Direction facing = context.getFace();
		World world = context.getWorld();
		
		// we clicked a block, but want the position in front of the block
		if(world.getBlockState(pos).isSolid()) {
			pos = pos.offset(facing);
		}

		ItemStack stack = context.getItem();

		// stop if we cannot edit
		if (context.getPlayer() == null || !context.getPlayer().canPlayerEdit(pos, facing, ItemStack.EMPTY)) {
			return ActionResultType.FAIL;
		}
		
		BlockState state = InspirationsTools.redstoneCharge.getDefaultState()
			.with(BlockRedstoneCharge.FACING, facing.getOpposite())
			.with(BlockRedstoneCharge.QUICK, context.isPlacerSneaking());
		
		DirectionalPlaceContext blockContext = new DirectionalPlaceContext(world, pos, facing, ItemStack.EMPTY, facing);
		

		// try placing a redstone charge

		if (world.getBlockState(pos).isReplaceable(blockContext)) {
			world.playSound(context.getPlayer(), pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
			world.setBlockState(pos, state, Constants.BlockFlags.DEFAULT_AND_RERENDER);
			redstoneCharge.onBlockPlacedBy(world, pos, state, null, ItemStack.EMPTY);
		}

		// mark we used the item
		if (context.getPlayer() instanceof ServerPlayerEntity) {
			CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)context.getPlayer(), pos, stack);
		}

		// damage it and return
		stack.damageItem(1, context.getPlayer(), player -> player.sendBreakAnimation(context.getHand()));
		return ActionResultType.SUCCESS;
	}
}
