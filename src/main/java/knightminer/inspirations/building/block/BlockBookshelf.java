package knightminer.inspirations.building.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nonnull;
import com.google.common.collect.ImmutableMap;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.tileentity.TileBookshelf;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.PropertyUnlistedInteger;
import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.block.BlockInventory;
import slimeknights.mantle.property.PropertyString;

public class BlockBookshelf extends BlockInventory implements ITileEntityProvider {

	public static final PropertyEnum<BookshelfType> TYPE = PropertyEnum.create("type", BookshelfType.class);
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final PropertyString TEXTURE = TextureBlockUtil.TEXTURE_PROP;
	public static final IUnlistedProperty<Integer> BOOKS = new PropertyUnlistedInteger("books");

	public BlockBookshelf() {
		super(Material.WOOD);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		this.setCreativeTab(CreativeTabs.DECORATIONS);
		this.setHardness(2.0F);
		this.setResistance(5.0F);
		this.setSoundType(SoundType.WOOD);
	}

	@Override
	protected ExtendedBlockState createBlockState() {
		return new ExtendedBlockState(this, new IProperty<?>[]{TYPE, FACING}, new IUnlistedProperty[]{TEXTURE, BOOKS});
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState()
				.withProperty(TYPE, BookshelfType.fromMeta(meta & 3))
				.withProperty(FACING, EnumFacing.getHorizontal(meta >> 2));
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex() << 2
				| state.getValue(TYPE).getMeta();
	}

	/**
	 * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
	 * IBlockstate
	 */
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getStateFromMeta(meta).withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		TextureBlockUtil.placeTextureBlock(world, pos, stack);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileBookshelf();
	}

	@Override
	protected boolean openGui(EntityPlayer player, World world, BlockPos pos) {
		player.openGui(Inspirations.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}


	/* Activation */
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float clickX, float clickY, float clickZ) {
		EnumFacing facing = state.getValue(FACING);

		// skip sides, we don't need them
		if(facing != side) {
			return false;
		}

		// if sneaking, just do the GUI
		if(player.isSneaking()) {
			return world.isRemote || openGui(player, world, pos);
		}

		// if we did not click a book, just do the GUI as well
		int book = bookClicked(facing, clickX, clickY, clickZ);
		if(book == -1) {
			return world.isRemote || openGui(player, world, pos);
		}

		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileBookshelf) {
			// try interacting
			if (((TileBookshelf) te).interact(player, hand, book)) {
				return true;
			}

			// if the offhand can interact, return false so we can process it later
			if (InspirationsRegistry.isBook(player.getHeldItemOffhand())) {
				return false;
			}
		}

		return true;
	}

	private static int bookClicked(EnumFacing facing, float clickX, float clickY, float clickZ) {
		// if we did not click between the shelves, ignore
		if(clickY < 0.0625 || clickY > 0.9375) {
			return -1;
		}
		int shelf = 0;
		// if we clicked below the middle shelf, add 7 to the book
		if(clickY <= 0.4375) {
			shelf = 7;
			// if we clicked below the top shelf but not quite in the middle shelf, no book
		} else if(clickY < 0.5625) {
			return -1;
		}

		int offX = facing.getFrontOffsetX();
		int offZ = facing.getFrontOffsetZ();
		double x1 = offX == -1 ? 0.625 : 0.0625;
		double z1 = offZ == -1 ? 0.625 : 0.0625;
		double x2 = offX ==  1 ? 0.375 : 0.9375;
		double z2 = offZ ==  1 ? 0.375 : 0.9375;
		// ensure we clicked within a shelf, not outside one
		if(clickX < x1 || clickX > x2 || clickZ < z1 || clickZ > z2) {
			return -1;
		}

		// okay, so now we know we clicked in the book area, so just take the position clicked to determine where
		EnumFacing dir = facing.rotateYCCW();
		// subtract one pixel and multiply by our direction
		double clicked = (dir.getFrontOffsetX() * clickX) + (dir.getFrontOffsetZ() * clickZ) - 0.0625;
		// if negative, just add one to wrap back around
		if(clicked < 0) {
			clicked = 1 + clicked;
		}

		// multiply by 8 to account for extra 2 pixels
		return shelf + Math.min((int)(clicked * 8), 7);
	}

	/*
	 * Bounds
	 */
	private static final ImmutableMap<EnumFacing, AxisAlignedBB> BOUNDS;
	private static final ImmutableMap<EnumFacing, AxisAlignedBB[]> TRACE_BOUNDS;
	static {
		// main bounds for collision and bounding box
		ImmutableMap.Builder<EnumFacing, AxisAlignedBB> bounds = ImmutableMap.builder();

		// shelf bounds
		ImmutableMap.Builder<EnumFacing, AxisAlignedBB[]> builder = ImmutableMap.builder();
		for(EnumFacing side : EnumFacing.HORIZONTALS) {
			int offX = side.getFrontOffsetX();
			int offZ = side.getFrontOffsetZ();
			double x1 = offX == -1 ? 0.5 : 0;
			double z1 = offZ == -1 ? 0.5 : 0;
			double x2 = offX ==  1 ? 0.5 : 1;
			double z2 = offZ ==  1 ? 0.5 : 1;

			bounds.put(side, new AxisAlignedBB(x1, 0, z1, x2, 1, z2));
			builder.put(side, new AxisAlignedBB[] {
					new AxisAlignedBB(x1,  0,      z1,  x2,  0.0625, z2), // bottom shelf
					new AxisAlignedBB(x1,  0.4375, z1,  x2,  0.5625, z2), // middle shelf
					new AxisAlignedBB(x1,  0.9375, z1,  x2,  1,      z2), // top shelf

					new AxisAlignedBB(offX == -1 ? 0.625 : 0, 0, offZ == -1 ? 0.625 : 0, offX ==  1 ? 0.375 : 1, 1, offZ ==  1 ? 0.375 : 1), // back wall
					new AxisAlignedBB(x1, 0, z1, offX == 0 ? 0.0625 : x2, 1, offZ == 0 ? 0.0625 : z2), // side wall 1
					new AxisAlignedBB(offX == 0 ? 0.9375 : x1, 0, offZ == 0 ? 0.9375 : z1, x2, 1, z2), // side wall 2
			});
		}
		BOUNDS = bounds.build();
		TRACE_BOUNDS = builder.build();
	}

	@Nonnull
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BOUNDS.get(state.getValue(FACING));
	}

	@Deprecated
	@Override
	public RayTraceResult collisionRayTrace(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {
		List<RayTraceResult> list = new ArrayList<>(6);
		for(AxisAlignedBB bound : TRACE_BOUNDS.get(state.getValue(FACING))) {
			list.add(rayTrace(pos, start, end, bound));
		}

		// compare results
		RayTraceResult result = null;
		double max = 0.0D;
		for(RayTraceResult raytraceresult : list) {
			if(raytraceresult != null) {
				double distance = raytraceresult.hitVec.squareDistanceTo(end);
				if(distance > max) {
					result = raytraceresult;
					max = distance;
				}
			}
		}

		return result;
	}

	/*
	 * Redstone
	 */

	/**
	 * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
	 */
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		// if powered, send updates for power
		if (getPower(state, world, pos) > 0) {
			world.notifyNeighborsOfStateChange(pos, this, false);
			world.notifyNeighborsOfStateChange(pos.offset(state.getValue(FACING).getOpposite()), this, false);
		}

		super.breakBlock(world, pos, state);
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return getPower(state, blockAccess, pos);
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		if (state.getValue(FACING) != side) {
			return 0;
		}

		return getPower(state, blockAccess, pos);

	}

	private int getPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos) {
		if(InspirationsBuilding.redstoneBook == null) {
			return 0;
		}

		TileEntity te = blockAccess.getTileEntity(pos);
		if(te instanceof TileBookshelf) {
			return ((TileBookshelf) te).getPower();
		}
		return 0;
	}

	/**
	 * Can this block provide power. Only wire currently seems to have this change based on its state.
	 */
	@Override
	public boolean canProvidePower(IBlockState state) {
		// ensure we have the redstone book, since it comes from the redstone module
		return InspirationsBuilding.redstoneBook != null;
	}


	/*
	 * Block properties
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Nonnull
	@Override
	public IBlockState getExtendedState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
		IExtendedBlockState extendedState = (IExtendedBlockState) state;

		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileBookshelf) {
			return ((TileBookshelf)te).writeExtendedBlockState(extendedState);
		}

		return super.getExtendedState(state, world, pos);
	}

	@Override
	@Deprecated
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
		// allows placing stuff on the back
		return side == state.getValue(FACING).getOpposite() ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
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
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror) {
		return state.withRotation(mirror.toRotation(state.getValue(FACING)));
	}

	/* Drops */

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for(BookshelfType type : BookshelfType.values()) {
			TextureBlockUtil.addBlocksFromOredict("slabWood", this, type.getMeta(), list);
		}
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(TYPE).getMeta();
	}

	@Nonnull
	@Override
	public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
		return TextureBlockUtil.getBlockItemStack(world, pos, state);
	}

	@Override
	public boolean removedByPlayer(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
		// we pull up a few calls to this point in time because we still have the TE here
		// the execution otherwise is equivalent to vanilla order
		this.onBlockDestroyedByPlayer(world, pos, state);
		if(willHarvest) {
			this.harvestBlock(world, player, pos, state, world.getTileEntity(pos), player.getHeldItemMainhand());
		}

		world.setBlockToAir(pos);
		// return false to prevent the above called functions to be called again
		// side effect of this is that no xp will be dropped. but it shoudln't anyway from a bookshelf :P
		return false;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
		drops.add(TextureBlockUtil.getBlockItemStack(world, pos, state));
	}

	@Override
	public float getEnchantPowerBonus(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileBookshelf) {
			return ((TileBookshelf) te).getEnchantPower();
		}
		return 0;
	}

	public static enum BookshelfType implements IStringSerializable {
		NORMAL,
		RAINBOW,
		TOMES,
		ANCIENT;

		private int meta;
		BookshelfType() {
			this.meta = ordinal();
		}

		@Override
		public String getName() {
			return this.name().toLowerCase(Locale.US);
		}

		public int getMeta() {
			return meta;
		}

		public static BookshelfType fromMeta(int meta) {
			if(meta < 0 || meta >= values().length) {
				meta = 0;
			}

			return values()[meta];
		}
	}
}
