package knightminer.inspirations.recipes.block;

import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Objects;

public class SmashingAnvilBlock extends AnvilBlock {

	public SmashingAnvilBlock(Block original) {
		super(Block.Properties.from(original));
		setRegistryName(Objects.requireNonNull(original.getRegistryName()));
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
	public void onEndFalling(World world, BlockPos pos, BlockState anvil, BlockState state) {
		BlockPos down = pos.down();
		if(!smashBlock(world, down, world.getBlockState(down))) {
			super.onEndFalling(world, pos, anvil, state);
		}
	}

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
			world.playEvent(2001, pos, Block.getStateId(state));
			world.setBlockState(pos, transformation);
		}
		return true;
	}
}
