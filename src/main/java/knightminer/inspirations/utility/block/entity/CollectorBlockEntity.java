package knightminer.inspirations.utility.block.entity;

import knightminer.inspirations.utility.InspirationsUtility;
import knightminer.inspirations.utility.block.menu.CollectorContainerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.block.entity.InventoryBlockEntity;
import slimeknights.mantle.util.WeakConsumerWrapper;

import javax.annotation.Nullable;

public class CollectorBlockEntity extends InventoryBlockEntity {
  private static final Component TITLE = new TranslatableComponent("gui.inspirations.collector");

  /** Cache of the current TE we are facing */
  @Nullable
  private LazyOptional<IItemHandler> facingHandler;
  /** Cache of the bounds to check for items */
  @Nullable
  private AABB itemBounds;

  /** Lambda to call on every item transfer. Final variable to reduce memory usage every tick */
  private final NonNullConsumer<IItemHandler> extractItem = this::extractItem;
  /** Lambda to call when a lazy optional is invalidated. Final variable to reduce memory usage */
  private final NonNullConsumer<LazyOptional<IItemHandler>> facingInvalidator = new WeakConsumerWrapper<>(this, (te, handler) -> {
    if (te.facingHandler == handler) {
      te.clearCachedInventories();
    }
  });

  public CollectorBlockEntity(BlockPos pos, BlockState state) {
    super(InspirationsUtility.tileCollector, pos, state, TITLE, false, 9);
  }

  /**
   * Called on redstone pulse to collect items from the world
   */
  public void collect() {
    if (level == null) {
      return;
    }

    // if there is an item handler, extract the items
    LazyOptional<IItemHandler> handler = getHandler();
    if (handler.isPresent()) {
      handler.ifPresent(extractItem);
    } else {
      // collect items from world
      boolean collected = false;
      for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, getItemBounds(), EntitySelector.ENTITY_STILL_ALIVE)) {
        ItemStack insert = entity.getItem();
        // no need to simulate, if successful we have to modify the stack regardless
        ItemStack remainder = ItemHandlerHelper.insertItemStacked(itemHandler, insert, false);
        // if the stack changed, we were successful
        if (remainder.getCount() < insert.getCount()) {
          collected = true;
          // empty means item is gone
          if (remainder.isEmpty()) {
            entity.discard();
          } else {
            entity.setItem(remainder);
          }
        }
      }
      // play sound. Plays dispenser dispense if success and dispenser fail if not
      level.levelEvent(collected ? LevelEvent.SOUND_DISPENSER_DISPENSE : LevelEvent.SOUND_DISPENSER_FAIL, worldPosition, 0);
    }
  }

  /**
   * Gets the item handler this collector is facing
   * @return  Item handler
   */
  private LazyOptional<IItemHandler> getHandler() {
    if (facingHandler != null) {
      return facingHandler;
    }

    // if no inventory cached yet, find a new one
    Direction facing = getBlockState().getValue(BlockStateProperties.FACING);
    assert level != null;
    BlockEntity te = level.getBlockEntity(worldPosition.relative(facing));
    // if we have a TE and its an item handler, try extracting from that
    if (te != null) {
      LazyOptional<IItemHandler> handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
      if (handler.isPresent()) {
        // add the invalidator
        handler.addListener(facingInvalidator);
        // cache and return
        return facingHandler = handler;
      }
    }
    // no item handler, cache empty
    return facingHandler = LazyOptional.empty();
  }

  /**
   * Gets the bounds for grabbing items
   * @return  Item bounds
   */
  private AABB getItemBounds() {
    if (itemBounds == null) {
      BlockPos offset = worldPosition.relative(getBlockState().getValue(BlockStateProperties.FACING));
      itemBounds = new AABB(offset.getX(), offset.getY(), offset.getZ(), offset.getX() + 1, offset.getY() + 1, offset.getZ() + 1);
    }
    return itemBounds;
  }

  /**
   * Logic to transfer a single item
   * @param neighbor  Neighbor to extract items from
   */
  private void extractItem(IItemHandler neighbor) {
    // basically, we iterate every slot, trying to remove a single item
    for (int i = 0; i < neighbor.getSlots(); i++) {
      ItemStack simulated = neighbor.extractItem(i, 1, true);
      // as soon as we find one we can extract, we try inserting it
      if (!simulated.isEmpty()) {
        // if it successfully inserts, extract it from the original inventory
        if (ItemHandlerHelper.insertItemStacked(itemHandler, simulated, false).isEmpty()) {
          neighbor.extractItem(i, 1, false);
          break;
        }
      }
    }
  }

  /**
   * Called when a neighbor updates to invalidate the inventory cache
   */
  public void clearCachedInventories() {
    this.facingHandler = null;
  }

  @Override
  public void setBlockState(BlockState pBlockState) {
    super.setBlockState(pBlockState);
    // if the block changed and this TE is intact, remove cache. likely we were rotated
    this.clearCachedInventories();
  }

  @Override
  public boolean canPlaceItem(int slot, ItemStack itemstack) {
    // mantle checks stack size which breaks some things when using stacks bigger than 1
    return slot < getContainerSize();
  }

  /*
   * GUI
   */

  @Nullable
  @Override
  public AbstractContainerMenu createMenu(int winId, Inventory playerInv, Player player) {
    return new CollectorContainerMenu(winId, playerInv, this);
  }
}
