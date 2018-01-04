package knightminer.inspirations.building;

import java.util.List;

import knightminer.inspirations.building.block.BlockFlower.FlowerType;
import knightminer.inspirations.common.Config;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
}
