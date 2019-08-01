package knightminer.inspirations.utility.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;

public class BlockCarpetedPressurePlate extends PressurePlateBlock {
	protected static final VoxelShape PRESSED_AABB = Block.makeCuboidShape(0, 0, 0, 16, 1.25, 16);
	protected static final VoxelShape UNPRESSED_AABB = Block.makeCuboidShape(0, 0, 0, 16, 1.5, 16);
	private final CarpetBlock carpet;

	public BlockCarpetedPressurePlate(CarpetBlock origCarpet) {
		super(Sensitivity.MOBS, Block.Properties.create(Material.ROCK, origCarpet.getColor())
				.hardnessAndResistance(0.5F)
				.sound(SoundType.CLOTH)
		);
		this.carpet = origCarpet;
	}

	@Nonnull
	@Override
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		boolean flag = this.getRedstoneStrength(state) > 0;
		return flag ? PRESSED_AABB : UNPRESSED_AABB;
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		return new ItemStack(carpet);
	}
}
