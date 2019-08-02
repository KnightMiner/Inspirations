package knightminer.inspirations.building.block;

import com.google.common.collect.ImmutableMap;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.tileentity.TileBookshelf;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
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
import slimeknights.mantle.block.InventoryBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockBookshelf extends InventoryBlock implements ITileEntityProvider {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final ModelProperty<String> TEXTURE = TextureBlockUtil.TEXTURE_PROP;
	public static final ModelProperty<Integer> BOOKS = new ModelProperty<>();

	public BlockBookshelf() {
		super(Block.Properties.create(Material.WOOD)
			.hardnessAndResistance(2.0F, 5.0F)
			.sound(SoundType.WOOD)
		);
		this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH));
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
		return getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
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
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileBookshelf) {
			player.openContainer((TileBookshelf)te);
			return true;
		}
		return false;
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
		double x2 = offX ==  1 ? 0.375 : 0.9375;
		double z2 = offZ ==  1 ? 0.375 : 0.9375;
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
		return shelf + Math.min((int)(clicked * 8), 7);
	}

	/*
	 * Bounds
	 */
	private static final ImmutableMap<Direction, VoxelShape> BOUNDS;

	static {
		// shelf bounds
		ImmutableMap.Builder<Direction, VoxelShape> builder = ImmutableMap.builder();
		for(Direction side : Direction.Plane.HORIZONTAL) {
			int offX = side.getXOffset();
			int offZ = side.getZOffset();
			double x1 = offX == -1 ? 0.5 : 0;
			double z1 = offZ == -1 ? 0.5 : 0;
			double x2 = offX ==  1 ? 0.5 : 1;
			double z2 = offZ ==  1 ? 0.5 : 1;

			builder.put(side, VoxelShapes.or(
					VoxelShapes.create(x1,  0,      z1,  x2,  0.0625, z2), // bottom shelf
					VoxelShapes.create(x1,  0.4375, z1,  x2,  0.5625, z2), // middle shelf
					VoxelShapes.create(x1,  0.9375, z1,  x2,  1,      z2), // top shelf

					VoxelShapes.create(offX == -1 ? 0.625 : 0, 0, offZ == -1 ? 0.625 : 0, offX ==  1 ? 0.375 : 1, 1, offZ ==  1 ? 0.375 : 1), // back wall
					VoxelShapes.create(x1, 0, z1, offX == 0 ? 0.0625 : x2, 1, offZ == 0 ? 0.0625 : z2), // side wall 1
					VoxelShapes.create(offX == 0 ? 0.9375 : x1, 0, offZ == 0 ? 0.9375 : z1, x2, 1, z2) // side wall 2
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
		// if powered, send updates for power
		if (!isMoving  && state.getBlock() != newState.getBlock() && getPower(world, pos) > 0) {
			world.notifyNeighborsOfStateChange(pos, this);
			world.notifyNeighborsOfStateChange(pos.offset(state.get(FACING).getOpposite()), this);
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}

	@Override
	public int getWeakPower(BlockState state, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return getPower(blockAccess, pos);
	}

	@Override
	public int getStrongPower(BlockState state, IBlockReader blockAccess, BlockPos pos, Direction side) {
		if (state.get(FACING) != side) {
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
    public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        if(group == ItemGroup.SEARCH || Config.enableBookshelf.get()) {
			TextureBlockUtil.addBlocksFromTag(BlockTags.WOODEN_SLABS, this, items);
        }
    }

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
		if (!Config.bookshelvesBoostEnchanting.get()) {
			return 0;
		}
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileBookshelf) {
			return ((TileBookshelf) te).getEnchantPower();
		}
		return 0;
	}
}
