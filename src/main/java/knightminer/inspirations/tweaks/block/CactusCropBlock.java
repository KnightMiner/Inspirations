package knightminer.inspirations.tweaks.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.tweaks.InspirationsTweaks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.PlantType;

import javax.annotation.Nonnull;

public class CactusCropBlock extends BlockCropBlock {

	private static final VoxelShape[] BOUNDS = {
			makeCuboidShape(1, 0, 1, 15,  2, 15),
			makeCuboidShape(1, 0, 1, 15,  4, 15),
			makeCuboidShape(1, 0, 1, 15,  6, 15),
			makeCuboidShape(1, 0, 1, 15,  8, 15),
			makeCuboidShape(1, 0, 1, 15, 10, 15),
			makeCuboidShape(1, 0, 1, 15, 12, 15),
			makeCuboidShape(1, 0, 1, 15, 14, 15)
	};
	public CactusCropBlock() {
		super(Blocks.CACTUS, PlantType.Desert);
	}

	@Override
	protected IItemProvider getSeedsItem() {
		return InspirationsTweaks.cactusSeeds;
	}

	@Nonnull
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return BOUNDS[this.getAge(state)];
	}

	/* spiky! */
	@Override
	public void onEntityCollision(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Entity entity) {
		entity.attackEntityFrom(DamageSource.CACTUS, 1.0F);
	}

	@Deprecated
	@Override
	public boolean isValidPosition(@Nonnull BlockState state, IWorldReader world, @Nonnull BlockPos pos) {
		// if true, vanilla cactus farms will now produce cactus seeds rather than full blocks
		if (Config.nerfCactusFarms.get()) {
			return super.isValidPosition(state, world, pos);
		}

		// if not above cactus, also use base block logic
		// prevents planting seeds in spots where they will break on growth
		BlockPos down = pos.down();
		BlockState soil = world.getBlockState(down);
		if (soil.getBlock() != Blocks.CACTUS) {
			return super.isValidPosition(state, world, pos);
		}

		// otherwise, do cactus logic, but without the horizontal checks
		return soil.canSustainPlant(world, down, Direction.UP, getPlant()) && !world.getBlockState(pos.up()).getMaterial().isLiquid();
	}
}
