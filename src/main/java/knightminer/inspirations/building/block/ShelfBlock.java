package knightminer.inspirations.building.block;

import com.google.common.collect.ImmutableMap;
import knightminer.inspirations.building.tileentity.ShelfTileEntity;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import slimeknights.mantle.block.RetexturedBlock;

import javax.annotation.Nullable;

public class ShelfBlock extends InventoryBlock implements IHidable {

  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

  public ShelfBlock(Properties properties) {
    super(properties);
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
    return new ShelfTileEntity();
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    super.onBlockPlacedBy(world, pos, state, placer, stack);
    RetexturedBlock.updateTextureBlock(world, pos, stack);
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
    Vector3d hitWorld = trace.getHitVec();
    Vector3d click = new Vector3d(hitWorld.x - pos.getX(), hitWorld.y - pos.getY(), hitWorld.z - pos.getZ());
    if (!isBookClicked(facing, click)) {
      return (world.isRemote || openGui(player, world, pos)) ? ActionResultType.SUCCESS : ActionResultType.PASS;
    }
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof ShelfTileEntity) {
      // try interacting
      if (((ShelfTileEntity)te).interact(player, hand, click)) {
        return ActionResultType.SUCCESS;
      }
      // if we failed to place an item on the shelf, pass if the offhand might try
      if (hand != Hand.OFF_HAND && !player.getHeldItemOffhand().isEmpty()) {
        return ActionResultType.CONSUME;
      }
    }
    return ActionResultType.CONSUME;
  }

  /**
   * Checks if a book was clicked
   * @param facing  Shelf facing
   * @param click   Block relative click position
   * @return  True if a book was clicked, false if the UI should open instead
   */
  private static boolean isBookClicked(Direction facing, Vector3d click) {
   // if we did not click between the shelves, ignore
    if (click.y < 0.0625 || click.y > 0.9375) {
      return false;
    }
    // if we clicked below the top shelf but not quite in the bottom shelf, no book
    if (click.y > 0.4375 && click.y < 0.5625) {
      return false;
    }
    int offX = facing.getXOffset();
    int offZ = facing.getZOffset();
    double x1 = offX == -1 ? 0.625 : 0;
    double z1 = offZ == -1 ? 0.625 : 0;
    double x2 = offX == +1 ? 0.375 : 1;
    double z2 = offZ == +1 ? 0.375 : 1;
    // ensure we clicked within a shelf, not outside one
    return !(click.x < x1) && !(click.x > x2) && !(click.z < z1) && !(click.z > z2);
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
      Vector3d min = new Vector3d(-0.5, 0, -7 / 16.0).rotateYaw(-(float)Math.PI / 2F * side.getHorizontalIndex());
      Vector3d max = new Vector3d( 0.5, 1, 0).rotateYaw(-(float)Math.PI / 2F * side.getHorizontalIndex());

      // Then assemble.
      builder.put(side, VoxelShapes.combineAndSimplify(
          VoxelShapes.create(x1, 0, z1, x2, 1, z2), // Full half slab
          VoxelShapes.or( // Then the two shelves.
                          VoxelShapes.create(0.5 + min.x, 1 / 16.0, 0.5 + min.z, 0.5 + max.x, 7 / 16.0, 0.5 + max.z),
                          VoxelShapes.create(0.5 + min.x, 9 / 16.0, 0.5 + min.z, 0.5 + max.x, 15 / 16.0, 0.5 + max.z)
                        ), IBooleanFunction.ONLY_FIRST));
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

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public boolean hasComparatorInputOverride(BlockState state) {
    return true;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof ShelfTileEntity) {
      return ((ShelfTileEntity)te).getComparatorPower();
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
    return RetexturedBlock.getPickBlock(world, pos, state);
  }

  @Override
  public float getEnchantPowerBonus(BlockState state, IWorldReader world, BlockPos pos) {
    if (!Config.bookshelvesBoostEnchanting.get()) {
      return 0;
    }
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof ShelfTileEntity) {
      return ((ShelfTileEntity)te).getEnchantPower();
    }
    return 0;
  }
}
