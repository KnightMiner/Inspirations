package knightminer.inspirations.building.block;

import java.util.Random;

import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTorchLever extends BlockTorch {

	public static final PropertyBool POWERED = BlockLever.POWERED;
	public static final PropertyDirection SIDE = PropertyDirection.create("side", EnumFacing.Plane.HORIZONTAL);
	public BlockTorchLever() {
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(FACING, EnumFacing.UP)
				.withProperty(SIDE, EnumFacing.NORTH)
				.withProperty(POWERED, false));
		this.setLightLevel(1.0F);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {FACING, POWERED, SIDE});
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		EnumFacing facing = state.getValue(FACING);
		double x = pos.getX() + 0.5D;
		double y = pos.getY() + 0.7D;
		double z = pos.getZ() + 0.5D;

		if (facing.getAxis().isHorizontal()) {
			EnumFacing opposite = facing.getOpposite();
			int offsetX = opposite.getFrontOffsetX();
			int offsetZ = opposite.getFrontOffsetZ();
			if(state.getValue(POWERED)) {
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + 0.10D * offsetX, y + 0.08D, z + 0.10D * offsetZ, 0.0D, 0.0D, 0.0D);
				world.spawnParticle(EnumParticleTypes.FLAME,        x + 0.10D * offsetX, y + 0.08D, z + 0.10D * offsetZ, 0.0D, 0.0D, 0.0D);
			} else {
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + 0.27D * offsetX, y + 0.22D, z + 0.27D * offsetZ, 0.0D, 0.0D, 0.0D);
				world.spawnParticle(EnumParticleTypes.FLAME,        x + 0.27D * offsetX, y + 0.22D, z + 0.27D * offsetZ, 0.0D, 0.0D, 0.0D);
			}
		} else {
			if(state.getValue(POWERED)) {
				EnumFacing side = state.getValue(SIDE).getOpposite();
				int offsetX = side.getFrontOffsetX();
				int offsetZ = side.getFrontOffsetZ();
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x - 0.23D * offsetX, y - 0.05D, z - 0.23D * offsetZ, 0.0D, 0.0D, 0.0D);
				world.spawnParticle(EnumParticleTypes.FLAME,        x - 0.23D * offsetX, y - 0.05D, z - 0.23D * offsetZ, 0.0D, 0.0D, 0.0D);
			} else {
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0D, 0.0D, 0.0D);
				world.spawnParticle(EnumParticleTypes.FLAME,        x, y, z, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	/*
	 * Powering
	 */

	/**
	 * Called when the block is right clicked by a player.
	 */
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing sideHit, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return true;
		}

		// update state
		state = state.cycleProperty(POWERED).withProperty(SIDE, playerIn.getHorizontalFacing());
		world.setBlockState(pos, state, 3);
		// play sound
		float pitch = state.getValue(POWERED) ? 0.6F : 0.5F;
		world.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, pitch);
		// notify update
		world.notifyNeighborsOfStateChange(pos, this, false);
		world.notifyNeighborsOfStateChange(pos.offset(state.getValue(FACING).getOpposite()), this, false);
		return true;
	}


	/**
	 * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
	 */
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		// if powered, send updates for power
		if (state.getValue(POWERED)) {
			world.notifyNeighborsOfStateChange(pos, this, false);
			world.notifyNeighborsOfStateChange(pos.offset(state.getValue(FACING).getOpposite()), this, false);
		}

		super.breakBlock(world, pos, state);
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return blockState.getValue(POWERED) ? 15 : 0;
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		if (!state.getValue(POWERED)) {
			return 0;
		}
		return state.getValue(FACING) == side ? 15 : 0;
	}

	/**
	 * Can this block provide power. Only wire currently seems to have this change based on its state.
	 */
	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}


	/*
	 * Metadata
	 */

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing = EnumFacing.getHorizontal(meta & 3);
		IBlockState state = this.getDefaultState().withProperty(POWERED, (meta & 8) > 1).withProperty(SIDE, facing);
		// if we have the third bit set, we are on the floor, so set facing to UP
		if((meta & 4) > 0) {
			facing = EnumFacing.UP;
		}
		return state.withProperty(FACING, facing);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;
		EnumFacing facing = state.getValue(FACING);
		// if we are facing up, use the side for the first two bits and set the third one
		if(facing == EnumFacing.UP) {
			i |= 4 + state.getValue(SIDE).getHorizontalIndex();
		} else {
			// otherwise use facing for the first two with the third unset
			i |= facing.getHorizontalIndex();
		}
		// set powered if needed
		if (state.getValue(POWERED)) {
			i |= 8;
		}

		return i;
	}
}
