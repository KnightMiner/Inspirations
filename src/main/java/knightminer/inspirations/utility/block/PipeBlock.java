package knightminer.inspirations.utility.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import knightminer.inspirations.utility.InspirationsUtility;
import knightminer.inspirations.utility.tileentity.PipeTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import slimeknights.mantle.block.InventoryBlock;
import slimeknights.mantle.util.BlockEntityHelper;

import javax.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public class PipeBlock extends InventoryBlock implements IHidable, SimpleWaterloggedBlock {
  // Facing is the direction we output to.
  public static final DirectionProperty FACING = BlockStateProperties.FACING;
  // These six values specify if another pipe/hopper is in this direction for us
  // to visually connect to.
  private static final BooleanProperty NORTH = BlockStateProperties.NORTH;
  private static final BooleanProperty EAST = BlockStateProperties.EAST;
  private static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
  private static final BooleanProperty WEST = BlockStateProperties.WEST;
  private static final BooleanProperty UP = BlockStateProperties.UP;
  private static final BooleanProperty DOWN = BlockStateProperties.DOWN;
  // If this is set, there is a hopper on our output side which isn't facing towards us.
  // We then render a longer pipe to connect with the spout model.
  public static final BooleanProperty HOPPER = BooleanProperty.create("hopper");

  private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

  // Direction.getIndex() -> Property. Order is D-U-N-S-W-E
  public static final BooleanProperty[] DIR_ENABLED = new BooleanProperty[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};

  public PipeBlock() {
    super(Block.Properties
              .of(Material.METAL, MaterialColor.STONE)  // Darker than iron blocks.
              .strength(3.0F, 8.0F)
              .sound(SoundType.METAL)
         );
    this.registerDefaultState(this.getStateDefinition().any()
                             .setValue(FACING, Direction.NORTH)
                             .setValue(NORTH, false)
                             .setValue(EAST, false)
                             .setValue(SOUTH, false)
                             .setValue(WEST, false)
                             .setValue(UP, false)
                             .setValue(DOWN, false)
                             .setValue(HOPPER, false)
                             .setValue(WATERLOGGED, false)
                        );
  }

  /* IHidable */

  @Override
  public boolean isEnabled() {
    return Config.enablePipe.getAsBoolean();
  }

  @Override
  public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> stacks) {
    if (shouldAddtoItemGroup(group)) {
      super.fillItemCategory(group, stacks);
    }
  }

  /* Block state settings */

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
    builder.add(WATERLOGGED, FACING, NORTH, EAST, SOUTH, WEST, UP, DOWN, HOPPER);
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public BlockState rotate(BlockState state, Rotation rot) {
    return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public BlockState mirror(BlockState state, Mirror mirror) {
    return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public BlockState updateShape(BlockState state, Direction neighFacing, BlockState neighState, LevelAccessor world, BlockPos pos, BlockPos neighPos) {
    Direction outFacing = state.getValue(FACING);

    // We only need to check the one side that updated.
    state = state.setValue(DIR_ENABLED[neighFacing.get3DDataValue()], canConnectTo(world, pos, outFacing, neighFacing));

    // Check if the output side is a hopper if that side was changed.
    if (outFacing == neighFacing) {
      BlockState offsetState = world.getBlockState(pos.relative(outFacing));
      state = state.setValue(HOPPER,
                         offsetState.getBlock() instanceof HopperBlock &&
                         offsetState.getValue(HopperBlock.FACING) != outFacing.getOpposite()
                        );
    }
    return state;
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    Level world = context.getLevel();
    BlockPos pos = context.getClickedPos();

    Direction facing = context.getClickedFace().getOpposite();
    // only allow up if allowed in the config.
    if (!Config.pipeUpwards.get() && facing == Direction.UP) {
      facing = context.getHorizontalDirection();
    }

    BlockState offsetState = world.getBlockState(pos.relative(facing));
    // When first placed, check every side.
    return this.defaultBlockState()
               .setValue(FACING, facing)
               .setValue(HOPPER, offsetState.getBlock() instanceof HopperBlock && offsetState.getValue(HopperBlock.FACING) != facing.getOpposite())
               .setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER)
               .setValue(UP, canConnectTo(world, pos, facing, Direction.UP))
               .setValue(DOWN, canConnectTo(world, pos, facing, Direction.DOWN))
               .setValue(NORTH, canConnectTo(world, pos, facing, Direction.NORTH))
               .setValue(EAST, canConnectTo(world, pos, facing, Direction.EAST))
               .setValue(SOUTH, canConnectTo(world, pos, facing, Direction.SOUTH))
               .setValue(WEST, canConnectTo(world, pos, facing, Direction.WEST));
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
    // return false if holding a pipe to make easier to place
    Item item = player.getItemInHand(hand).getItem();
    if (item == InspirationsUtility.pipe.asItem() || Block.byItem(item) instanceof HopperBlock) {
      return InteractionResult.PASS;
    }
    return super.use(state, world, pos, player, hand, trace);
  }

  @Override
  public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
    // If destroyed, drop contents.
    if (state.getBlock() != newState.getBlock()) {
      BlockEntity te = world.getBlockEntity(pos);
      if (te instanceof Container) {
        Containers.dropContents(world, pos, (Container)te);
      }
    }
    super.onRemove(state, world, pos, newState, isMoving);
  }

  /* Model and shape */

  private static boolean canConnectTo(LevelAccessor world, BlockPos pos, Direction facing, Direction side) {
    // ignore side pipe is facing
    if (facing == side) return false;

    BlockState state = world.getBlockState(pos.relative(side));
    Block block = state.getBlock();
    Direction opposite = side.getOpposite();
    // if it is a known item output thingy and is facing us, connect
    if ((block instanceof PipeBlock || block instanceof DropperBlock) && state.getValue(FACING) == opposite) return true;
    // hopper check, we can skip on down since hoppers cannot face up
    return side != Direction.DOWN && block instanceof HopperBlock && state.getValue(HopperBlock.FACING) == opposite;
  }


  /* Tile Entity */

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new PipeTileEntity(pos, state);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> matchType) {
    return BlockEntityHelper.serverTicker(level, matchType, InspirationsUtility.tilePipe, PipeTileEntity.SERVER_TICKER);
  }

  @Override
  protected boolean openGui(Player player, Level world, BlockPos pos) {
    if (!(player instanceof ServerPlayer)) {
      throw new AssertionError("Needs to be server!");
    }
    BlockEntity te = world.getBlockEntity(pos);
    if (te instanceof PipeTileEntity) {
      NetworkHooks.openGui((ServerPlayer)player, (MenuProvider)te, pos);
      return true;
    }
    return false;
  }

  @Override
  public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos neighbor, boolean isMoving) {
    if (pos.relative(state.getValue(FACING)).equals(neighbor)) {
      BlockEntity te = world.getBlockEntity(pos);
      if (te instanceof PipeTileEntity) {
        ((PipeTileEntity) te).clearCachedInventories();
      }
    }
    super.neighborChanged(state, world, pos, blockIn, neighbor, isMoving);
  }

  /* Bounds */

  // base bounds
  private static final VoxelShape BOUNDS_CENTER = Shapes.box(0.375, 0.25, 0.375, 0.625, 0.5, 0.625),
  // main bounds for side pipes
  BOUNDS_DOWN = Shapes.box(0.375, 0, 0.375, 0.625, 0.25, 0.625),
      BOUNDS_UP = Shapes.box(0.375, 0.5, 0.375, 0.625, 1, 0.625),
      BOUNDS_NORTH = Shapes.box(0.375, 0.25, 0, 0.625, 0.5, 0.375),
      BOUNDS_SOUTH = Shapes.box(0.375, 0.25, 0.625, 0.625, 0.5, 1),
      BOUNDS_WEST = Shapes.box(0, 0.25, 0.375, 0.375, 0.5, 0.625),
      BOUNDS_EAST = Shapes.box(0.625, 0.25, 0.375, 1, 0.5, 0.625),
  // extra bounds for the raytrace to select the little connections
  BOUNDS_DOWN_CONNECT = Shapes.box(0.34375, 0, 0.34375, 0.65625, 0.0625, 0.65625),
      BOUNDS_UP_CONNECT = Shapes.box(0.34375, 0.9375, 0.34375, 0.65625, 1, 0.65625),
      BOUNDS_NORTH_CONNECT = Shapes.box(0.34375, 0.21875, 0, 0.65625, 0.53125, 0.0625),
      BOUNDS_SOUTH_CONNECT = Shapes.box(0.34375, 0.21875, 0.9375, 0.65625, 0.53125, 1),
      BOUNDS_WEST_CONNECT = Shapes.box(0, 0.21875, 0.34375, 0.0625, 0.53125, 0.65625),
      BOUNDS_EAST_CONNECT = Shapes.box(0.9375, 0.21875, 0.34375, 1, 0.53125, 0.65625);


  // Compute a static lookup table for all the combinations.
  // First index is the facing, the second is a connections bitmask.
  private static final VoxelShape[][] BOUNDS = new VoxelShape[6][64];

  static {
    // above side bounds in an array to index easier - DUNSWE
    VoxelShape[] BOUNDS_SIDES = {
        BOUNDS_DOWN, BOUNDS_UP, BOUNDS_NORTH, BOUNDS_SOUTH, BOUNDS_WEST, BOUNDS_EAST
    };
    VoxelShape[] BOUNDS_CONN_SIDES = {
        BOUNDS_DOWN_CONNECT, BOUNDS_UP_CONNECT, BOUNDS_NORTH_CONNECT,
        BOUNDS_SOUTH_CONNECT, BOUNDS_WEST_CONNECT, BOUNDS_EAST_CONNECT
    };
    for (int i = 0; i < 64; i++) {
      VoxelShape shape = BOUNDS_CENTER;
      for (int j = 0; j < 6; j++) {
        if ((i & (1 << j)) != 0) {
          shape = Shapes.or(shape, BOUNDS_CONN_SIDES[j], BOUNDS_SIDES[j]);
        }
      }
      for (int j = 0; j < 6; j++) {
        BOUNDS[j][i] = Shapes.or(shape, BOUNDS_SIDES[j]);
      }
    }
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
    int bitmask = 0;
    for (int i = 0; i < 6; i++) {
      bitmask |= state.getValue(DIR_ENABLED[i]) ? (1 << i) : 0;
    }
    return BOUNDS[state.getValue(FACING).get3DDataValue()][bitmask];
  }
}
