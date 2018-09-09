package knightminer.inspirations.recipes.dispenser;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe.CauldronState;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DispenseCauldronRecipe extends BehaviorDefaultDispenseItem {
	private static final BehaviorDefaultDispenseItem DEFAULT = new BehaviorDefaultDispenseItem();
	private IBehaviorDispenseItem fallback;
	private int[] validMeta;
	public DispenseCauldronRecipe(IBehaviorDispenseItem fallback, int... validMeta) {
		this.fallback = fallback;
		this.validMeta = validMeta;
	}

	private boolean isMetaValid(ItemStack stack) {
		if(validMeta.length == 0) {
			return true;
		}
		int meta = stack.getMetadata();
		for(int i : validMeta) {
			if(i == meta) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
		if(!isMetaValid(stack)) {
			return fallback.dispense(source, stack);
		}

		EnumFacing side = source.getBlockState().getValue(BlockDispenser.FACING);
		BlockPos pos = source.getBlockPos().offset(side);
		World world = source.getWorld();
		IBlockState state = world.getBlockState(pos);
		if(!(state.getBlock() instanceof BlockCauldron)) {
			return fallback.dispense(source, stack);
		}

		// grab the TE if extended
		TileCauldron cauldron = null;
		CauldronState cauldronState = CauldronState.WATER;
		boolean boiling = false;
		if(Config.enableExtendedCauldron) {
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof TileCauldron) {
				cauldron = (TileCauldron) te;
				cauldronState = cauldron.getState();
				boiling = state.getValue(BlockEnhancedCauldron.BOILING);
			}
		} else {
			boiling = InspirationsRegistry.isCauldronFire(world.getBlockState(pos.down()));
		}

		// other properties
		int level = BlockEnhancedCauldron.getCauldronLevel(state);

		// grab recipe
		ICauldronRecipe recipe = InspirationsRegistry.getCauldronResult(stack, boiling, level, cauldronState);
		if(recipe == null) {
			return DEFAULT.dispense(source, stack);
		}
		// grab state first since we may need to back out
		CauldronState newState = recipe.getState(stack, boiling, level, cauldronState);

		// if its not a TE, stop right here and disallow any recipes which do not return water
		if(cauldron == null && !CauldronState.WATER.matches(newState)) {
			return DEFAULT.dispense(source, stack);
		}

		// play sound
		SoundEvent sound = recipe.getSound(stack, boiling, level, cauldronState);
		if(sound != null) {
			world.playSound((EntityPlayer)null, pos, sound, SoundCategory.BLOCKS, recipe.getVolume(sound), 1.0F);
		}

		// update level
		int newLevel = recipe.getLevel(level);
		if(newLevel != level) {
			((BlockCauldron)state.getBlock()).setWaterLevel(world, pos, state, newLevel);
			if(newLevel == 0) {
				newState = CauldronState.WATER;
			}
		}

		// update the state
		if(cauldron != null) {
			cauldron.setState(newState, true);
		}

		// result
		ItemStack result = recipe.getResult(stack, boiling, level, cauldronState);
		ItemStack container = recipe.getContainer(stack);
		int oldSize = stack.getCount();
		ItemStack remainder = recipe.transformInput(stack.copy(), boiling, level, cauldronState);

		// if there is no remainder, return will be different
		if(remainder.isEmpty()) {
			// no container means return is result
			if(container.isEmpty()) {
				return result;
			}

			// otherwise update the container and its our return
			container.setCount(container.getCount() * oldSize);
			dispenseItem(source, result);
			return container;
		}

		// we at least have a remainder, so dispense the item and container
		dispenseItem(source, result);
		if(!container.isEmpty()) {
			container.setCount(container.getCount() * (oldSize - remainder.getCount()));
			dispenseItem(source, container);
		}

		return remainder;
	}

	private static void dispenseItem(IBlockSource source, ItemStack stack) {
		if(((TileEntityDispenser)source.getBlockTileEntity()).addItemStack(stack) < 0) {
			DEFAULT.dispense(source, stack);
		}
	}
}
