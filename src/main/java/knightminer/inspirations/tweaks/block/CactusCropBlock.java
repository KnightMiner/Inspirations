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
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.PlantType;

import javax.annotation.Nonnull;

public class CactusCropBlock extends BlockCropBlock {

	private static final VoxelShape[] BOUNDS = {
			VoxelShapes.create(0.4375, 0, 0.4375, 0.5625, 0.125, 0.5625),
			VoxelShapes.create(0.375, 0, 0.375, 0.625, 0.25, 0.625),
			VoxelShapes.create(0.3125, 0, 0.3125, 0.6875, 0.375, 0.6875),
			VoxelShapes.create(0.25, 0, 0.25, 0.75, 0.5, 0.75),
			VoxelShapes.create(0.1875, 0, 0.1875, 0.8125, 0.625, 0.8125),
			VoxelShapes.create(0.125, 0, 0.125, 0.875, 0.75, 0.875),
			VoxelShapes.create(0.0625, 0, 0.0625, 0.9375, 0.875, 0.9375)
	};
	public CactusCropBlock() {
		super(Blocks.CACTUS, PlantType.Desert, BOUNDS, Block.Properties
			.create(Material.CACTUS)
			.hardnessAndResistance(0.4F)
			.tickRandomly()
			.sound(SoundType.CLOTH)
		);
	}

	@Nonnull
	@Override
	public String getTranslationKey() {
		return "item.inspirations.cactus_seeds";
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
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return getShape(state, world, pos, context);
	}

	@Override
	public void onEntityCollision(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Entity entity) {
		entity.attackEntityFrom(DamageSource.CACTUS, 1.0F);
	}
}
