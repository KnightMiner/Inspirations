package knightminer.inspirations.building;

import java.util.List;

import knightminer.inspirations.building.block.BlockFlower;
import knightminer.inspirations.building.block.BlockRope;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

public class BuildingEvents {
	@SubscribeEvent
	public static void addFlowerDrop(HarvestDropsEvent event) {
		if(!Config.enableFlowers.get() || event.isCanceled() || event.getWorld().isRemote()) {
			return;
		}

		// check that its the right block
		BlockFlower flower = BlockFlower.getFlowerFromBlock(event.getState().getBlock());
		if(flower == null) {
			return;
		}

		// check conditions: must be shearing and not creative
		PlayerEntity player = event.getHarvester();
		if(player == null || player.isCreative()) {
			return;
		}

		ItemStack shears = player.getHeldItemMainhand();
		Item item = shears.getItem();
		if(!(item instanceof ShearsItem || item.getToolTypes(shears).contains(InspirationsRegistry.SHEAR_TYPE))) {
			return;
		}

		// finally, time to do the drop
		// replace the first match with the new item
		List<ItemStack> drops = event.getDrops();
		Item doublePlant = event.getState().getBlock().asItem();
		for(ItemStack drop : drops) {
			if(drop.getItem() == doublePlant) {
				drops.remove(drop);
				drops.add(new ItemStack(flower, 2));
				break;
			}
		}
	}

	@SubscribeEvent
	public static void toggleRopeLadder(PlayerInteractEvent.RightClickBlock event) {
		if (!Config.enableRopeLadder() || event.getWorld().isRemote()) {
			return;
		}

		World world = event.getWorld();
		BlockPos pos = event.getPos();
		BlockState state = world.getBlockState(pos);
		if (!(state.getBlock() instanceof BlockRope)) {
			return;
		}

		PlayerEntity player = event.getEntityPlayer();
		if (state.get(BlockRope.RUNGS) != BlockRope.Rungs.NONE) {
			if (removeRopeLadder(world, pos, state, player)) {
				event.setCanceled(true);
				event.setCancellationResult(ActionResultType.SUCCESS);
			}
			return;
		}

		if (makeRopeLadder(world, pos, state, event.getFace(), player, event.getItemStack())) {
			event.setCanceled(true);
			event.setCancellationResult(ActionResultType.SUCCESS);
		}
	}

	private static boolean removeRopeLadder(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		// only remove rungs when sneaking
		if (!player.isSneaking()) {
			return false;
		}

		// remove rungs
		world.setBlockState(pos, state.with(BlockRope.RUNGS, BlockRope.Rungs.NONE));
		BlockRope rope = (BlockRope)state.getBlock();
		SoundType soundtype = rope.getSoundType(state, world, pos, player);
		world.playSound(player, pos, soundtype.getBreakSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
		ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(rope.getRungsItem(), BlockRope.RUNG_ITEM_COUNT), player.inventory.currentItem);
		player.resetActiveHand();

		return true;
	}

	private static boolean makeRopeLadder(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, ItemStack stack) {
		// must have not clicked the bottom and we must have 4 items
		if (side.getAxis() == Direction.Axis.Y || (stack.getCount() < BlockRope.RUNG_ITEM_COUNT && !player.isCreative())) {
			return false;
		}

		// ensure we hae the right item
		BlockRope rope = (BlockRope)state.getBlock();
		if (stack.getItem() != rope.getRungsItem()) {
			return false;
		}

		// add rungs
		world.setBlockState(pos, state.with(BlockRope.RUNGS, BlockRope.Rungs.fromAxis(side.rotateY().getAxis())));
		SoundType soundtype = state.getBlock().getSoundType(state, world, pos, player);
		world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
		if(!player.isCreative()) {
			stack.shrink(BlockRope.RUNG_ITEM_COUNT);
		}

		return true;
	}
}
