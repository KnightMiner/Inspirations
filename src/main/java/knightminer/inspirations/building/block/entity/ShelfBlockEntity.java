package knightminer.inspirations.building.block.entity;

import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.block.ShelfBlock;
import knightminer.inspirations.building.block.menu.ShelfContainerMenu;
import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.common.network.InventorySlotSyncPacket;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.block.entity.IRetexturedBlockEntity;
import slimeknights.mantle.block.entity.NameableBlockEntity;
import slimeknights.mantle.util.RetexturedHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShelfBlockEntity extends NameableBlockEntity implements IRetexturedBlockEntity {
  public static final ModelProperty<Integer> BOOKS = new ModelProperty<>();
  private static final Component TITLE = new TranslatableComponent("gui.inspirations.shelf.name");

  /**
   * Cached enchantment bonus, so we are not constantly digging the inventory
   */
  private float enchantBonus = Float.NaN;

  private final ShelfInventory inventory = new ShelfInventory(this);
  private final LazyOptional<IItemHandler> itemCapability = LazyOptional.of(() -> inventory);
  private final IModelData data = new ModelDataMap.Builder().withProperty(BOOKS).withProperty(RetexturedHelper.BLOCK_PROPERTY).build();
  public ShelfBlockEntity(BlockPos pos, BlockState state) {
    super(InspirationsBuilding.shelfTileEntity, pos, state, TITLE);
  }

  /**
   * Determines which index to modify based on the hit vector and held item
   * @param held   Currently held item
   * @param click  Click vector
   * @return  Index of book clicked, or -1 if the item cannot be placed
   */
  private int getIndexFromHit(ItemStack held, Vec3 click) {
    Direction dir = getBlockState().getValue(ShelfBlock.FACING).getCounterClockWise();
    // location clicked on the block, 0 to 1
    double clicked = (dir.getStepX() * (click.x - 0.5)) + (dir.getStepZ() * (click.z - 0.5)) + 0.5;
    // pixel clicked, 0 to 15
    int pixel = Mth.clamp((int)(clicked * 16), 0, 15);
    // shelf index clicked, 0 to 7
    int shelfIndex = pixel / 2;
    // index for the whole shelf
    int slotIndex = (click.y <= 0.4375 ? 8 : 0) + shelfIndex;
    // if there is an item at the current index, we will remove it
    if (!inventory.getStackInSlot(slotIndex).isEmpty()) {
      return slotIndex;
    }
    // item at the previous index means remove that (assuming its a book)
    boolean previousEmpty = false;
    if (shelfIndex != 0) {
      ItemStack previous = inventory.getStackInSlot(slotIndex - 1);
      if (!previous.isEmpty() && !InspirationsRegistry.isBook(previous)) {
        return slotIndex - 1;
      }
      previousEmpty = previous.isEmpty();
    }
    // at this point we are adding an item, find the proper index to add
    if (held.isEmpty()) {
      return -1;
    }
    // for books, the current is proper
    if (InspirationsRegistry.isBook(held)) {
      return slotIndex;
    }
    // if the next slot if filled (or invalid), this spot is invalid
    // if the previous is invalid however, we can use that
    if (shelfIndex == 7 || !inventory.getStackInSlot(slotIndex + 1).isEmpty()) {
      return previousEmpty ? slotIndex - 1 : -1;
    }
    // centering the item feels more natural, so on even pixels attempt to offset back one
    if (previousEmpty && pixel % 2 == 0) {
      // shelf index 1 always works (0 will never hit this)
      if (shelfIndex == 1) {
        return slotIndex - 1;
      }
      // at 2 or more, need to ensure the item before the previous is empty or a book
      ItemStack beforePrevious = inventory.getStackInSlot(slotIndex - 2);
      if (beforePrevious.isEmpty() || InspirationsRegistry.isBook(beforePrevious)) {
        return slotIndex - 1;
      }
    }
    return slotIndex;
  }

  /*
   * Book logic
   */

  /**
   * Interacts with the TE, adding or removing a book if possible
   * @param player  Player interacting
   * @param hand    Hand used
   * @param click   Block relative click location
   * @return  True if the shelf was modified
   */
  public boolean interact(Player player, InteractionHand hand, Vec3 click) {
    ItemStack stack = player.getItemInHand(hand);
    int index = getIndexFromHit(stack, click);
    if (index == -1) {
      return false;
    }

    // if it contains a book, take the book out
    ItemStack current = inventory.getStackInSlot(index);
    if (!current.isEmpty()) {
      if (level != null && !level.isClientSide) {
        ItemHandlerHelper.giveItemToPlayer(player, current, player.getInventory().selected);
        inventory.setStackInSlot(index, ItemStack.EMPTY);
      }
      return true;
    }

    // try adding book
    if (inventory.canInsertItem(index, stack)) {
      if (level != null && !level.isClientSide) {
        inventory.setStackInSlot(index, stack.split(1));
      }
      return true;
    }
    return false;
  }

  /**
   * Called when the shelf items change
   * @param slot       Slot index
   * @param oldStack   Old stack in the slot
   * @param newStack   New stack in the slot, may be same instance as the old if the size just changed
   */
  public void onSlotChanged(int slot, ItemStack oldStack, ItemStack newStack) {
    // slot update
    Level world = getLevel();
    if (world != null && !world.isClientSide) {
      InspirationsNetwork.sendToClients(world, this.worldPosition, new InventorySlotSyncPacket(newStack, slot, worldPosition));
    }
    if (world != null) {
      // update for rendering
      if (world.isClientSide) {
        ModelDataManager.requestModelDataRefresh(this);
      }

      // if we have redstone books and either the old stack xor the new one is a book, update
      if (oldStack.getItem() == InspirationsBuilding.redstoneBook ^ newStack.getItem() == InspirationsBuilding.redstoneBook) {
        world.updateNeighbourForOutputSignal(worldPosition, this.getBlockState().getBlock());
      }
    }
    // clear bonus to recalculate it
    enchantBonus = Float.NaN;
  }


  /*
   * Inventory
   */

  /** Gets the shelf inventory */
  public ShelfInventory getInventory() {
    return inventory;
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
    if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      return itemCapability.cast();
    }
    return super.getCapability(cap, side);
  }

  @Override
  public void invalidateCaps() {
    super.invalidateCaps();
    itemCapability.invalidate();
  }


  /*
   * GUI
   */

  @Nullable
  @Override
  public AbstractContainerMenu createMenu(int winId, Inventory playerInv, Player player) {
    return new ShelfContainerMenu(winId, playerInv, this);
  }


  /*
   * Extra logic
   */

  /** Gets the value to display on comparators */
  public int getComparatorPower() {
    // return largest slot index of redstone book
    for (int i = ShelfInventory.MAX_ITEMS; i >= 0; i--) {
      if (inventory.getStackInSlot(i).getItem() == InspirationsBuilding.redstoneBook) {
        return i;
      }
    }
    return 0;
  }

  /** Gets the power for an enchantment table */
  public float getEnchantPower() {
    // if we have a cached value, use that
    if (!Float.isNaN(enchantBonus)) {
      return enchantBonus;
    }
    // simple sum of all books with the power of a full shelf
    float books = 0;
    for (int i = 0; i < ShelfInventory.MAX_ITEMS; i++) {
      ItemStack stack = inventory.getStackInSlot(i);
      if (!stack.isEmpty()) {
        float power = InspirationsRegistry.getBookEnchantingPower(stack);
        if (power > 0) {
          books += power;
        }
      }
    }

    // divide by 14 since that is the number of books in a shelf
    enchantBonus = books / ShelfInventory.MAX_ITEMS;
    return enchantBonus;
  }

  /*
   * Rendering
   */
  @Nonnull
  @Override
  public IModelData getModelData() {
    // pack books into integer
    int books = 0;
    for (int i = 0; i < ShelfInventory.MAX_ITEMS; i++) {
      // non books will render in the TESR
      if (InspirationsRegistry.isBook(inventory.getStackInSlot(i))) {
        books |= 1 << i;
      }
    }
    // get texture if present
    data.setData(BOOKS, books);
    Block texture = getTexture();
    if (texture != Blocks.AIR) {
      data.setData(RetexturedHelper.BLOCK_PROPERTY, texture);
    }
    return data;
  }


  /*
   * Networking
   */

  @Override
  protected boolean shouldSyncOnUpdate() {
    return true;
  }


  /* NBT */
  private static final String TAG_ITEMS = "Items";

  @Override
  public void saveSynced(CompoundTag tags) {
    super.saveSynced(tags);
    tags.put(TAG_ITEMS, inventory.serializeNBT());
  }

  @Override
  public void load(CompoundTag tags) {
    super.load(tags);
    if (tags.contains(TAG_ITEMS, Tag.TAG_LIST)) {
      inventory.deserializeNBT(tags.getList(TAG_ITEMS, Tag.TAG_COMPOUND));
      if (level != null && level.isClientSide) {
        requestModelDataUpdate();
        BlockState state = getBlockState();
        level.sendBlockUpdated(worldPosition, state, state, 0);
      }
    }
  }
}
