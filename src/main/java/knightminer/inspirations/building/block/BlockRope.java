package knightminer.inspirations.building.block;

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
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

public class BlockRope extends EnumBlock<BlockRope.RopeType> {

	public static final PropertyEnum<Rungs> RUNGS = PropertyEnum.create("rungs", Rungs.class);
	public static final PropertyEnum<RopeType> TYPE = PropertyEnum.create("type", RopeType.class);
	public static final PropertyBool BOTTOM = PropertyBool.create("bottom");
	public BlockRope() {
		super(Material.CARPET, TYPE, RopeType.class);

		this.setCreativeTab(CreativeTab.DECORATIONS);
		this.setHardness(0.5f);
		this.setDefaultState(this.getBlockState().getBaseState().withProperty(TYPE, RopeType.ROPE).withProperty(RUNGS, Rungs.NONE));
		IBlockState chain = this.getDefaultState().withProperty(TYPE, RopeType.CHAIN);
		for (Rungs rungs : Rungs.values()) {
			this.setHarvestLevel("pickaxe", 0, chain.withProperty(RUNGS, rungs));
		}
	}

	/* Blockstate */

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE, RUNGS, BOTTOM);
	}

	@Deprecated
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		BlockPos down = pos.down();
		return state.withProperty(BOTTOM, !canConnectTo(world.getBlockState(down), world, down, EnumFacing.UP));
	}

	/**
	 * Returns true if the block can connect to the given state
	 * @param state  State to check
	 * @param world  World access
	 * @param pos    Block position
	 * @param side   Side to connect
	 * @return  True if it can connect
	 */
	private boolean canConnectTo(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		if(state.getBlock() == this) {
			return true;
		}

		BlockFaceShape shape = state.getBlockFaceShape(world, pos, side);
		return shape == BlockFaceShape.CENTER || shape == BlockFaceShape.CENTER_BIG || shape == BlockFaceShape.SOLID;
	}

	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(prop, fromMeta(meta & 0b0011)).withProperty(RUNGS, Rungs.fromMeta(meta >> 2));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(prop).getMeta() | (state.getValue(RUNGS).getMeta() << 2);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(prop).getMeta();
	}


	/* Metal props */

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
		return canConnectTo(state, world, pos, EnumFacing.DOWN);
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
		ItemStack stack = player.getHeldItem(hand);

		// if rope, extend
		if(Block.getBlockFromItem(stack.getItem()) == this) {
			return extendRope(world, pos, state, side, player, stack, clickX, clickY, clickZ);
		}

		return false;
	}

	private boolean extendRope(World world, BlockPos pos, IBlockState state, EnumFacing side, EntityPlayer player, ItemStack stack, float clickX, float clickY, float clickZ) {
		// no need to check verticals, one is not possible and the other normal block placement
		// also skip if wrong rope type
		RopeType type = state.getValue(TYPE);
		if(stack.getMetadata() != type.getMeta()) {
			return false;
		}

		// find the first block at the bottom of the rope
		BlockPos next = pos.down();
		IBlockState below = world.getBlockState(next);
		while(below.getBlock() == this && below.getValue(TYPE) == type) {
			next = next.down();
			below = world.getBlockState(next);
		}
		if(this.canPlaceBlockAt(world, next)) {
			ItemBlock itemBlock = (ItemBlock)stack.getItem();
			IBlockState newState = this.getDefaultState().withProperty(TYPE, type);
			if(itemBlock.placeBlockAt(stack, player, world, next, side, clickX, clickY, clickZ, newState)) {
				SoundType soundtype = newState.getBlock().getSoundType(newState, world, pos, player);
				world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				if(!player.capabilities.isCreativeMode) {
					stack.shrink(1);
				}
			}
		}

		return true;
	}


	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		// drop sticks if rungs
		super.getDrops(drops, world, pos, state, fortune);
		if (state.getValue(RUNGS) != Rungs.NONE) {
			drops.add(new ItemStack(state.getValue(TYPE).getItem(), 4));
		}
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		// when breaking, place all items from ropes below at the position of this rope
		// break all blocks below that are ropes
		RopeType type = state.getValue(TYPE);
		BlockPos next = pos.down();
		IBlockState below = world.getBlockState(next);
		int count = 0;
		int extra = 0;
		// go down to the bottom
		while(below.getBlock() == this && below.getValue(TYPE) == type) {
			count++;
			if (below.getValue(RUNGS) != Rungs.NONE) {
				extra++;
			}
			next = next.down();
			below = world.getBlockState(next);
		}
		// then break them coming back up
		for(int i = 0; i < count; i++) {
			next = next.up();
			world.destroyBlock(next, false);
		}

		// then spawn their items up here
		ItemStack drops = new ItemStack(this, count, type.getMeta());
		spawnAsEntity(world, pos, drops);
		if (extra > 0) {
			ItemStack extraDrop = new ItemStack(type.getItem(), extra*4);
			spawnAsEntity(world, pos, extraDrop);
		}
	}


	/* Block properties */

	@Override
	public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
		return true;
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

	@Deprecated
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}


	/* Bounds */

	// order is X, Y, Z
	protected static final AxisAlignedBB[] BOUNDS = {
			new AxisAlignedBB(0.375,  0, 0.375,  0.625, 1,  0.625),
			new AxisAlignedBB(0.0625, 0, 0.375,  0.9375, 1, 0.625),
			new AxisAlignedBB(0.375,  0, 0.0625, 0.625, 1,  0.9375)
	};
	protected static final AxisAlignedBB[] BOUNDS_BOTTOM = {
			new AxisAlignedBB(0.375,  0.25, 0.375,  0.625,  1, 0.625),
			new AxisAlignedBB(0.0625, 0.25, 0.375,  0.9375, 1, 0.625),
			new AxisAlignedBB(0.375,  0.25, 0.0625, 0.625,  1, 0.9375)
	};

	@Deprecated
	@Nonnull
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		int rungs = state.getValue(RUNGS).getMeta();
		if(state.getActualState(source, pos).getValue(BOTTOM)) {
			return BOUNDS_BOTTOM[rungs];
		}
		return BOUNDS[rungs];
	}

	public enum RopeType implements IStringSerializable, EnumBlock.IEnumMeta {
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

		public Item getItem() {
			// TODO: in 1.14, use bamboo for vines
			if(this == CHAIN) {
				return Items.IRON_NUGGET;
			}
			return Items.STICK;
		}
	}

	public enum Rungs implements IStringSerializable, EnumBlock.IEnumMeta {
		NONE,
		X,
		Z;

		private int meta;
		Rungs() {
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

		public static Rungs fromMeta(int meta) {
			if (meta < 0 || meta > values().length) {
				return NONE;
			}
			return values()[meta];
		}

		public static Rungs fromAxis(EnumFacing.Axis axis) {
			switch(axis) {
				case X: return X;
				case Z: return Z;
			}
			return NONE;
		}
	}
}
