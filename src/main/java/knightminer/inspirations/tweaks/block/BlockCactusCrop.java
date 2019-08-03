package knightminer.inspirations.tweaks.block;

import knightminer.inspirations.tweaks.InspirationsTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.PlantType;

import javax.annotation.Nonnull;

public class BlockCactusCrop extends BlockBlockCrop {

	private static final VoxelShape[] BOUNDS = {
			Block.makeCuboidShape(0.4375, 0, 0.4375, 0.5625, 0.125, 0.5625),
			Block.makeCuboidShape(0.375, 0, 0.375, 0.625, 0.25, 0.625),
			Block.makeCuboidShape(0.3125, 0, 0.3125, 0.6875, 0.375, 0.6875),
			Block.makeCuboidShape(0.25, 0, 0.25, 0.75, 0.5, 0.75),
			Block.makeCuboidShape(0.1875, 0, 0.1875, 0.8125, 0.625, 0.8125),
			Block.makeCuboidShape(0.125, 0, 0.125, 0.875, 0.75, 0.875),
			Block.makeCuboidShape(0.0625, 0, 0.0625, 0.9375, 0.875, 0.9375)
	};
	public BlockCactusCrop() {
		super(Blocks.CACTUS, PlantType.Desert, BOUNDS, Block.Properties
			.create(Material.CACTUS)
			.hardnessAndResistance(0.4F)
			.tickRandomly()
			.sound(SoundType.CLOTH)
		);
	}

	@Nonnull
	@Override
	protected IItemProvider getSeedsItem() {
		return InspirationsTweaks.cactusSeeds;
	}

	@Override
	public boolean isValidPosition(@Nonnull BlockState state, IWorldReader world, @Nonnull BlockPos pos) {
		return Blocks.CACTUS.isValidPosition(Blocks.CACTUS.getDefaultState(), world, pos);
	}


	/* spiky! */

	@Override
	public void onEntityCollision(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Entity entity) {
		entity.attackEntityFrom(DamageSource.CACTUS, 1.0F);
	}
}
