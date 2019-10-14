package knightminer.inspirations.tweaks.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import javax.annotation.Nonnull;

public class ItemSeed extends BlockItem implements IPlantable {

	private PlantType type;
	private CropsBlock crops;
	public ItemSeed(CropsBlock crops, PlantType type) {
		super(crops, new Item.Properties().group(ItemGroup.FOOD));
		this.type = type;
		this.crops = crops;
	}

	@Override
	public PlantType getPlantType(IBlockReader world, BlockPos pos) {
		return type;
	}

	@Override
	public BlockState getPlant(IBlockReader world, BlockPos pos) {
		return crops.getPlant(world, pos);
	}

	@Nonnull
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		BlockPos pos = context.getPos();
		Direction facing = context.getFace();
		PlayerEntity player = context.getPlayer();
		ItemStack stack = context.getItem();
		BlockState state = context.getWorld().getBlockState(pos);

		if(facing == Direction.UP && player != null &&
			player.canPlayerEdit(pos.offset(facing), facing, stack) &&
			crops.isValidPosition(state, context.getWorld(), pos.up()) &&
			context.getWorld().isAirBlock(pos.up())
		) {
			context.getWorld().setBlockState(pos.up(), this.crops.getDefaultState());
			if (player instanceof ServerPlayerEntity) {
				CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)player, pos.up(), stack);
			}

			stack.shrink(1);
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.FAIL;
	}
}
