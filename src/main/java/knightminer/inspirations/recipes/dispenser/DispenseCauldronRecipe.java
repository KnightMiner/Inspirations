package knightminer.inspirations.recipes.dispenser;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe.CauldronState;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import net.minecraft.block.*;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class DispenseCauldronRecipe extends DefaultDispenseItemBehavior {
	private static final DefaultDispenseItemBehavior DEFAULT = new DefaultDispenseItemBehavior();
	private IDispenseItemBehavior fallback;

	public DispenseCauldronRecipe(IDispenseItemBehavior fallback) {
		this.fallback = fallback;
	}

	@Nonnull
	@Override
   protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
		if(!stack.getItem().isIn(InspirationsRegistry.TAG_DISP_CAULDRON_RECIPES)) {
			return fallback.dispense(source, stack);
		}

		Direction side = source.getBlockState().get(DispenserBlock.FACING);
		BlockPos pos = source.getBlockPos().offset(side);
		World world = source.getWorld();
		BlockState state = world.getBlockState(pos);
		if(!InspirationsRegistry.isNormalCauldron(state)) {
			return fallback.dispense(source, stack);
		}

		// grab the TE if extended
		TileCauldron cauldron = null;
		CauldronState cauldronState = CauldronState.WATER;
		boolean boiling = false;
		Block block = state.getBlock();
		if(Config.enableExtendedCauldron() && block instanceof BlockEnhancedCauldron) {
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof TileCauldron) {
				cauldron = (TileCauldron) te;
				cauldronState = cauldron.getState();
				boiling = state.get(BlockEnhancedCauldron.BOILING);
			}
		} else {
			cauldronState = InspirationsRegistry.getCauldronState(state);
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

		// if its not extended, stop right here and disallow any recipes which do not return water
		if(!Config.enableExtendedCauldron() && !CauldronState.WATER.matches(newState)) {
			return DEFAULT.dispense(source, stack);
		}

		// play sound
		SoundEvent sound = recipe.getSound(stack, boiling, level, cauldronState);
		if(sound != null) {
			world.playSound(null, pos, sound, SoundCategory.BLOCKS, recipe.getVolume(sound), 1.0F);
		}

		// update level
		int newLevel = recipe.getLevel(level);
		if(newLevel != level || !cauldronState.matches(newState)) {
			// overrides for full cauldrons, assuming we started with a "valid cauldron", in this context an iron one
			if(newLevel == InspirationsRegistry.getCauldronMax() && InspirationsRegistry.isNormalCauldron(state) && InspirationsRegistry.hasFullCauldron(newState)) {
				world.setBlockState(pos, InspirationsRegistry.getFullCauldron(newState));
				cauldron = null;
			} else {
				if(!(block instanceof CauldronBlock)) {
					((CauldronBlock)Blocks.CAULDRON).setWaterLevel(world, pos, Blocks.CAULDRON.getDefaultState(), newLevel);

					// missing the tile entity
					if(Config.enableExtendedCauldron()) {
						TileEntity te = world.getTileEntity(pos);
						if(te instanceof TileCauldron) {
							cauldron = (TileCauldron)te;
						}
					}
				} else {
					((CauldronBlock)block).setWaterLevel(world, pos, state, newLevel);
				}
				if(newLevel == 0) {
					newState = CauldronState.WATER;
				}
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
		if(((DispenserTileEntity)source.getBlockTileEntity()).addItemStack(stack) < 0) {
			DEFAULT.dispense(source, stack);
		}
	}
}
