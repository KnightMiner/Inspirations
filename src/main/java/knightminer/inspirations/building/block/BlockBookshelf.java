package knightminer.inspirations.building.block;

import com.google.common.collect.ImmutableMap;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.tileentity.TileBookshelf;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ITileEntityProvider;
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
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
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
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.fml.network.NetworkHooks;
import slimeknights.mantle.block.InventoryBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockBookshelf extends InventoryBlock implements ITileEntityProvider, IHidable {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final ModelProperty<String> TEXTURE = TextureBlockUtil.TEXTURE_PROP;
	public static final ModelProperty<Integer> BOOKS = new ModelProperty<>();

	public BlockBookshelf() {
		super(Block.Properties.create(Material.WOOD)
				.hardnessAndResistance(2.0F, 5.0F)
				.sound(SoundType.WOOD)
		);
		this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileBookshelf();
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		TextureBlockUtil.placeTextureBlock(world, pos, stack);
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(@Nonnull IBlockReader world) {
		return new TileBookshelf();
	}

	@Override
	protected boolean openGui(PlayerEntity player, World world, BlockPos pos) {
		if(!(player instanceof ServerPlayerEntity)) {
			throw new AssertionError("Needs to be server!");
		}
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileBookshelf) {
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

		// skip sides, we don't need them
		if(facing != trace.getFace()) {
			return false;
		}

		// if sneaking, just do the GUI
		if(player.isSneaking()) {
			return world.isRemote || openGui(player, world, pos);
		}

		// if we did not click a book, just do the GUI as well
		int book = bookClicked(facing, trace.getHitVec());
		if(book == -1) {
			return world.isRemote || openGui(player, world, pos);
		}

		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileBookshelf) {
			// try interacting
			if(((TileBookshelf) te).interact(player, hand, book)) {
				return true;
			}

			// if the offhand can interact, return false so we can process it later
			if(InspirationsRegistry.isBook(player.getHeldItemOffhand())) {
				return false;
			}
		}

		return true;
	}

	private static int bookClicked(Direction facing, Vec3d click) {
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
		return shelf + Math.min((int) (clicked * 8), 7);
	}

	/*
	 * Bounds
	 */
	private static final ImmutableMap<Direction, VoxelShape> BOUNDS;

	static {
		// shelf bounds
		ImmutableMap.Builder<Direction, VoxelShape> builder = ImmutableMap.builder();
		for(Direction side : Direction.Plane.HORIZONTAL) {
			// Construct the shelf by constructing a half slab, then cutting out the two shelves.

			// Exterior slab shape. For each direction, do 0.1 if the side is pointing that way.
			int offX = side.getXOffset();
			int offZ = side.getZOffset();
			double x1 = offX == -1 ? 0.5 : 0;
			double z1 = offZ == -1 ? 0.5 : 0;
			double x2 = offX == 1 ? 0.5 : 1;
			double z2 = offZ == 1 ? 0.5 : 1;

			// Rotate the 2 X-Z points correctly for the inset shelves.
			Vec3d min = new Vec3d(-7 / 16.0, 0, -7 / 16.0).rotateYaw(-(float) Math.PI / 2F * side.getHorizontalIndex());
			Vec3d max = new Vec3d(7 / 16.0, 1, 0).rotateYaw(-(float) Math.PI / 2F * side.getHorizontalIndex());

			// Then assemble.
			builder.put(side, VoxelShapes.combineAndSimplify(
					VoxelShapes.create(x1, 0, z1, x2, 1, z2), // Full half slab
					VoxelShapes.or( // Then the two shelves.
							VoxelShapes.create(0.5 + min.x, 1 / 16.0, 0.5 + min.z, 0.5 + max.x, 7 / 16.0, 0.5 + max.z),
							VoxelShapes.create(0.5 + min.x, 9 / 16.0, 0.5 + min.z, 0.5 + max.x, 15 / 16.0, 0.5 + max.z)
					),
					IBooleanFunction.ONLY_FIRST
			));
		}
		BOUNDS = builder.build();
	}

	@Nonnull
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return BOUNDS.get(state.get(FACING));
	}

	/*
	 * Redstone
	 */

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if(state.getBlock() != newState.getBlock()) {
			// if powered, send updates for power
			if(getPower(world, pos) > 0) {
				world.notifyNeighborsOfStateChange(pos, this);
				world.notifyNeighborsOfStateChange(pos.offset(state.get(FACING).getOpposite()), this);
			}
			TileEntity tileentity = world.getTileEntity(pos);
			if(tileentity instanceof IInventory) {
				InventoryHelper.dropInventoryItems(world, pos, (IInventory) tileentity);
			}
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}

	@Override
	public int getWeakPower(BlockState state, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return getPower(blockAccess, pos);
	}

	@Override
	public int getStrongPower(BlockState state, IBlockReader blockAccess, BlockPos pos, Direction side) {
		if(state.get(FACING) != side) {
			return 0;
		}

		return getPower(blockAccess, pos);

	}

	private int getPower(IBlockReader blockAccess, BlockPos pos) {
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
	public boolean canProvidePower(BlockState state) {
		// ensure we have the redstone book, since it comes from the redstone module
		return InspirationsBuilding.redstoneBook != null;
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

	@Nonnull
	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.with(FACING, mirror.mirror(state.get(FACING)));
	}

	/* Drops */

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		return TextureBlockUtil.getBlockItemStack(world, pos, state);
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

	@Nonnull
	@Override
	public List<ItemStack> getDrops(@Nonnull BlockState state, LootContext.Builder builder) {
		List<ItemStack> drops = new ArrayList<>(1);
		drops.add(TextureBlockUtil.getBlockItemStack(builder.getWorld(), builder.get(LootParameters.POSITION), state));
		return drops;
	}

	@Override
	public float getEnchantPowerBonus(BlockState state, IWorldReader world, BlockPos pos) {
		if(!Config.bookshelvesBoostEnchanting.get()) {
			return 0;
		}
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileBookshelf) {
			return ((TileBookshelf) te).getEnchantPower();
		}
		return 0;
	}
}
