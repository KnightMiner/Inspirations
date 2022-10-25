package knightminer.inspirations.utility.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import knightminer.inspirations.utility.tileentity.CollectorTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.block.InventoryBlock;

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
    return Config.enableCollector.get();
  }

  @Override
  public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> stacks) {
    if (shouldAddtoItemGroup(group)) {
      super.fillItemCategory(group, stacks);
    }
  }

  /* Block state settings */

  @Override
  protected void createBlockStateDefinition(StateContainer.Builder<Block,BlockState> builder) {
    builder.add(FACING, TRIGGERED);
  }

  @Override
  public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation direction) {
    return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public BlockState mirror(BlockState state, Mirror mirror) {
    return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    // place opposite since its more useful to face into what you clicked
    Direction facing = context.getNearestLookingDirection();
    PlayerEntity player = context.getPlayer();
    if (player != null && player.isCrouching()) {
      facing = facing.getOpposite();
    }
    return this.defaultBlockState().setValue(FACING, facing);
  }

  @Override
  public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
    // If destroyed, drop contents.
    if (state.getBlock() != newState.getBlock()) {
      TileEntity te = world.getBlockEntity(pos);
      if (te instanceof IInventory) {
        InventoryHelper.dropContents(world, pos, (IInventory)te);
      }
    }
    super.onRemove(state, world, pos, newState, isMoving);
  }

  /* Tile Entity */

  @Override
  public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
    return new CollectorTileEntity();
  }

  @Override
  protected boolean openGui(PlayerEntity player, World world, BlockPos pos) {
    if (!(player instanceof ServerPlayerEntity)) {
      throw new AssertionError("Needs to be server!");
    }
    TileEntity te = world.getBlockEntity(pos);
    if (te instanceof CollectorTileEntity) {
      NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)te, pos);
      return true;
    }
    return false;
  }


  /* Comparator logic */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public int getAnalogOutputSignal(BlockState blockState, World world, BlockPos pos) {
    TileEntity te = world.getBlockEntity(pos);
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
  public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos neighbor, boolean isMoving) {
    // clear inventory cache
    if (pos.relative(state.getValue(FACING)).equals(neighbor)) {
      TileEntity te = world.getBlockEntity(pos);
      if (te instanceof CollectorTileEntity) {
        ((CollectorTileEntity) te).clearCachedInventories();
      }
    }

    // update powered state
    boolean powered = world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.above());
    boolean triggered = state.getValue(TRIGGERED);
    if (powered && !triggered) {
      world.getBlockTicks().scheduleTick(pos, this, 4);
      world.setBlock(pos, state.setValue(TRIGGERED, true), 4);
    } else if (!powered && triggered) {
      world.setBlock(pos, state.setValue(TRIGGERED, false), 4);
    }
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    if (world.isClientSide) {
      return;
    }
    TileEntity te = world.getBlockEntity(pos);
    if (te instanceof CollectorTileEntity) {
      ((CollectorTileEntity)te).collect();
    }
  }
}
