package knightminer.inspirations.tools.block;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockRedstoneCharge extends Block {

	public static final BooleanProperty QUICK = BooleanProperty.create("quick");
	public static final DirectionProperty FACING = DirectionalBlock.FACING;

	public BlockRedstoneCharge() {
		super(Block.Properties.create(Material.MISCELLANEOUS)
				.hardnessAndResistance(0)
				.lightValue(2)
		);

		this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.DOWN).with(QUICK, false));
	}

	// These should be overwritten by everything.

	@Override
	public boolean isAir(BlockState state, IBlockReader world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean isReplaceable(BlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
		return true;
	}

	@Override
	public PushReaction getPushReaction(BlockState p_149656_1_) {
		return PushReaction.DESTROY;
	}

	/* Blockstate */

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, QUICK);
	}

	/* Fading */
	/**
	 * How many world ticks before ticking
	 */
	@Override
	public int tickRate(IWorldReader world) {
		return 20;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		if (!world.isRemote()) {
			world.notifyNeighbors(pos.offset(state.get(FACING)), this);
			world.getPendingBlockTicks().scheduleTick(pos, this, state.get(QUICK) ? 2 : 20);
		}
		super.onBlockPlacedBy(world, pos, state, entity, stack);
	}

	@Override
	public void onReplaced(BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
		if (!isMoving && state.getBlock() != newState.getBlock()) {
         super.onReplaced(state, worldIn, pos, newState, isMoving);
         worldIn.notifyNeighbors(pos, this);
         worldIn.notifyNeighbors(pos.offset(state.get(FACING)), this);
      }
	}


	@Override
	public void tick(BlockState state, World world, BlockPos pos, Random random) {
		if (!world.isRemote) {
			world.removeBlock(pos, false);
			world.playSound(null, pos, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
		}
	}


	/* Powering */

	@Override
	public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return 15;
	}

	@Override
	public int getStrongPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return state.get(FACING).getOpposite() == side ? 15 : 0;
	}

	@Override
	public boolean canProvidePower(BlockState state) {
		return true;
	}

	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
		return false;
	}


	/* Bounds */
	private static final VoxelShape BOUNDS = Block.makeCuboidShape(6, 6, 6, 10, 10, 10);


	@Nonnull
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return BOUNDS;
	}

	@Nonnull
	@Override
	public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, ISelectionContext context) {
		return VoxelShapes.empty();
	}


	/* Properties */

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
		return !world.getBlockState(pos).getFluidState().isEmpty();
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return isValidPosition(getDefaultState(), context.getWorld(), context.getPos()) ? getDefaultState() : Blocks.AIR.getDefaultState();
	}

	@Override
    @OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		Direction facing = stateIn.get(FACING);

		int offX = facing.getXOffset();
		int offY = facing.getYOffset();
		int offZ = facing.getZOffset();

		double x = pos.getX() + 0.5;
		double y = pos.getY() + 0.5;
		double z = pos.getZ() + 0.5;
		for(double i = 0; i <= 0.25; i += 0.05) {
			worldIn.addParticle(RedstoneParticleData.REDSTONE_DUST, x + offX * i, y + offY * i, z + offZ * i, 0, 0, 0);
		}
	}

	@Nonnull
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}
}
