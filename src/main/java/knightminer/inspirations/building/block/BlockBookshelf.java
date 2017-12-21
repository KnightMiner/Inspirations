package knightminer.inspirations.building.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import com.google.common.collect.ImmutableMap;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.tileentity.TileBookshelf;
import knightminer.inspirations.library.util.RecipeUtil;
import knightminer.inspirations.library.util.TagUtil;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.block.BlockInventory;
import slimeknights.mantle.property.PropertyString;

@SuppressWarnings("unchecked")
public class BlockBookshelf extends BlockInventory implements ITileEntityProvider {

	public static final PropertyEnum<BookshelfType> TYPE = PropertyEnum.create("type", BookshelfType.class);
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final PropertyString TEXTURE = new PropertyString("texture");
	public static final IUnlistedProperty<?>[] PROPS;
	public static final IUnlistedProperty<Boolean>[] BOOKS;
	static {
		PROPS = new IUnlistedProperty<?>[15];
		BOOKS = (IUnlistedProperty<Boolean>[])new IUnlistedProperty<?>[14];
		for(int i = 0; i < 14; i++) {
			PROPS[i] = BOOKS[i] = Properties.toUnlisted(PropertyBool.create("book" + i));
		}
		PROPS[14] = TEXTURE;
	}

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
		return new ExtendedBlockState(this, new IProperty<?>[]{TYPE, FACING}, PROPS);
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
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);

		NBTTagCompound tag = TagUtil.getTagSafe(stack);
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileBookshelf) {
			TileBookshelf table = (TileBookshelf) te;
			NBTTagCompound textureTag = tag.getCompoundTag(RecipeUtil.TAG_TEXTURE);
			if(textureTag == null) {
				textureTag = new NBTTagCompound();
			}

			table.updateTextureBlock(textureTag);
		}
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

		// just skip logic for non-front, we only interact with the front
		if(facing != side) {
			return false;
		}

		// if sneaking, open gui
		if(player.isSneaking() && !world.isRemote) {
			return openGui(player, world, pos);
		}

		if(world.isRemote) {
			return true;
		}

		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileBookshelf) {
			return ((TileBookshelf) te).interact(player, hand, facing, clickX, clickY, clickZ);
		}

		return false;
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
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for(BookshelfType type : BookshelfType.values()) {
			RecipeUtil.addBlocksFromOredict("slabWood", this, type.getMeta(), list);
		}
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
