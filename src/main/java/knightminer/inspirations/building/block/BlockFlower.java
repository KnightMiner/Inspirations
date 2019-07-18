package knightminer.inspirations.building.block;

import java.util.Random;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockFlower extends BushBlock implements IGrowable {
    private static final VoxelShape SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);
	private final DoublePlantBlock largePlant;

	public BlockFlower(DoublePlantBlock largePlant) {
		super(Block.Properties.create(Material.PLANTS).hardnessAndResistance(0F).sound(SoundType.PLANT));
		this.largePlant = largePlant;
	}

	/* Planty stuff */

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		Vec3d off = state.getOffset(world, pos);
		return SHAPE.withOffset(off.x, off.y, off.z);
	}

	@Override
	public OffsetType getOffsetType() {
		return OffsetType.XZ;
	}

	/* Doubling up */

	@Override
	public boolean canGrow(IBlockReader world, BlockPos pos, BlockState state, boolean isClient) {
		return largePlant != null;
	}


	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
		return true;
	}


	@Override
	public void grow(World world, Random rand, BlockPos pos, BlockState state) {
		// should not happen, but catch anyways
		if(largePlant == null) {
			return;
		}

		if (world.isAirBlock(pos.up())) {
			largePlant.placeAt(world, pos, 2);
		}
	}
}
