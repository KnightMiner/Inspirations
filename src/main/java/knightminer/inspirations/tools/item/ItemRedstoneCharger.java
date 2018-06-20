package knightminer.inspirations.tools.item;

import static knightminer.inspirations.tools.InspirationsTools.redstoneCharge;

import knightminer.inspirations.utility.block.BlockRedstoneCharge;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemRedstoneCharger extends Item {
	public ItemRedstoneCharger() {
		this.maxStackSize = 1;
		this.setMaxDamage(120);
		this.setCreativeTab(CreativeTabs.TOOLS);
	}

	/**
	 * Called when a Block is right-clicked with this Item
	 */
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		// we clicked a block, but want the position in front of the block
		if(!redstoneCharge.canPlaceBlockAt(world, pos)) {
			pos = pos.offset(facing);
		}

		ItemStack stack = player.getHeldItem(hand);

		// stop if we cannot edit
		if (!player.canPlayerEdit(pos, facing, stack)) {
			return EnumActionResult.FAIL;
		}

		// try placing a redstone charge
		if (redstoneCharge.canPlaceBlockAt(world, pos)) {
			world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
			IBlockState state = redstoneCharge.getDefaultState()
					.withProperty(BlockRedstoneCharge.FACING, facing.getOpposite())
					.withProperty(BlockRedstoneCharge.QUICK, player.isSneaking());
			world.setBlockState(pos, state, 11);
		}

		// mark we used the item
		if (player instanceof EntityPlayerMP) {
			CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
		}

		// damage it and return
		stack.damageItem(1, player);
		return EnumActionResult.SUCCESS;
	}
}
