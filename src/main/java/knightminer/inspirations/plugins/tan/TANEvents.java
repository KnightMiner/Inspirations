package knightminer.inspirations.plugins.tan;

import knightminer.inspirations.recipes.tileentity.TileCauldron;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TANEvents {


	/**
	 * Event to run glass bottle interactions before TAN does to prevent it from overriding bottles it shouldn't
	 */
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void clickCauldron(RightClickBlock event) {
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		IBlockState state = world.getBlockState(pos);
		if(state.getBlock() != Blocks.CAULDRON) {
			return;
		}
		int level = state.getValue(BlockCauldron.LEVEL);
		if(level == 0) {
			return;
		}

		ItemStack stack = event.getItemStack();
		if(stack.getItem() != Items.GLASS_BOTTLE) {
			return;
		}

		TileEntity te = world.getTileEntity(pos);
		if(!(te instanceof TileCauldron)) {
			return;
		}

		((TileCauldron) te).interact(state, event.getEntityPlayer(), event.getHand());
		event.setCanceled(true);
	}
}
