package knightminer.inspirations.recipes;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe.CauldronState;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockFire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

public class RecipesEvents {

	@SubscribeEvent
	public static void clickCauldron(RightClickBlock event) {
		// full cauldron extension requires block substitution
		if(!Config.simpleCauldronRecipes) {
			return;
		}

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
		if(stack.isEmpty()) {
			return;
		}

		boolean isBoiling = world.getBlockState(pos.down()).getBlock() instanceof BlockFire;
		ICauldronRecipe recipe = InspirationsRegistry.getCauldronResult(stack, isBoiling, level, CauldronState.WATER);

		// ensure both we have a recipe and the recipe is valid for a non-te cauldron
		if(recipe != null) {
			if(!world.isRemote && recipe.getState(stack, isBoiling, level, CauldronState.WATER).matches(CauldronState.WATER)) {
				// sound
				SoundEvent sound = recipe.getSound(stack, isBoiling, level, CauldronState.WATER);
				if(sound != null) {
					world.playSound((EntityPlayer)null, pos, sound, SoundCategory.BLOCKS, recipe.getVolume(sound), 1.0F);
				}

				// update block
				int newLevel = MathHelper.clamp(recipe.getLevel(level), 0, 3);
				if(newLevel != level) {
					Blocks.CAULDRON.setWaterLevel(world, pos, state, newLevel);
				}

				// result
				ItemStack result = recipe.getResult(stack, isBoiling, level, CauldronState.WATER);
				EntityPlayer player = event.getEntityPlayer();
				if(!player.capabilities.isCreativeMode) {
					player.setHeldItem(event.getHand(), recipe.transformInput(stack, isBoiling, level, CauldronState.WATER));
				}
				if(!result.isEmpty()) {
					ItemHandlerHelper.giveItemToPlayer(player, result, player.inventory.currentItem);
				}
			}

			event.setCanceled(true);
			event.setCancellationResult(EnumActionResult.SUCCESS);
		}
	}
}
