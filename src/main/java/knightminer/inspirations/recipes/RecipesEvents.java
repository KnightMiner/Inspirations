package knightminer.inspirations.recipes;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RecipesEvents {

	/**
	 * Event to handle cauldron clicking.
	 * Done though an event instead of the block so we can ensure it runs before other cauldron handlers, since we cancel for non-water
	 * @param event
	 */
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void clickCauldron(RightClickBlock event) {
		if(!Config.enableCauldronRecipes) {
			return;
		}

		// basic properties
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		IBlockState state = world.getBlockState(pos);
		if(state.getBlock() != Blocks.CAULDRON) {
			return;
		}
		ItemStack stack = event.getItemStack();
		if(stack.isEmpty()) {
			return;
		}

		boolean result = TileCauldron.interact(world, pos, state, event.getEntityPlayer(), event.getHand());
		if(result) {
			event.setCanceled(true);
			event.setCancellationResult(EnumActionResult.SUCCESS);
		}
	}
}
