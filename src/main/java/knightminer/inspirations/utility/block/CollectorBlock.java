package knightminer.inspirations.utility.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import knightminer.inspirations.utility.block.entity.CollectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkHooks;
import slimeknights.mantle.block.InventoryBlock;

import javax.annotation.Nullable;
import java.util.Random;

public class CollectorBlock extends InventoryBlock implements IHidable {

  public static final DirectionProperty FACING = BlockStateProperties.FACING;
  private static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

  public CollectorBlock() {
    super(Block.Properties.of(Material.STONE)
                          .strength(3.5F)
                          .sound(SoundType.STONE)
         );
    this.registerDefaultState(this.getStateDefinition().any()
                             .setValue(FACING, Direction.NORTH)
                             .setValue(TRIGGERED, false));
  }

  /* IHidable */

  @Override
  public boolean isEnabled() {
    return Config.enableCollector.getAsBoolean();
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
    builder.add(FACING, TRIGGERED);
  }

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

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    // place opposite since its more useful to face into what you clicked
    Direction facing = context.getNearestLookingDirection();
    Player player = context.getPlayer();
    if (player != null && player.isCrouching()) {
      facing = facing.getOpposite();
    }
    return this.defaultBlockState().setValue(FACING, facing);
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

  /* Tile Entity */

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new CollectorBlockEntity(pos, state);
  }

  @Override
  protected boolean openGui(Player player, Level world, BlockPos pos) {
    if (!(player instanceof ServerPlayer)) {
      throw new AssertionError("Needs to be server!");
    }
    BlockEntity te = world.getBlockEntity(pos);
    if (te instanceof CollectorBlockEntity) {
      NetworkHooks.openGui((ServerPlayer)player, (MenuProvider)te, pos);
      return true;
    }
    return false;
  }


  /* Comparator logic */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public int getAnalogOutputSignal(BlockState blockState, Level world, BlockPos pos) {
    BlockEntity te = world.getBlockEntity(pos);
    if (te != null) {
      return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).map(
          ItemHandlerHelper::calcRedstoneFromInventory
                                                                                      ).orElse(0);
    }
    return 0;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public boolean hasAnalogOutputSignal(BlockState state) {
    return true;
  }


  /* Collecting logic */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos neighbor, boolean isMoving) {
    // clear inventory cache
    if (pos.relative(state.getValue(FACING)).equals(neighbor)) {
      BlockEntity te = world.getBlockEntity(pos);
      if (te instanceof CollectorBlockEntity) {
        ((CollectorBlockEntity) te).clearCachedInventories();
      }
    }

    // update powered state
    boolean powered = world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.above());
    boolean triggered = state.getValue(TRIGGERED);
    if (powered && !triggered) {
      world.scheduleTick(pos, this, 4);
      world.setBlock(pos, state.setValue(TRIGGERED, true), 4);
    } else if (!powered && triggered) {
      world.setBlock(pos, state.setValue(TRIGGERED, false), 4);
    }
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
    if (world.isClientSide) {
      return;
    }
    BlockEntity te = world.getBlockEntity(pos);
    if (te instanceof CollectorBlockEntity) {
      ((CollectorBlockEntity)te).collect();
    }
  }
}
