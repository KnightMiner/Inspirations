package knightminer.inspirations.utility;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.utility.block.BlockCarpetedPressurePlate;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class UtilityEvents {
	@SubscribeEvent
	public static void placeCarpetOnPressurePlate(RightClickBlock event) {
		if(!Config.enableCarpetedPressurePlate) {
			return;
		}

		// must be using carpet
		ItemStack stack = event.getItemStack();
		if(stack.getItem() != Item.getItemFromBlock(Blocks.CARPET)) {
			return;
		}

		// must be clicking a stone pressure plate or the block below one
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		IBlockState current = world.getBlockState(pos);
		if(current.getBlock() != Blocks.STONE_PRESSURE_PLATE) {
			pos = pos.up();
			current = world.getBlockState(pos);
			if(current.getBlock() != Blocks.STONE_PRESSURE_PLATE) {
				return;
			}
		}

		// determine the state to place
		IBlockState state;
		int meta = stack.getMetadata();
		EnumDyeColor color = EnumDyeColor.byMetadata(meta);
		if(meta < 8) {
			state = InspirationsUtility.carpetedPressurePlate1.getDefaultState()
					.withProperty(BlockCarpetedPressurePlate.COLOR1, color);
		} else {
			state = InspirationsUtility.carpetedPressurePlate2.getDefaultState()
					.withProperty(BlockCarpetedPressurePlate.COLOR2, color);
		}

		// play sound
		EntityPlayer player = event.getEntityPlayer();
		SoundType sound = state.getBlock().getSoundType(state, world, pos, player);
		world.playSound(player, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

		// place the block
		if(!world.isRemote) {
			// and place it
			world.setBlockState(pos, state);

			// add statistic
			if (player instanceof EntityPlayerMP) {
				CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
			}

			// take one carpet
			if(!player.capabilities.isCreativeMode) {
				stack.shrink(1);
			}
		}
		event.setCanceled(true);
		event.setCancellationResult(EnumActionResult.SUCCESS);
	}

	/**
	 * Makes clicking a hopper with a pipe place the pipe instead of opening the hopper's GUI
	 */
	@SubscribeEvent
	public static void clickHopperWithPipe(RightClickBlock event) {
		if(!Config.enablePipe || event.getItemStack().getItem() != InspirationsUtility.pipeItem) {
			return;
		}
		World world = event.getWorld();
		if(world.isRemote || !(world.getBlockState(event.getPos()).getBlock() instanceof BlockHopper)) {
			return;
		}

		event.setUseBlock(Result.DENY);
		event.setUseItem(Result.ALLOW);
	}
}
