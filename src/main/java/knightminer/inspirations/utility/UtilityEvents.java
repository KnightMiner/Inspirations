package knightminer.inspirations.utility;

import knightminer.inspirations.common.Config;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class UtilityEvents {
	@SubscribeEvent
	public static void placeCarpetOnPressurePlate(RightClickBlock event) {
		if(!Config.enableCarpetedPressurePlate.get()) {
			return;
		}

		// must be using carpet
		ItemStack stack = event.getItemStack();
		Block carpetBlock = Block.getBlockFromItem(stack.getItem());
		if(!(carpetBlock instanceof CarpetBlock)) {
			return;
		}

		// must be clicking a stone pressure plate or the block below one
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		BlockState current = world.getBlockState(pos);
		if(current.getBlock() != Blocks.STONE_PRESSURE_PLATE) {
			pos = pos.up();
			current = world.getBlockState(pos);
			if(current.getBlock() != Blocks.STONE_PRESSURE_PLATE) {
				return;
			}
		}

		// determine the state to place
		DyeColor color = ((CarpetBlock)carpetBlock).getColor();

		BlockState state = InspirationsUtility.carpetedPressurePlates[color.getId()].getDefaultState();
		state = state.updatePostPlacement(Direction.DOWN, world.getBlockState(pos.down()), world, pos, pos.down());

		// play sound
		PlayerEntity player = event.getEntityPlayer();
		SoundType sound = state.getBlock().getSoundType(state, world, pos, player);
		world.playSound(player, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

		// place the block
		if(!world.isRemote) {
			// and place it
			world.setBlockState(pos, state);

			// add statistic
			if (player instanceof ServerPlayerEntity) {
				CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)player, pos, stack);
			}

			// take one carpet
			if(!player.isCreative()) {
				stack.shrink(1);
			}
		}
		event.setCanceled(true);
		event.setCancellationResult(ActionResultType.SUCCESS);
	}

	/**
	 * Makes clicking a hopper with a pipe place the pipe instead of opening the hopper's GUI
	 */
	@SubscribeEvent
	public static void clickHopperWithPipe(RightClickBlock event) {
		if(!Config.enablePipe.get() || event.getItemStack().getItem() != InspirationsUtility.pipeItem) {
			return;
		}
		World world = event.getWorld();
		if(world.isRemote || !(world.getBlockState(event.getPos()).getBlock() instanceof HopperBlock)) {
			return;
		}

		event.setUseBlock(Event.Result.DENY);
		event.setUseItem(Event.Result.ALLOW);
	}
}
