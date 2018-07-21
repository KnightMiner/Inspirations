package knightminer.inspirations.tweaks.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

public class ItemSeed extends ItemSeeds {

	private EnumPlantType type;
	private BlockCrops crops;
	public ItemSeed(BlockCrops crops, EnumPlantType type) {
		super(crops, Blocks.FARMLAND);
		this.type = type;
		this.crops = crops;
	}

	@Override
	public EnumPlantType getPlantType(net.minecraft.world.IBlockAccess world, BlockPos pos) {
		return type;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		IBlockState state = world.getBlockState(pos);
		if(facing == EnumFacing.UP && player.canPlayerEdit(pos.offset(facing), facing, stack) && crops.canBlockStay(world, pos.up(), state) && world.isAirBlock(pos.up())) {
			world.setBlockState(pos.up(), this.crops.getDefaultState());
			if (player instanceof EntityPlayerMP) {
				CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos.up(), stack);
			}

			stack.shrink(1);
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}
}
