package knightminer.inspirations.building;

import knightminer.inspirations.building.block.RopeBlock;
import knightminer.inspirations.common.Config;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

public class BuildingEvents {

	@SubscribeEvent
	public static void toggleRopeLadder(PlayerInteractEvent.RightClickBlock event) {
		if (!Config.enableRopeLadder() || event.getWorld().isRemote()) {
			return;
		}

		World world = event.getWorld();
		BlockPos pos = event.getPos();
		BlockState state = world.getBlockState(pos);
		if (!(state.getBlock() instanceof RopeBlock)) {
			return;
		}

		PlayerEntity player = event.getEntityPlayer();
		if (state.get(RopeBlock.RUNGS) != RopeBlock.Rungs.NONE) {
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
		world.setBlockState(pos, state.with(RopeBlock.RUNGS, RopeBlock.Rungs.NONE));
		RopeBlock rope = (RopeBlock)state.getBlock();
		SoundType soundtype = rope.getSoundType(state, world, pos, player);
		world.playSound(player, pos, soundtype.getBreakSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
		ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(rope.getRungsItem(), RopeBlock.RUNG_ITEM_COUNT), player.inventory.currentItem);
		player.resetActiveHand();

		return true;
	}

	private static boolean makeRopeLadder(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, ItemStack stack) {
		// must have not clicked the bottom and we must have 4 items
		if (side.getAxis() == Direction.Axis.Y || (stack.getCount() < RopeBlock.RUNG_ITEM_COUNT && !player.isCreative())) {
			return false;
		}

		// ensure we hae the right item
		RopeBlock rope = (RopeBlock)state.getBlock();
		if (stack.getItem() != rope.getRungsItem()) {
			return false;
		}

		// add rungs
		world.setBlockState(pos, state.with(RopeBlock.RUNGS, RopeBlock.Rungs.fromAxis(side.rotateY().getAxis())));
		SoundType soundtype = state.getBlock().getSoundType(state, world, pos, player);
		world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
		if(!player.isCreative()) {
			stack.shrink(RopeBlock.RUNG_ITEM_COUNT);
		}

		return true;
	}
}
