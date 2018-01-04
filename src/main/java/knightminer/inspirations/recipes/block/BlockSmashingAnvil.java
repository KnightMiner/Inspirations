package knightminer.inspirations.recipes.block;

import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSmashingAnvil extends BlockAnvil {

	public BlockSmashingAnvil() {
		this.setHardness(5.0F);
		this.setSoundType(SoundType.ANVIL);
		this.setResistance(2000.0F);
		this.setUnlocalizedName("anvil");
	}

	@Override
	public void onEndFalling(World world, BlockPos pos, IBlockState anvil, IBlockState state) {
		BlockPos down = pos.down();
		if(!smashBlock(world, down, world.getBlockState(down))) {
			super.onEndFalling(world, pos, anvil, state);
		}
	}

	public static boolean smashBlock(World world, BlockPos pos, IBlockState state) {
		// if we started on air, just return true
		if(state.getBlock() == Blocks.AIR) {
			return true;
		}
		// if the block is unbreakable, leave it
		if(state.getBlockHardness(world, pos) == -1) {
			return false;
		}

		IBlockState transformation = InspirationsRegistry.getAnvilSmashResult(state);
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
