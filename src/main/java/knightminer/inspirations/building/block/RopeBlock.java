package knightminer.inspirations.building.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.block.HidableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Locale;

import net.minecraft.block.AbstractBlock.Properties;

@SuppressWarnings("WeakerAccess")
public class RopeBlock extends HidableBlock implements IWaterLoggable {
  public static final EnumProperty<Rungs> RUNGS = EnumProperty.create("rungs", Rungs.class);
  public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");
  private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

  // Number of items used per block.
  public static final int RUNG_ITEM_COUNT = 4;

  private Item rungsItem;

  public RopeBlock(Item rungsItem, Properties props) {
    super(props, Config.enableRope);
    this.registerDefaultState(this.stateDefinition.any()
                                            .setValue(BOTTOM, false)
                                            .setValue(RUNGS, Rungs.NONE)
                                            .setValue(WATERLOGGED, false)
                        );
    this.rungsItem = rungsItem;
  }

  @Override
  protected void createBlockStateDefinition(StateContainer.Builder<Block,BlockState> builder) {
    builder.add(BOTTOM, RUNGS, WATERLOGGED);
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }

  public Item getRungsItem() {
    return rungsItem;
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    BlockPos down = context.getClickedPos().below();
    return defaultBlockState()
        .setValue(BOTTOM, isBottom(context.getLevel().getBlockState(down), context.getLevel(), down))
        .setValue(RUNGS, Rungs.NONE)
        .setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
  }

  private static final VoxelShape ATTACH_TOP = Block.box(7, 15, 7, 9, 16, 9);
  private static final VoxelShape ATTACH_BOTTOM = Block.box(7, 0, 7, 9, 1, 9);

  private boolean isBottom(BlockState state, IBlockReader world, BlockPos pos) {
    if (state.getBlock() == this) {
      return false;
    }
    // Check if the top of the block is able to attach to the rope - the center 2x2 must
    // all be present.
    return state.is(BlockTags.LEAVES)
           || VoxelShapes.joinIsNotEmpty(state.getCollisionShape(world, pos).getFaceShape(Direction.UP), ATTACH_TOP, IBooleanFunction.ONLY_SECOND);
  }

  /* Ropey logic */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
    return super.canSurvive(state, world, pos) && isValidRope(world, pos);
  }

  private boolean isValidRope(IWorldReader world, BlockPos pos) {
    BlockPos up = pos.above();
    BlockState state = world.getBlockState(up);
    if (state.getBlock() == this) {
      return true;
    }
    // Check if the bottom of the block is able to attach to the rope - the center 4x4 must
    // all be present.
    return !state.is(BlockTags.LEAVES) && !VoxelShapes.joinIsNotEmpty(
        state.getCollisionShape(world, pos).getFaceShape(Direction.DOWN), ATTACH_BOTTOM, IBooleanFunction.ONLY_SECOND
                                                                );
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
    // if the rope is not valid, break it
    if (!this.isValidRope(world, pos)) {
      return Blocks.AIR.defaultBlockState();
    }
    if (facing == Direction.DOWN) {
      BlockPos down = pos.below();
      return state.setValue(BOTTOM, isBottom(world.getBlockState(down), world, down));
    }
    return state;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
    // no need to check verticals, one is not possible and the other normal block placement
    if (hit.getDirection().getAxis().isVertical()) {
      return ActionResultType.PASS;
    }

    // right click with a rope to extend downwards
    ItemStack stack = player.getItemInHand(hand);
    // check if the item is the same type as us
    if (Block.byItem(stack.getItem()) != this) {
      return ActionResultType.PASS;
    }

    // find the first block at the bottom of the rope
    BlockPos next = pos.below();
    while (world.getBlockState(next).getBlock() == this) {
      next = next.below();
    }
    if (this.canSurvive(state, world, next)) {
      ((BlockItem)stack.getItem()).place(new BlockItemUseContext(player, hand, stack, new BlockRayTraceResult(Vector3d.atBottomCenterOf(next), Direction.UP, next, false)));
    }

    return ActionResultType.SUCCESS;
  }

  @Override
  public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
    // when breaking, place all items from ropes below at the position of this rope
    // break all blocks below that are ropes
    BlockPos next = pos.below();
    int count = 0;
    int rungs = 0;
    // go down to the bottom
    BlockState below = world.getBlockState(next);
    while (below.getBlock() == this) {
      count++;
      if (below.getValue(RUNGS) != Rungs.NONE) {
        rungs++;
      }
      next = next.below();
      below = world.getBlockState(next);
    }
    // then break them coming back up
    for (int i = 0; i < count; i++) {
      next = next.above();
      world.destroyBlock(next, false);
    }

    // then spawn their items up here
    ItemStack drops = new ItemStack(this, count);
    popResource(world, pos, drops);
    if (rungs > 0) {
      popResource(world, pos, new ItemStack(rungsItem, rungs * RUNG_ITEM_COUNT));
    }

    super.playerWillDestroy(world, pos, state, player);
  }


  /* Bounds */

  // Shape for collisions. Indexes are Rungs ordinals.
  private static final VoxelShape[] SHAPE = new VoxelShape[3];
  private static final VoxelShape[] SHAPE_BOTTOM = new VoxelShape[3];

  static {
    VoxelShape rope_core = Block.box(7, 0, 7, 9, 16, 9);
    VoxelShape rope_core_bottom = VoxelShapes.or(
        Block.box(7, 7, 7, 9, 16, 9),
        Block.box(6.5, 4, 6.5, 9.5, 7, 9.5)
                                                );

    VoxelShape rope_rungs_x = VoxelShapes.or(
        Block.box(1, 5, 7, 15, 7, 9),
        Block.box(1, 9, 7, 15, 11, 9),
        Block.box(1, 13, 7, 15, 15, 9)
                                            );
    VoxelShape rope_rungs_z = VoxelShapes.or(
        Block.box(7, 5, 1, 9, 7, 15),
        Block.box(7, 9, 1, 9, 11, 15),
        Block.box(7, 13, 1, 9, 15, 15)
                                            );

    SHAPE[Rungs.NONE.ordinal()] = rope_core;
    SHAPE_BOTTOM[Rungs.NONE.ordinal()] = rope_core_bottom;

    SHAPE[Rungs.X.ordinal()] = VoxelShapes.or(rope_core, rope_rungs_x,
                                              Block.box(1, 1, 7, 15, 3, 9)
                                             );
    SHAPE_BOTTOM[Rungs.X.ordinal()] = VoxelShapes.or(rope_core_bottom, rope_rungs_x);

    SHAPE[Rungs.Z.ordinal()] = VoxelShapes.or(rope_core, rope_rungs_z,
                                              Block.box(7, 1, 1, 9, 3, 15)
                                             );
    SHAPE_BOTTOM[Rungs.Z.ordinal()] = VoxelShapes.or(rope_core_bottom, rope_rungs_z);
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return (state.getValue(BOTTOM) ? SHAPE_BOTTOM : SHAPE)[state.getValue(RUNGS).ordinal()];
  }

  @Override
  @SuppressWarnings("deprecation")
  @Deprecated
  public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    // if no rungs, no collision
    return state.getValue(RUNGS) != Rungs.NONE ? state.getShape(worldIn, pos) : VoxelShapes.empty();
  }

  public enum Rungs implements IStringSerializable {
    NONE,
    X,
    Z;

    @Override
    public String getSerializedName() {
      return this.name().toLowerCase(Locale.US);
    }

    public static Rungs fromAxis(Direction.Axis axis) {
      switch (axis) {
        case X:
          return X;
        case Z:
          return Z;
      }
      return NONE;
    }
  }
}
