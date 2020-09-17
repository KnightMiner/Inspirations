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
    super(Block.Properties.create(Material.ROCK)
                          .hardnessAndResistance(3.5F)
                          .sound(SoundType.STONE)
         );
    this.setDefaultState(this.getStateContainer().getBaseState()
                             .with(FACING, Direction.NORTH)
                             .with(TRIGGERED, false));
  }

  /* IHidable */

  @Override
  public boolean isEnabled() {
    return Config.enableCollector.get();
  }

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> stacks) {
    if (shouldAddtoItemGroup(group)) {
      super.fillItemGroup(group, stacks);
    }
  }

  /* Block state settings */

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block,BlockState> builder) {
    builder.add(FACING, TRIGGERED);
  }

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

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    // place opposite since its more useful to face into what you clicked
    Direction facing = context.getNearestLookingDirection();
    PlayerEntity player = context.getPlayer();
    if (player != null && player.isCrouching()) {
      facing = facing.getOpposite();
    }
    return this.getDefaultState().with(FACING, facing);
  }

  @Override
  public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
    // If destroyed, drop contents.
    if (state.getBlock() != newState.getBlock()) {
      TileEntity te = world.getTileEntity(pos);
      if (te instanceof IInventory) {
        InventoryHelper.dropInventoryItems(world, pos, (IInventory)te);
      }
    }
    super.onReplaced(state, world, pos, newState, isMoving);
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
    TileEntity te = world.getTileEntity(pos);
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
  public int getComparatorInputOverride(BlockState blockState, World world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
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
  public boolean hasComparatorInputOverride(BlockState state) {
    return true;
  }


  /* Collecting logic */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos neighbor, boolean isMoving) {
    // clear inventory cache
    if (pos.offset(state.get(FACING)).equals(neighbor)) {
      TileEntity te = world.getTileEntity(pos);
      if (te instanceof CollectorTileEntity) {
        ((CollectorTileEntity) te).clearCachedInventories();
      }
    }

    // update powered state
    boolean powered = world.isBlockPowered(pos) || world.isBlockPowered(pos.up());
    boolean triggered = state.get(TRIGGERED);
    if (powered && !triggered) {
      world.getPendingBlockTicks().scheduleTick(pos, this, 4);
      world.setBlockState(pos, state.with(TRIGGERED, true), 4);
    } else if (!powered && triggered) {
      world.setBlockState(pos, state.with(TRIGGERED, false), 4);
    }
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    if (world.isRemote) {
      return;
    }
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof CollectorTileEntity) {
      ((CollectorTileEntity)te).collect();
    }
  }
}
