package knightminer.inspirations.building.block;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.mantle.client.CreativeTab;

public class BlockRope extends EnumBlock<BlockRope.RopeType> {

	public static final PropertyEnum<RopeType> TYPE = PropertyEnum.create("type", RopeType.class);
	public static final PropertyBool BOTTOM = PropertyBool.create("bottom");
	public BlockRope() {
		super(Material.CARPET, TYPE, RopeType.class);

		this.setCreativeTab(CreativeTab.BUILDING_BLOCKS);
		this.setHardness(0.5f);
		this.setHarvestLevel("pickaxe", 0, this.getDefaultState().withProperty(TYPE, RopeType.CHAIN));
	}

	/* Blockstate */

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE, BOTTOM);
	}

	/**
	 * Get the actual Block state of this Block at the given position. This applies properties not visible in the
	 * metadata, such as fence connections.
	 */
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.withProperty(BOTTOM, world.getBlockState(pos.down()).getBlock() != this);
	}

	@Override
	@Deprecated
	public Material getMaterial(IBlockState state){
		if(state.getValue(TYPE) == RopeType.CHAIN) {
			return Material.IRON;
		}
		return super.getMaterial(state);
	}

	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
		switch(state.getValue(TYPE)) {
			case ROPE:
				return SoundType.CLOTH;
			case CHAIN:
				return SoundType.METAL;
			case VINE:
				return SoundType.PLANT;
		}
		return super.getSoundType(state, world, pos, entity);
	}

	@Override
	@Deprecated
	public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
		if(state.getValue(TYPE) == RopeType.CHAIN) {
			return 5.0f;
		}
		return super.getBlockHardness(state, worldIn, pos);
	}

	@Override
	public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
		if(world.getBlockState(pos).getValue(TYPE) == RopeType.CHAIN) {
			return 30f;
		}
		return super.getExplosionResistance(world, pos, exploder, explosion);
	}


	/* Ropey logic */

	/**
	 * Checks if this block can be placed exactly at the given position.
	 */
	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return super.canPlaceBlockAt(world, pos) && isValidRope(world, pos);
	}

	private boolean isValidRope(World world, BlockPos pos) {
		BlockPos up = pos.up();
		IBlockState state = world.getBlockState(up);
		return state.isSideSolid(world, up, EnumFacing.DOWN) || state.getBlock() == this;
	}

	/**
	 * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
	 * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
	 * block, etc.
	 */
	@Override
	@Deprecated
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		// if the rope is not valid, break it
		if (!this.isValidRope(world, pos)) {
			this.dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}

		super.neighborChanged(state, world, pos, blockIn, fromPos);
	}

	// right click with a rope to extend downwards
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float clickX, float clickY, float clickZ) {
		// no need to check verticals, one is not possible and the other normal block placement
		if(side.getAxis().isVertical()) {
			return false;
		}

		ItemStack stack = player.getHeldItem(hand);
		// check if the item is the same type as us
		if(Block.getBlockFromItem(stack.getItem()) != this || this.getStateFromMeta(stack.getMetadata()) != state) {
			return false;
		}

		// find the first block at the bottom of the rope
		BlockPos next = pos.down();
		while(world.getBlockState(next) == state) {
			next = next.down();
		}
		if(this.canPlaceBlockAt(world, next)) {
			ItemBlock itemBlock = (ItemBlock)stack.getItem();
			if(itemBlock.placeBlockAt(stack, player, world, next, side, clickX, clickY, clickZ, state)) {
				IBlockState newState = world.getBlockState(pos);
				SoundType soundtype = newState.getBlock().getSoundType(newState, world, pos, player);
				world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				if(!player.capabilities.isCreativeMode) {
					stack.shrink(1);
				}
			}
		}

		return true;
	}

	// when breaking, place all items from ropes below at the position of this rope
	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		// break all blocks below that are ropes
		BlockPos next = pos.down();
		int count = 0;
		// go down to the bottom
		while(world.getBlockState(next) == state) {
			next = next.down();
			count++;
		}
		// then break them coming back up
		for(int i = 0; i < count; i++) {
			next = next.up();
			world.destroyBlock(next, false);
		}

		// then spawn their items up here
		ItemStack drops = new ItemStack(this, count, this.getMetaFromState(state));
		spawnAsEntity(world, pos, drops);
	}


	/* Block properties */

	@Override
	public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
		return true;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	@Deprecated
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}


	/* Bounds */

	protected static final AxisAlignedBB BOUNDS = new AxisAlignedBB(0.375, 0, 0.375, 0.625, 1, 0.625);
	protected static final AxisAlignedBB BOUNDS_BOTTOM = new AxisAlignedBB(0.375, 0.25, 0.375, 0.625, 1, 0.625);
	@Nonnull
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		if(state.getActualState(source, pos).getValue(BOTTOM)) {
			return BOUNDS_BOTTOM;
		}
		return BOUNDS;

	}

	public static enum RopeType implements IStringSerializable, EnumBlock.IEnumMeta {
		ROPE,
		CHAIN,
		VINE;

		private int meta;
		RopeType() {
			this.meta = ordinal();
		}

		@Override
		public int getMeta() {
			return meta;
		}

		@Override
		public String getName() {
			return this.name().toLowerCase(Locale.US);
		}
	}

}
