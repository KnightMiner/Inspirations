package knightminer.inspirations.building;

import knightminer.inspirations.building.block.BlockFlower.FlowerType;
import knightminer.inspirations.building.block.BlockRope;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.Util;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;

public class BuildingEvents {
	@SubscribeEvent
	public static void addFlowerDrop(HarvestDropsEvent event) {
		if(!Config.enableFlowers || event.isCanceled() || event.getWorld().isRemote) {
			return;
		}

		// check that its the right block
		IBlockState state = event.getState();
		if(state.getBlock() != Blocks.DOUBLE_PLANT) {
			return;
		}

		// check conditions: must be shearing and not creative
		EntityPlayer player = event.getHarvester();
		if(player == null || player.capabilities.isCreativeMode) {
			return;
		}

		ItemStack shears = player.getHeldItemMainhand();
		Item item = shears.getItem();
		if(!(item instanceof ItemShears || item.getToolClasses(shears).contains("shears"))) {
			return;
		}

		// finally, time to do the drop
		FlowerType type = FlowerType.fromDouble(state.getActualState(event.getWorld(), event.getPos()).getValue(BlockDoublePlant.VARIANT));

		// well, maybe not
		if(type == null) {
			return;
		}

		// replace the first match with the new item
		List<ItemStack> drops = event.getDrops();
		Item doublePlant = Item.getItemFromBlock(Blocks.DOUBLE_PLANT);
		for(ItemStack drop : drops) {
			if(drop.getItem() == doublePlant) {
				drops.remove(drop);
				drops.add(new ItemStack(InspirationsBuilding.flower, 2, type.getMeta()));
				break;
			}
		}
	}

	@SubscribeEvent
	public static void toggleRopeLadder(PlayerInteractEvent.RightClickBlock event) {
		if (!Config.enableRopeLadder) {
			return;
		}

		World world = event.getWorld();
		BlockPos pos = event.getPos();
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() != InspirationsBuilding.rope) {
			return;
		}

		EntityPlayer player = event.getEntityPlayer();
		if (state.getValue(BlockRope.RUNGS) != BlockRope.Rungs.NONE) {
			if (removeRopeLadder(world, pos, state, player)) {
				event.setCanceled(true);
				event.setCancellationResult(EnumActionResult.SUCCESS);
			}
			return;
		}

		if (makeRopeLadder(world, pos, state, event.getFace(), player, event.getItemStack())) {
			event.setCanceled(true);
			event.setCancellationResult(EnumActionResult.SUCCESS);
		}
	}

	private static boolean removeRopeLadder(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		// only remove rungs when sneaking
		if (!player.isSneaking()) {
			return false;
		}

		// remove rungs
		world.setBlockState(pos, state.withProperty(BlockRope.RUNGS, BlockRope.Rungs.NONE));
		SoundType soundtype = state.getBlock().getSoundType(state, world, pos, player);
		world.playSound(player, pos, soundtype.getBreakSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
		ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(state.getValue(BlockRope.TYPE).getItem(), 4), player.inventory.currentItem);

		return true;
	}

	private static boolean makeRopeLadder(World world, BlockPos pos, IBlockState state, EnumFacing side, EntityPlayer player, ItemStack stack) {
		// must have not clicked the bottom and we must have 4 items
		if (side.getAxis() == EnumFacing.Axis.Y || (stack.getCount() < 4 && !player.isCreative())) {
			return false;
		}

		// ensure we hae the right item
		BlockRope.RopeType type = state.getValue(BlockRope.TYPE);
		if (type == BlockRope.RopeType.CHAIN) {
			if (!Util.oreMatches(stack, "nuggetIron"))
				return false;
		} else {
			if (stack.getItem() != Items.STICK) {
				return false;
			}
		}

		// add rungs
		world.setBlockState(pos, state.withProperty(BlockRope.RUNGS, BlockRope.Rungs.fromAxis(side.rotateY().getAxis())));
		SoundType soundtype = state.getBlock().getSoundType(state, world, pos, player);
		world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
		if(!player.capabilities.isCreativeMode) {
			stack.shrink(4);
		}

		return true;
	}
}
