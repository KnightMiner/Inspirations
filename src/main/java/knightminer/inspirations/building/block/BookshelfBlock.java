package knightminer.inspirations.building.block;

import com.google.common.collect.ImmutableMap;
import knightminer.inspirations.building.tileentity.BookshelfTileEntity;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import slimeknights.mantle.block.InventoryBlock;

import javax.annotation.Nullable;

public class BookshelfBlock extends InventoryBlock implements IHidable {

  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

  public BookshelfBlock() {
    super(Block.Properties.create(Material.WOOD)
                          .hardnessAndResistance(2.0F, 5.0F)
                          .sound(SoundType.WOOD)
         );
    this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH));
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block,BlockState> builder) {
    builder.add(FACING);
  }

  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new BookshelfTileEntity();
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    super.onBlockPlacedBy(world, pos, state, placer, stack);
    TextureBlockUtil.updateTextureBlock(world, pos, stack);
  }


  /* Enable/Disabling */

  @Override
  public boolean isEnabled() {
    return Config.enableBookshelf.get();
  }

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    if (shouldAddtoItemGroup(group)) {
      super.fillItemGroup(group, items);
    }
  }


  /* Activation */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
    Direction facing = state.get(FACING);

    // skip opposite, not needed as the back is never clicked for books
    if (facing.getOpposite() == trace.getFace()) {
      return ActionResultType.PASS;
    }

    // if sneaking, just do the GUI
    if (player.isCrouching()) {
      return (world.isRemote || openGui(player, world, pos)) ? ActionResultType.SUCCESS : ActionResultType.PASS;
    }

    // if we did not click a book, just do the GUI as well
    int book = bookClicked(facing, pos, trace.getHitVec());
    if (book == -1) {
      return (world.isRemote || openGui(player, world, pos)) ? ActionResultType.SUCCESS : ActionResultType.PASS;
    }

    TileEntity te = world.getTileEntity(pos);
    if (te instanceof BookshelfTileEntity) {
      // try interacting
      if (((BookshelfTileEntity)te).interact(player, hand, book)) {
        return ActionResultType.SUCCESS;
      }

      // if the offhand can interact, return false so we can process it later
      if (InspirationsRegistry.isBook(player.getHeldItemOffhand())) {
        return ActionResultType.PASS;
      }
    }

    return ActionResultType.SUCCESS;
  }

  /**
   * Gets the book that was clicked
   * @param facing     Direction of the bookshelf
   * @param pos        Block position
   * @param clickWorld World relative click position
   * @return Index of clicked book, or -1 if no book clicked
   */
  private static int bookClicked(Direction facing, BlockPos pos, Vector3d clickWorld) {
    Vector3d click = new Vector3d(clickWorld.x - pos.getX(), clickWorld.y - pos.getY(), clickWorld.z - pos.getZ());
    // if we did not click between the shelves, ignore
    if (click.y < 0.0625 || click.y > 0.9375) {
      return -1;
    }
    int shelf = 0;
    // if we clicked below the middle shelf, add 7 to the book
    if (click.y <= 0.4375) {
      shelf = 7;
      // if we clicked below the top shelf but not quite in the middle shelf, no book
    } else if (click.y < 0.5625) {
      return -1;
    }

    int offX = facing.getXOffset();
    int offZ = facing.getZOffset();
    double x1 = offX == -1 ? 0.625 : 0.0625;
    double z1 = offZ == -1 ? 0.625 : 0.0625;
    double x2 = offX == +1 ? 0.375 : 0.9375;
    double z2 = offZ == +1 ? 0.375 : 0.9375;
    // ensure we clicked within a shelf, not outside one
    if (click.x < x1 || click.x > x2 || click.z < z1 || click.z > z2) {
      return -1;
    }

    // okay, so now we know we clicked in the book area, so just take the position clicked to determine where
    Direction dir = facing.rotateYCCW();
    // subtract one pixel and multiply by our direction
    double clicked = (dir.getXOffset() * click.x) + (dir.getZOffset() * click.z) - 0.0625;
    // if negative, just add one to wrap back around
    if (clicked < 0) {
      clicked = 1 + clicked;
    }

    // multiply by 8 to account for extra 2 pixels
    return shelf + Math.min((int)(clicked * 8), 6);
  }

  /*
   * Bounds
   */
  private static final ImmutableMap<Direction,VoxelShape> BOUNDS;

  static {
    // shelf bounds
    ImmutableMap.Builder<Direction,VoxelShape> builder = ImmutableMap.builder();
    for (Direction side : Direction.Plane.HORIZONTAL) {
      // Construct the shelf by constructing a half slab, then cutting out the two shelves.

      // Exterior slab shape. For each direction, do 0.1 if the side is pointing that way.
      int offX = side.getXOffset();
      int offZ = side.getZOffset();
      double x1 = offX == -1 ? 0.5 : 0;
      double z1 = offZ == -1 ? 0.5 : 0;
      double x2 = offX == 1 ? 0.5 : 1;
      double z2 = offZ == 1 ? 0.5 : 1;

      // Rotate the 2 X-Z points correctly for the inset shelves.
      Vector3d min = new Vector3d(-7 / 16.0, 0, -7 / 16.0).rotateYaw(-(float)Math.PI / 2F * side.getHorizontalIndex());
      Vector3d max = new Vector3d(7 / 16.0, 1, 0).rotateYaw(-(float)Math.PI / 2F * side.getHorizontalIndex());

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

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return BOUNDS.get(state.get(FACING));
  }

  /*
   * Comparators
   */

  @Override
  public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
    if (state.getBlock() != newState.getBlock()) {
      TileEntity tileentity = world.getTileEntity(pos);
      if (tileentity instanceof IInventory) {
        InventoryHelper.dropInventoryItems(world, pos, (IInventory)tileentity);
      }
    }
    super.onReplaced(state, world, pos, newState, isMoving);
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public boolean hasComparatorInputOverride(BlockState p_149740_1_) {
    return true;
  }

  @SuppressWarnings("deprecation")
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
  @Override
  public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation direction) {
    return state.with(FACING, direction.rotate(state.get(FACING)));
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public BlockState mirror(BlockState state, Mirror mirror) {
    return state.with(FACING, mirror.mirror(state.get(FACING)));
  }

  /* Drops */

  @Override
  public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
    return TextureBlockUtil.getPickBlock(world, pos, state);
  }

  //	@Override
  //	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
  //		// we pull up a few calls to this point in time because we still have the TE here
  //		// the execution otherwise is equivalent to vanilla order
  //		this.onBlockHarvested(world, pos, state, player);
  //		if(willHarvest) {
  //			this.harvestBlock(world, player, pos, state, world.getTileEntity(pos), player.getHeldItemMainhand());
  //		}
  //
  //		world.setBlockState(pos, Blocks.AIR.getDefaultState());
  //		// return false to prevent the above called functions to be called again
  //		// side effect of this is that no xp will be dropped. but it shoudln't anyway from a bookshelf :P
  //		return false;
  //	}

  @Override
  public float getEnchantPowerBonus(BlockState state, IWorldReader world, BlockPos pos) {
    if (!Config.bookshelvesBoostEnchanting.get()) {
      return 0;
    }
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof BookshelfTileEntity) {
      return ((BookshelfTileEntity)te).getEnchantPower();
    }
    return 0;
  }
}
