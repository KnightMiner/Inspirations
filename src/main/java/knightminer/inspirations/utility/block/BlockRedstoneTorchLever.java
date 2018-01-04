package knightminer.inspirations.utility.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static knightminer.inspirations.utility.InspirationsUtility.redstoneTorchLever;
import static knightminer.inspirations.utility.InspirationsUtility.redstoneTorchLeverPowered;

public class BlockRedstoneTorchLever extends BlockLever {

	private boolean powered;
	public BlockRedstoneTorchLever(boolean powered) {
		this.setTickRandomly(true);
		this.powered = powered;
		if(powered) {
			this.setLightLevel(0.5F);
		}
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(redstoneTorchLever);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(redstoneTorchLever);
	}

	/* Redstone powering */

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		// only give power on the connected side
		if(powered && state.getValue(FACING).getFacing() == side) {
			return 15;
		}

		return 0;
	}

	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return getWeakPower(blockState, blockAccess, pos, side);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return true;
		}

		state = togglePower(state);
		Block block = state.getBlock();
		world.setBlockState(pos, state, 3);
		world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, block == redstoneTorchLeverPowered ? 0.6F : 0.5F);
		world.notifyNeighborsOfStateChange(pos, block, false);
		world.notifyNeighborsOfStateChange(pos.offset(state.getValue(FACING).getFacing().getOpposite()), block, false);
		return true;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (powered) {
			worldIn.notifyNeighborsOfStateChange(pos, this, false);
			EnumFacing enumfacing = state.getValue(FACING).getFacing();
			worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing.getOpposite()), this, false);
		}

		super.breakBlock(worldIn, pos, state);
	}


	/* Toggling */

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
		return state.withProperty(POWERED, isBlockPowered(state, world, pos));
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		super.neighborChanged(state, world, pos, block, fromPos);
		if(world.getBlockState(pos).getBlock() != this) {
			return;
		}

		boolean powered = isBlockPowered(state, world, pos);
		if(powered != state.getValue(POWERED)) {
			world.setBlockState(pos, state.withProperty(POWERED, powered), 16);
			if(powered) {
				world.scheduleUpdate(pos, this, this.tickRate(world));
			}
		}
	}

	@Override
	public int tickRate(World worldIn) {
		return 2;
	}

	@Override
	public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		IBlockState newState = togglePower(state).withProperty(POWERED, isBlockPowered(state, world, pos));
		world.setBlockState(pos, newState);
		world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, newState.getBlock() == redstoneTorchLeverPowered ? 0.6F : 0.5F);
	}


	/* Client */

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		if(!powered) {
			return;
		}
		EnumOrientation orientation = state.getValue(FACING);
		EnumFacing facing = orientation.getFacing();
		double x = pos.getX() + 0.5D;
		double y = pos.getY() + 0.7D;
		double z = pos.getZ() + 0.5D;

		if (facing.getAxis().isHorizontal()) {
			y -= 0.40D;
			world.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 0.0D, 0.0D, 0.0D);
		} else {
			switch(orientation) {
				case UP_X:
				case DOWN_X:
					x -= 0.4D;
					break;
				case UP_Z:
				case DOWN_Z:
					z -= 0.4D;
					break;
			}
			y -= 0.05D * facing.getFrontOffsetY();
			world.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}


	/* Helpers */

	protected IBlockState togglePower(IBlockState state) {
		EnumOrientation orientation = state.getValue(FACING);
		boolean powered = state.getValue(POWERED);
		Block block = state.getBlock() == redstoneTorchLever ? redstoneTorchLeverPowered : redstoneTorchLever;

		return block.getDefaultState().withProperty(FACING, orientation).withProperty(POWERED, powered);
	}

	protected boolean isBlockPowered(IBlockState state, World world, BlockPos pos) {
		EnumFacing facing = state.getValue(FACING).getFacing().getOpposite();
		for (EnumFacing side : EnumFacing.values()) {
			if(side == facing) {
				continue;
			}
			BlockPos offset = pos.offset(side);
			IBlockState offsetState = world.getBlockState(offset);
			if(offsetState.getBlock() == Blocks.REDSTONE_WIRE) {
				if(offsetState.getValue(BlockRedstoneWire.POWER) > 0) {
					return true;
				}
			} else {
				if(world.getRedstonePower(offset, side) > 0) {
					return true;
				}
			}
		}

		return false;
	}
}
