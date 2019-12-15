package knightminer.inspirations.utility.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BlockRedstoneCharge extends Block {

	public static final PropertyBool QUICK = PropertyBool.create("quick");
	public static final PropertyDirection FACING = BlockDirectional.FACING;
	public BlockRedstoneCharge() {
		super(Material.CIRCUITS);
		this.setHardness(0);
		this.setLightLevel(0.5F);

		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.DOWN).withProperty(QUICK, false));
	}


	/* Blockstate */

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, QUICK);
	}

	@Deprecated
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState()
				.withProperty(QUICK, (meta & 8) > 0)
				.withProperty(FACING, EnumFacing.getFront(meta & 7));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex() | (state.getValue(QUICK) ? 8 : 0);
	}


	/* Fading */

	@Override
	public int tickRate(World world) {
		return 20;
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		if (!world.isRemote) {
			world.notifyNeighborsOfStateChange(pos.offset(state.getValue(FACING)), this, false);
			world.scheduleUpdate(pos, this, state.getValue(QUICK) ? 2 : 20);
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		world.notifyNeighborsOfStateChange(pos, this, false);
		world.notifyNeighborsOfStateChange(pos.offset(state.getValue(FACING)), this, false);

		super.breakBlock(world, pos, state);
	}

	@Override
	public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (!world.isRemote) {
			world.setBlockToAir(pos);
			world.playSound(null, pos, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
		}
	}


	/* Powering */

	@Deprecated
	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return 15;
	}

	@Deprecated
	@Override
	public int getStrongPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return state.getValue(FACING).getOpposite() == side ? 15 : 0;
	}

	@Deprecated
	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
		return false;
	}


	/* Bounds */

	protected static final AxisAlignedBB BOUNDS = new AxisAlignedBB(0.375, 0.375, 0.375, 0.625, 0.625, 0.625);

	@Deprecated
	@Nonnull
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BOUNDS;
	}

	@Override
	@Deprecated
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		return NULL_AABB;
	}


	/* Properties */

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return super.canPlaceBlockAt(world, pos) && !world.getBlockState(pos).getMaterial().isLiquid();
	}

	@Override
	@Deprecated
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
		// no placey
		return BlockFaceShape.UNDEFINED;
	}

	@Deprecated
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Deprecated
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		EnumFacing facing = stateIn.getValue(FACING);

		int offX = facing.getFrontOffsetX();
		int offY = facing.getFrontOffsetY();
		int offZ = facing.getFrontOffsetZ();

		double x = pos.getX() + 0.5;
		double y = pos.getY() + 0.5;
		double z = pos.getZ() + 0.5;
		for(double i = 0; i <= 0.25; i += 0.05) {
			worldIn.spawnParticle(EnumParticleTypes.REDSTONE, x + offX * i, y + offY * i, z + offZ * i, 0, 0, 0);
		}
	}

	@Deprecated
	@Nonnull
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}
}
