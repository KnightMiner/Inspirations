package knightminer.inspirations.utility.tileentity;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.utility.InspirationsUtility;
import knightminer.inspirations.utility.block.PipeBlock;
import knightminer.inspirations.utility.inventory.PipeContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.tileentity.InventoryTileEntity;
import slimeknights.mantle.util.WeakConsumerWrapper;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;

public class PipeTileEntity extends InventoryTileEntity implements ITickableTileEntity {
  private static final ITextComponent TITLE = new TranslationTextComponent("gui.inspirations.pipe");

  /* Number of ticks before transfer is allowed again */
  private short cooldown = 0;

  /** Cache of the current TE we are facing */
  @Nullable
  private LazyOptional<IItemHandler> facingHandler = null;
  /** Cache of the current hopper we are facing */
  @Nullable
  private WeakReference<HopperTileEntity> hopper;

  /** Lambda to call on every item transfer. Final variable to reduce memory usage every tick */
  private final NonNullConsumer<IItemHandler> transferItem = this::transferItem;
  /** Lambda to call when a lazy optional is invalidated. Final variable to reduce memory usage */
  private final NonNullConsumer<LazyOptional<IItemHandler>> facingInvalidator = new WeakConsumerWrapper<>(this, (te, handler) -> {
    if (te.facingHandler == handler) {
      te.clearCachedInventories();
    }
  });

  public PipeTileEntity() {
    super(InspirationsUtility.tilePipe, TITLE, 1);
  }

  @Override
  public void tick() {
    if (level == null || level.isClientSide) {
      return;
    }

    // do not function if facing up when disallowed
    Direction facing = this.getBlockState().getValue(PipeBlock.FACING);
    if (!Config.pipeUpwards.get() && facing == Direction.UP) {
      return;
    }

    // process cooldown
    cooldown--;
    if (cooldown > 0) {
      return;
    }
    cooldown = 0;

    // no items, skip
    if (this.isEmpty()) {
      return;
    }

    // transfer items if we have a handler
    getHandler(facing).ifPresent(transferItem);
  }

  /**
   * Gets the item handler in the given direction
   * @param facing  Direction
   * @return  Item handler
   */
  private LazyOptional<IItemHandler> getHandler(Direction facing) {
    // return cached handler if present
    if (facingHandler != null) {
      return facingHandler;
    }

    // fetch TE and capability
    assert level != null;
    TileEntity te = level.getBlockEntity(worldPosition.relative(facing));
    if (te != null) {
      LazyOptional<IItemHandler> handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
      if (handler.isPresent()) {
        // add the invalidator
        handler.addListener(facingInvalidator);
        // if its a hopper, store that so we can update cooldowns
        if (te instanceof HopperTileEntity) {
          hopper = new WeakReference<>((HopperTileEntity)te);
        }
        // cache and return
        return facingHandler = handler;
      }
    }
    // no item handler, cache empty
    return facingHandler = LazyOptional.empty();
  }

  /**
   * Logic to transfer a single item
   * @param neighbor  Neighbor to transfer items into
   */
  private void transferItem(IItemHandler neighbor) {
    ItemStack stack = getItem(0);
    if (stack.isEmpty()) {
      return;
    }

    ItemStack copy = stack.copy();
    copy.setCount(1);
    // if we successfully place it in, shrink it here
    if (ItemHandlerHelper.insertItemStacked(neighbor, copy, false).isEmpty()) {
      // set cooldown on the hopper
      if (hopper != null) {
        HopperTileEntity hop = this.hopper.get();
        if (hop != null) {
          hop.setCooldown(8);
        }
      }

      // remove the stack if empty
      stack.shrink(1);
      if (stack.isEmpty()) {
        this.setItem(0, ItemStack.EMPTY);
      }
      cooldown = 8;

      this.setChanged();
    }
  }

  /**
   * Called when a neighbor updates to invalidate the inventory cache
   */
  public void clearCachedInventories() {
    this.facingHandler = null;
    this.hopper = null;
  }

  @Override
  public void clearCache() {
    super.clearCache();
    // if the block changed and this TE is intact, remove cache. likely we were rotated
    this.clearCachedInventories();
  }

  @Override
  public void setItem(int slot, ItemStack itemstack) {
    super.setItem(slot, itemstack);
    cooldown = 7; // set the cooldown to prevent instant retransfer
  }


  /* GUI */

  @Override
  public Container createMenu(int winId, PlayerInventory inv, PlayerEntity entity) {
    return new PipeContainer(winId, inv, this);
  }


  /* NBT */

  private static final String TAG_COOLDOWN = "cooldown";

  @Override
  public void load(BlockState state, CompoundNBT tags) {
    super.load(state, tags);
    this.cooldown = tags.getShort(TAG_COOLDOWN);
  }

  @Override
  public CompoundNBT save(CompoundNBT tags) {
    super.save(tags);
    tags.putShort(TAG_COOLDOWN, this.cooldown);

    return tags;
  }
}
