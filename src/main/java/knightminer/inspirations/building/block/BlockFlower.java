package knightminer.inspirations.building.block;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import knightminer.inspirations.common.Config;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockFlower extends BushBlock implements IGrowable {
    private static final VoxelShape SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);
	private final DoublePlantBlock largePlant;

	private static final Map<Block, BlockFlower> largeToFlower = new HashMap<>();

	public BlockFlower(DoublePlantBlock largePlant) {
		super(Block.Properties.create(Material.PLANTS).hardnessAndResistance(0F).sound(SoundType.PLANT));
		this.largePlant = largePlant;
		largeToFlower.put(largePlant, this);
	}

    @Override
    public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        if(group == ItemGroup.SEARCH || Config.enableFlowers.get()) {
			super.fillItemGroup(group, items);
        }
    }

	/**
	 * Get the BlockFlower that matches the original double-high block type, or null.
	 * @param original The Vanilla block which has a flower version.
	 * @return The matching flower version, or null;
	 */
	@Nullable
	public static BlockFlower getFlowerFromBlock(Block original) {
		return largeToFlower.get(original);
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
