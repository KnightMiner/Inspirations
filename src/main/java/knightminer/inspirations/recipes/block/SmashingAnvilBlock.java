package knightminer.inspirations.recipes.block;

import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class SmashingAnvilBlock extends AnvilBlock {

	public SmashingAnvilBlock(Block.Properties props) {
		super(props);
	}

	// Replace this to handle our different blocks.
   @Nullable
   public static BlockState damage(BlockState state) {
      Block block = state.getBlock();
      if (block == Blocks.ANVIL || block == InspirationsRecipes.fullAnvil) {
         return InspirationsRecipes.chippedAnvil.getDefaultState().with(FACING, state.get(FACING));
      } else {
		  if (block == Blocks.CHIPPED_ANVIL || block == InspirationsRecipes.chippedAnvil)
			  return InspirationsRecipes.damagedAnvil.getDefaultState().with(FACING, state.get(FACING));
		  else return null;
      }
   }

	@Override
	public void onEndFalling(World world, BlockPos pos, BlockState anvil, BlockState target, FallingBlockEntity entity) {
		BlockPos down = pos.down();
		if(!smashBlock(world, down, world.getBlockState(down))) {
			super.onEndFalling(world, pos, anvil, target, entity);
		}
	}

	/**
	 * Base logic to smash a block
	 * @param world  World instance
	 * @param pos    Position target
	 * @param state  State being smashed
	 * @return  True if somethign was smashed
	 */
	@SuppressWarnings("WeakerAccess")
	public static boolean smashBlock(World world, BlockPos pos, BlockState state) {
		// if we started on air, just return true
		if(state.getBlock() == Blocks.AIR) {
			return true;
		}
		// if the block is unbreakable, leave it
		if(state.getBlockHardness(world, pos) == -1) {
			return false;
		}

		BlockState transformation = InspirationsRegistry.getAnvilSmashResult(state);
		if(transformation == null) {
			return false;
		}

		// if the result is air, break the block
		if(transformation.getBlock() == Blocks.AIR) {
			world.destroyBlock(pos, true);
		} else {
			// breaking particles
			world.playEvent(Constants.WorldEvents.BREAK_BLOCK_EFFECTS, pos, Block.getStateId(state));
			world.setBlockState(pos, transformation);
		}
		return true;
	}
}
