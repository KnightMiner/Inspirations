package knightminer.inspirations.building.block;

import com.google.common.collect.ImmutableMap;
import knightminer.inspirations.building.block.entity.ShelfBlockEntity;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import slimeknights.mantle.block.InventoryBlock;
import slimeknights.mantle.block.RetexturedBlock;

import javax.annotation.Nullable;
import java.util.Map;

public class ShelfBlock extends InventoryBlock implements IHidable {
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

  public ShelfBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
    builder.add(FACING);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new ShelfBlockEntity(pos, state);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
  }

  @Override
  public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    super.setPlacedBy(world, pos, state, placer, stack);
    RetexturedBlock.updateTextureBlock(world, pos, stack);
  }


  /* Enable/Disabling */

  @Override
  public boolean isEnabled() {
    return Config.enableBookshelf.getAsBoolean();
  }

  @Override
  public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
    if (shouldAddtoItemGroup(group)) {
      super.fillItemCategory(group, items);
    }
  }


  /* Activation */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
    Direction facing = state.getValue(FACING);

    // skip opposite, not needed as the back is never clicked for books
    if (facing.getOpposite() == trace.getDirection()) {
      return InteractionResult.PASS;
    }

    // if sneaking, just do the GUI
    if (player.isCrouching()) {
      return (world.isClientSide || openGui(player, world, pos)) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    // if we did not click a book, just do the GUI as well
    Vec3 hitWorld = trace.getLocation();
    Vec3 click = new Vec3(hitWorld.x - pos.getX(), hitWorld.y - pos.getY(), hitWorld.z - pos.getZ());
    if (!isBookClicked(facing, click)) {
      return (world.isClientSide || openGui(player, world, pos)) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
    BlockEntity te = world.getBlockEntity(pos);
    if (te instanceof ShelfBlockEntity) {
      // try interacting
      if (((ShelfBlockEntity)te).interact(player, hand, click)) {
        return InteractionResult.SUCCESS;
      }
      // if we failed to place an item on the shelf, pass if the offhand might try
      if (hand != InteractionHand.OFF_HAND && !player.getOffhandItem().isEmpty()) {
        return InteractionResult.CONSUME;
      }
    }
    return InteractionResult.CONSUME;
  }

  /**
   * Checks if a book was clicked
   * @param facing  Shelf facing
   * @param click   Block relative click position
   * @return  True if a book was clicked, false if the UI should open instead
   */
  private static boolean isBookClicked(Direction facing, Vec3 click) {
   // if we did not click between the shelves, ignore
    if (click.y < 0.0625 || click.y > 0.9375) {
      return false;
    }
    // if we clicked below the top shelf but not quite in the bottom shelf, no book
    if (click.y > 0.4375 && click.y < 0.5625) {
      return false;
    }
    int offX = facing.getStepX();
    int offZ = facing.getStepZ();
    double x1 = offX == -1 ? 0.625 : 0;
    double z1 = offZ == -1 ? 0.625 : 0;
    double x2 = offX ==  1 ? 0.375 : 1;
    double z2 = offZ ==  1 ? 0.375 : 1;
    // ensure we clicked within a shelf, not outside one
    return !(click.x < x1) && !(click.x > x2) && !(click.z < z1) && !(click.z > z2);
  }

  /*
   * Bounds
   */
  private static final Map<Direction,VoxelShape> BOUNDS;

  static {
    // shelf bounds
    ImmutableMap.Builder<Direction,VoxelShape> builder = ImmutableMap.builder();
    for (Direction side : Direction.Plane.HORIZONTAL) {
      // Construct the shelf by constructing a half slab, then cutting out the two shelves.

      // Exterior slab shape. For each direction, do 0.1 if the side is pointing that way.
      int offX = side.getStepX();
      int offZ = side.getStepZ();
      double x1 = offX == -1 ? 0.5 : 0;
      double z1 = offZ == -1 ? 0.5 : 0;
      double x2 = offX == 1 ? 0.5 : 1;
      double z2 = offZ == 1 ? 0.5 : 1;

      // Rotate the 2 X-Z points correctly for the inset shelves.
      Vec3 first = new Vec3(-0.5, 0, -7 / 16.0).yRot(-(float)Math.PI / 2F * side.get2DDataValue());
      Vec3 second = new Vec3( 0.5, 1, 0).yRot(-(float)Math.PI / 2F * side.get2DDataValue());

      // Then assemble.
      double minX = Math.min(first.x, second.x);
      double minZ = Math.min(first.z, second.z);
      double maxX = Math.max(first.x, second.x);
      double maxZ = Math.max(first.z, second.z);
      builder.put(side, Shapes.join(
          Shapes.box(x1, 0, z1, x2, 1, z2), // Full half slab
          Shapes.or( // Then the two shelves.
                          Shapes.box(0.5 + minX, 1 / 16.0, 0.5 + minZ, 0.5 + maxX,  7 / 16.0, 0.5 + maxZ),
                          Shapes.box(0.5 + minX, 9 / 16.0, 0.5 + minZ, 0.5 + maxX, 15 / 16.0, 0.5 + maxZ)
                        ), BooleanOp.ONLY_FIRST));
    }
    BOUNDS = builder.build();
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return BOUNDS.get(state.getValue(FACING));
  }

  /*
   * Comparators
   */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public boolean hasAnalogOutputSignal(BlockState state) {
    return true;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
    BlockEntity te = world.getBlockEntity(pos);
    if (te instanceof ShelfBlockEntity) {
      return ((ShelfBlockEntity)te).getComparatorPower();
    }
    return 0;
  }


  /*
   * Block properties
   */
  @Override
  public BlockState rotate(BlockState state, LevelAccessor world, BlockPos pos, Rotation direction) {
    return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public BlockState mirror(BlockState state, Mirror mirror) {
    return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
  }

  /* Drops */

  @Override
  public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
    return RetexturedBlock.getPickBlock(level, pos, state);
  }

  @Override
  public float getEnchantPowerBonus(BlockState state, LevelReader world, BlockPos pos) {
    if (!Config.bookshelvesBoostEnchanting.getAsBoolean()) {
      return 0;
    }
    BlockEntity te = world.getBlockEntity(pos);
    if (te instanceof ShelfBlockEntity) {
      return ((ShelfBlockEntity)te).getEnchantPower();
    }
    return 0;
  }
}
