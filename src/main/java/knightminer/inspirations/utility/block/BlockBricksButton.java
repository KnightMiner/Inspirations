package knightminer.inspirations.utility.block;

import com.google.common.collect.ImmutableMap;
import knightminer.inspirations.library.Util;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import slimeknights.mantle.block.EnumBlock;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Random;

public class BlockBricksButton extends EnumBlock<BlockBricksButton.BrickType> {

	public static final PropertyEnum<BrickType> TYPE = PropertyEnum.create("type", BrickType.class);
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final PropertyBool POWERED = BlockButton.POWERED;
	public BlockBricksButton() {
		super(Material.ROCK, TYPE, BrickType.class);

		this.setTickRandomly(true);
		this.setCreativeTab(CreativeTabs.REDSTONE);
		this.setHardness(1.5F);
		this.setResistance(10.0F);
		this.setSoundType(SoundType.STONE);
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(TYPE, BrickType.BRICKS)
				.withProperty(FACING, EnumFacing.NORTH)
				.withProperty(POWERED, false));
	}


	/* Blockstate */

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE, FACING, POWERED);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState()
				.withProperty(TYPE, BrickType.fromMeta(meta & 1))
				.withProperty(FACING, EnumFacing.getHorizontal(meta >> 1))
				.withProperty(POWERED, (meta & 8) > 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(TYPE).getMeta()
				| (state.getValue(FACING).getHorizontalIndex() << 1)
				| (state.getValue(POWERED) ? 8 : 0);
	}

	@Deprecated
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getStateFromMeta(meta).withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Deprecated
	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Deprecated
	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(TYPE).getMeta();
	}

	@Override
	protected ItemStack getSilkTouchDrop(IBlockState state) {
		return new ItemStack(this, 1, state.getValue(TYPE).getMeta());
	}

	/* Pressing the button */

	@Override
	public int tickRate(World worldIn) {
		return 20;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		// if you did not click the secret button, no button for you
		if(!Util.clickedAABB(getButtonBox(state), hitX, hitY, hitZ)) {
			return false;
		}

		// if already powered, we done here
		if (state.getValue(POWERED)) {
			return true;
		}

		world.setBlockState(pos, state.withProperty(POWERED, true), 3);
		world.markBlockRangeForRenderUpdate(pos, pos);
		world.playSound(player, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
		world.notifyNeighborsOfStateChange(pos, this, false);
		world.scheduleUpdate(pos, this, this.tickRate(world));
		return true;
	}

	@Override
	public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (world.isRemote) {
			return;
		}
		if ((state.getValue(POWERED))) {
			world.setBlockState(pos, state.withProperty(POWERED, false));
			world.notifyNeighborsOfStateChange(pos, this, false);
			world.playSound(null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
			world.markBlockRangeForRenderUpdate(pos, pos);
		}
	}

	private static final ImmutableMap<EnumFacing, AxisAlignedBB> BRICK_BUTTON;
	private static final ImmutableMap<EnumFacing, AxisAlignedBB> NETHER_BUTTON;
	static {
		ImmutableMap.Builder<EnumFacing, AxisAlignedBB> bounds = ImmutableMap.builder();
		bounds.put(EnumFacing.NORTH, new AxisAlignedBB(0.3125, 0.3125, 0,      0.75,   0.5, 0.0625));
		bounds.put(EnumFacing.SOUTH, new AxisAlignedBB(0.25,   0.3125, 0.9375, 0.6875, 0.5, 1     ));
		bounds.put(EnumFacing.WEST,  new AxisAlignedBB(0,      0.3125, 0.25,   0.0625, 0.5, 0.6875));
		bounds.put(EnumFacing.EAST,  new AxisAlignedBB(0.9375, 0.3125, 0.3125, 1,      0.5, 0.75  ));
		BRICK_BUTTON = bounds.build();

		bounds = ImmutableMap.builder();
		bounds.put(EnumFacing.NORTH, new AxisAlignedBB(0.375,  0.5, 0,      0.8125, 0.6875, 0.0625));
		bounds.put(EnumFacing.SOUTH, new AxisAlignedBB(0.1875, 0.5, 0.9375, 0.625,  0.6875, 1     ));
		bounds.put(EnumFacing.WEST,  new AxisAlignedBB(0,      0.5, 0.1875, 0.0625, 0.6875, 0.625 ));
		bounds.put(EnumFacing.EAST,  new AxisAlignedBB(0.9375, 0.5, 0.375,  1,      0.6875, 0.8125));
		NETHER_BUTTON = bounds.build();
	}

	private static AxisAlignedBB getButtonBox(IBlockState state) {
		EnumFacing facing = state.getValue(FACING);
		if(state.getValue(TYPE) == BrickType.BRICKS) {
			return BRICK_BUTTON.get(facing);
		}

		return NETHER_BUTTON.get(facing);
	}


	/* Redstone logic */

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if (state.getValue(POWERED)) {
			world.notifyNeighborsOfStateChange(pos, this, false);
		}

		super.breakBlock(world, pos, state);
	}

	@Deprecated
	@Override
	public int getWeakPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return state.getValue(POWERED) ? 15 : 0;
	}

	@Deprecated
	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		// we may be a button, but we act as though ourself is the block that is powered
		return 0;
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

	public enum BrickType implements IStringSerializable, EnumBlock.IEnumMeta {
		BRICKS,
		NETHER;

		private int meta;
		BrickType() {
			this.meta = ordinal();
		}

		@Override
		public int getMeta() {
			return meta;
		}

		@Override
		public String getName() {
			return name().toLowerCase(Locale.US);
		}

		public static BrickType fromMeta(int i) {
			if(i < 0 || i > values().length) {
				i = 0;
			}
			return values()[i];
		}
	}
}
