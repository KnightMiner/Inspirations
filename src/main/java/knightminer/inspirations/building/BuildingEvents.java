package knightminer.inspirations.building;

import java.util.List;

import knightminer.inspirations.building.block.BlockFlower;
import knightminer.inspirations.common.Config;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BuildingEvents {
	private static ToolType shearsType = ToolType.get("shears");
	@SubscribeEvent
	public static void addFlowerDrop(HarvestDropsEvent event) {
		if(!Config.enableFlowers.get() || event.isCanceled() || event.getWorld().isRemote()) {
			return;
		}

		// check that its the right block
		BlockState state = event.getState();
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
		if(!(item instanceof ShearsItem || item.getToolTypes(shears).contains(shearsType))) {
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
}
