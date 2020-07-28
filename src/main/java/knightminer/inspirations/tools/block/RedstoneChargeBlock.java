package knightminer.inspirations.tools.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirectionalBlock;
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
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class RedstoneChargeBlock extends Block {

	public static final BooleanProperty QUICK = BooleanProperty.create("quick");
	public static final DirectionProperty FACING = DirectionalBlock.FACING;

	public RedstoneChargeBlock() {
		super(Block.Properties.create(Material.MISCELLANEOUS)
				.hardnessAndResistance(0)
				.setLightLevel((state) -> 2)
		);

		this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.DOWN).with(QUICK, false));
	}

	// These should be overwritten by everything.

	@Override
	public boolean isAir(BlockState state, IBlockReader world, BlockPos pos) {
		return true;
	}

	@Deprecated
	@Override
	public boolean isReplaceable(BlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
		return true;
	}

	@Deprecated
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

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		if (!world.isRemote()) {
			world.notifyNeighborsOfStateChange(pos.offset(state.get(FACING)),this);
			world.getPendingBlockTicks().scheduleTick(pos, this, state.get(QUICK) ? 2 : 20);
		}
		super.onBlockPlacedBy(world, pos, state, entity, stack);
	}

	@Deprecated
	@Override
	public void onReplaced(BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
		if (!isMoving && state.getBlock() != newState.getBlock()) {
         super.onReplaced(state, worldIn, pos, newState, isMoving);
         worldIn.notifyNeighborsOfStateChange(pos, this);
         worldIn.notifyNeighborsOfStateChange(pos.offset(state.get(FACING)), this);
      }
	}

	@Deprecated
	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!world.isRemote) {
			world.removeBlock(pos, false);
			world.playSound(null, pos, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
		}
	}


	/* Powering */

	@Deprecated
	@Override
	public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return 15;
	}

	@Deprecated
	@Override
	public int getStrongPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return state.get(FACING).getOpposite() == side ? 15 : 0;
	}

	@Deprecated
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

	@Deprecated
	@Nonnull
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return BOUNDS;
	}

	@Deprecated
	@Nonnull
	@Override
	public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, ISelectionContext context) {
		return VoxelShapes.empty();
	}


	/* Properties */

	@Deprecated
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
		return !world.getBlockState(pos).getFluidState().isEmpty();
	}

	@Deprecated
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

	@Deprecated
	@Nonnull
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}
}
