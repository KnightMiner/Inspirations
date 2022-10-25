package knightminer.inspirations.utility.tileentity;

import knightminer.inspirations.utility.InspirationsUtility;
import knightminer.inspirations.utility.inventory.CollectorContainer;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants.WorldEvents;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.tileentity.InventoryTileEntity;
import slimeknights.mantle.util.WeakConsumerWrapper;

import javax.annotation.Nullable;

public class CollectorTileEntity extends InventoryTileEntity {
  private static final ITextComponent TITLE = new TranslationTextComponent("gui.inspirations.collector");

  /** Cache of the current TE we are facing */
  @Nullable
  private LazyOptional<IItemHandler> facingHandler;
  /** Cache of the bounds to check for items */
  @Nullable
  private AxisAlignedBB itemBounds;

  /** Lambda to call on every item transfer. Final variable to reduce memory usage every tick */
  private final NonNullConsumer<IItemHandler> extractItem = this::extractItem;
  /** Lambda to call when a lazy optional is invalidated. Final variable to reduce memory usage */
  private final NonNullConsumer<LazyOptional<IItemHandler>> facingInvalidator = new WeakConsumerWrapper<>(this, (te, handler) -> {
    if (te.facingHandler == handler) {
      te.clearCachedInventories();
    }
  });

  public CollectorTileEntity() {
    super(InspirationsUtility.tileCollector, TITLE, 9);
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
      for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, getItemBounds())) {
        ItemStack insert = entity.getItem();
        // no need to simulate, if successful we have to modify the stack regardless
        ItemStack remainder = ItemHandlerHelper.insertItemStacked(itemHandler, insert, false);
        // if the stack changed, we were successful
        if (remainder.getCount() < insert.getCount()) {
          collected = true;
          // empty means item is gone
          if (remainder.isEmpty()) {
            entity.remove();
          } else {
            entity.setItem(remainder);
          }
        }
      }
      // play sound. Plays dispenser dispense if success and dispenser fail if not
      level.levelEvent(collected ? WorldEvents.DISPENSER_DISPENSE_SOUND : WorldEvents.DISPENSER_FAIL_SOUND, worldPosition, 0);
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
    TileEntity te = level.getBlockEntity(worldPosition.relative(facing));
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
  private AxisAlignedBB getItemBounds() {
    if (itemBounds == null) {
      BlockPos offset = worldPosition.relative(getBlockState().getValue(BlockStateProperties.FACING));
      itemBounds = new AxisAlignedBB(offset.getX(), offset.getY(), offset.getZ(), offset.getX() + 1, offset.getY() + 1, offset.getZ() + 1);
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
  public void clearCache() {
    super.clearCache();
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
  public Container createMenu(int winId, PlayerInventory playerInv, PlayerEntity player) {
    return new CollectorContainer(winId, playerInv, this);
  }
}
