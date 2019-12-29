package knightminer.inspirations.building.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import knightminer.inspirations.building.tileentity.BookshelfTileEntity;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import slimeknights.mantle.block.InventoryBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BookshelfBlock extends InventoryBlock implements IHidable {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<Offset> POSITION = EnumProperty.create("pos", Offset.class);

	public BookshelfBlock() {
		super(Block.Properties.create(Material.WOOD)
				.hardnessAndResistance(2.0F, 5.0F)
				.sound(SoundType.WOOD)
		);
		this.setDefaultState(this.getStateContainer().getBaseState()
				.with(FACING, Direction.NORTH)
				.with(POSITION, Offset.FRONT)
		);
	}

	/**
	 * Return the number of books this shelf can hold.
	 * @param state The relevant blockstate.
	 * @return The book count.
	 */
	public static int getBookCount(@Nullable BlockState state) {
		if (state != null && state.get(BookshelfBlock.POSITION) == BookshelfBlock.Offset.BOTH) {
			return 28;
		} else {
			return 14;
		}
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, POSITION);
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new BookshelfTileEntity();
	}

	@Deprecated
	@Nullable
	@Override
	public TileEntity createNewTileEntity(@Nonnull IBlockReader world) {
		return new BookshelfTileEntity();
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		// Offset from the center of the block.
		Vec3d offset = context.getHitVec().subtract(new Vec3d(context.getPos()).add(0.5, 0.5, 0.5));

		Direction direction = context.getPlacementHorizontalFacing().getOpposite();
		// Compute which half of the block the player clicked on.
		Offset pos = (offset.dotProduct(new Vec3d(direction.getDirectionVec())) > 0) ? Offset.FRONT: Offset.BACK;

		return getDefaultState().with(FACING, direction).with(POSITION, pos);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		TextureBlockUtil.updateTextureBlock(world, pos, stack);
	}

	@Override
	protected boolean openGui(PlayerEntity player, World world, BlockPos pos) {
		if(!(player instanceof ServerPlayerEntity)) {
			throw new AssertionError("Needs to be server!");
		}
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof BookshelfTileEntity) {
			NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) te, pos);
			return true;
		}
		return false;
	}

	/* Enable/Disabling */

	@Override
	public boolean isEnabled() {
		return Config.enableBookshelf.get();
	}

	@Override
	public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
		if(shouldAddtoItemGroup(group)) {
			super.fillItemGroup(group, items);
		}
	}


	/* Activation */

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
		Direction facing = state.get(FACING);

		// skip opposite, not needed as the back is never clicked for books
		if(facing.getOpposite() == trace.getFace()) {
			if (state.get(POSITION) != Offset.BOTH && player.getHeldItemMainhand().getItem() == asItem()) {
				if (!world.isRemote) {
					player.getHeldItemMainhand().setCount(player.getHeldItemMainhand().getCount() - 1);
					return world.setBlockState(pos, state.with(POSITION, Offset.BOTH));
				}
				return true;
			}
			return false;
		}

		// if sneaking, just do the GUI
		if(player.isSneaking()) {
			return world.isRemote || openGui(player, world, pos);
		}

		// if we did not click a book, just do the GUI as well
		int book = bookClicked(facing, pos, trace.getHitVec());
		if(book == -1) {
			return world.isRemote || openGui(player, world, pos);
		}

		TileEntity te = world.getTileEntity(pos);
		if(te instanceof BookshelfTileEntity) {
			// try interacting
			if(((BookshelfTileEntity) te).interact(player, hand, book)) {
				return true;
			}

			// if the offhand can interact, return false so we can process it later
			if(InspirationsRegistry.isBook(player.getHeldItemOffhand())) {
				return false;
			}
		}

		return true;
	}

	private static int bookClicked(Direction facing, BlockPos pos, Vec3d clickWorld) {
		Vec3d click = new Vec3d(clickWorld.x - pos.getX(), clickWorld.y - pos.getY(), clickWorld.z - pos.getZ());
		// if we did not click between the shelves, ignore
		if(click.y < 0.0625 || click.y > 0.9375) {
			return -1;
		}
		int shelf = 0;
		// if we clicked below the middle shelf, add 7 to the book
		if(click.y <= 0.4375) {
			shelf = 7;
			// if we clicked below the top shelf but not quite in the middle shelf, no book
		} else if(click.y < 0.5625) {
			return -1;
		}

		int offX = facing.getXOffset();
		int offZ = facing.getZOffset();
		double x1 = offX == -1 ? 0.625 : 0.0625;
		double z1 = offZ == -1 ? 0.625 : 0.0625;
		double x2 = offX == +1 ? 0.375 : 0.9375;
		double z2 = offZ == +1 ? 0.375 : 0.9375;
		// ensure we clicked within a shelf, not outside one
		if(click.x < x1 || click.x > x2 || click.z < z1 || click.z > z2) {
			return -1;
		}

		// okay, so now we know we clicked in the book area, so just take the position clicked to determine where
		Direction dir = facing.rotateYCCW();
		// subtract one pixel and multiply by our direction
		double clicked = (dir.getXOffset() * click.x) + (dir.getZOffset() * click.z) - 0.0625;
		// if negative, just add one to wrap back around
		if(clicked < 0) {
			clicked = 1 + clicked;
		}

		// multiply by 8 to account for extra 2 pixels
		return shelf + Math.min((int) (clicked * 8), 6);
	}

	/*
	 * Bounds
	 */
	private static final ImmutableMap<Pair<Direction, Offset>, VoxelShape> BOUNDS;

	/**
	 * Compute a voxelshape, rotated by the provided direction.
	 */
	private static VoxelShape makeRotatedShape(Direction side, int x1, int y1, int z1, int x2, int y2, int z2) {
		float yaw = -(float) Math.PI / 2F * side.getHorizontalIndex();
		Vec3d min = new Vec3d(x1 - 8, y1 - 8, z1 - 8).rotateYaw(yaw);
		Vec3d max = new Vec3d(x2 - 8, y2 - 8, z2 - 8).rotateYaw(yaw);
		return VoxelShapes.create(
				0.5 + min.x / 16.0, 0.5 + min.y / 16.0, 0.5 + min.z / 16.0,
				0.5 + max.x / 16.0, 0.5 + max.y / 16.0, 0.5 + max.z / 16.0
		);
	}

	static {
		// shelf bounds
		ImmutableMap.Builder<Pair<Direction, Offset>, VoxelShape> builder = ImmutableMap.builder();
		for(Direction side : Direction.Plane.HORIZONTAL) {
			// Construct the shelf by constructing the full block, then cutting out the shelves.
			// +Z is forward.
			builder.put(Pair.of(side, Offset.BACK), VoxelShapes.combineAndSimplify(
					// Slab
					makeRotatedShape(side,0, 0, 0, 16, 16, 8),
					VoxelShapes.or( // Bottom, top.
						makeRotatedShape(side,1,1, 1, 15, 7, 8),
						makeRotatedShape(side,1,9, 1, 15, 15, 8)
					),
					IBooleanFunction.ONLY_FIRST
			));
			builder.put(Pair.of(side, Offset.FRONT), VoxelShapes.combineAndSimplify(
					// Slab
					makeRotatedShape(side,0, 0, 8, 16, 16, 16),
					VoxelShapes.or( // Bottom, top.
						makeRotatedShape(side,1,1, 9, 15, 7, 16),
						makeRotatedShape(side,1,9, 9, 15, 15, 16)
					),
					IBooleanFunction.ONLY_FIRST
			));
			builder.put(Pair.of(side, Offset.BOTH), VoxelShapes.combineAndSimplify(
					VoxelShapes.fullCube(),
					VoxelShapes.or( // Bottom back, top back, bottom front, top front
						makeRotatedShape(side,1,1, 0, 15, 7, 7),
						makeRotatedShape(side,1,9, 0, 15, 15, 7),
						makeRotatedShape(side,1,1, 9, 15, 7, 16),
						makeRotatedShape(side,1,9, 9, 15, 15, 16)
					),
					IBooleanFunction.ONLY_FIRST
			));
		}
		BOUNDS = builder.build();
	}

	@Deprecated
	@Nonnull
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return BOUNDS.get(Pair.of(state.get(FACING), state.get(POSITION)));
	}

	/*
	 * Comparators
	 */

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if(state.getBlock() != newState.getBlock()) {
			TileEntity tileentity = world.getTileEntity(pos);
			if(tileentity instanceof IInventory) {
				InventoryHelper.dropInventoryItems(world, pos, (IInventory) tileentity);
			}
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}

	@Deprecated
	@Override
	public boolean hasComparatorInputOverride(BlockState p_149740_1_) {
		return true;
	}

	@Deprecated
	@Override
	public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof BookshelfTileEntity) {
			return ((BookshelfTileEntity)te).getComparatorPower();
		}
		return 0;
	}


	/*
	 * Block properties
	 */

	@Nonnull
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation direction) {
		return state.with(FACING, direction.rotate(state.get(FACING)));
	}

	@Deprecated
	@Nonnull
	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.with(FACING, mirror.mirror(state.get(FACING)));
	}

	/* Drops */

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		return TextureBlockUtil.getPickBlock(world, pos, state);
	}

	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
		// we pull up a few calls to this point in time because we still have the TE here
		// the execution otherwise is equivalent to vanilla order
		this.onBlockHarvested(world, pos, state, player);
		if(willHarvest) {
			this.harvestBlock(world, player, pos, state, world.getTileEntity(pos), player.getHeldItemMainhand());
		}

		world.setBlockState(pos, Blocks.AIR.getDefaultState());
		// return false to prevent the above called functions to be called again
		// side effect of this is that no xp will be dropped. but it shoudln't anyway from a bookshelf :P
		return false;
	}

	@Override
	public float getEnchantPowerBonus(BlockState state, IWorldReader world, BlockPos pos) {
		if(!Config.bookshelvesBoostEnchanting.get()) {
			return 0;
		}
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof BookshelfTileEntity) {
			return ((BookshelfTileEntity) te).getEnchantPower();
		}
		return 0;
	}

	public enum Offset implements IStringSerializable {
		BACK("back"),
		FRONT("front"),
		BOTH("both");

		private final String name;

		Offset(String name) {
			this.name = name;
		}

		public String toString() {
			return this.getName();
		}

		@Nonnull
		@Override
		public String getName() {
			return name;
		}
	}
}
