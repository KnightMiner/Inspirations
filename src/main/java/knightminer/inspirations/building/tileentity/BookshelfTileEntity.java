package knightminer.inspirations.building.tileentity;

import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.inventory.BookshelfContainer;
import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.common.network.InventorySlotSyncPacket;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.tileentity.IRetexturedTileEntity;
import slimeknights.mantle.tileentity.InventoryTileEntity;
import slimeknights.mantle.util.RetexturedHelper;

import javax.annotation.Nullable;

public class BookshelfTileEntity extends InventoryTileEntity implements IRetexturedTileEntity {
  private static final int MAX_BOOKS = 16;
  public static final ModelProperty<Integer> BOOKS = new ModelProperty<>();
  private static final ITextComponent TITLE = new TranslationTextComponent("gui.inspirations.bookshelf.name");

  /**
   * Cached enchantment bonus, so we are not constantly digging the inventory
   */
  private float enchantBonus = Float.NaN;

  private final IModelData data = new ModelDataMap.Builder().withProperty(BOOKS).withProperty(RetexturedHelper.BLOCK_PROPERTY).build();
  public BookshelfTileEntity() {
    super(InspirationsBuilding.tileBookshelf, TITLE, MAX_BOOKS, 1);
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack itemstack) {
    ItemStack oldStack = this.getStackInSlot(slot);

    // we sync slot changes to all clients around
    if (getWorld() != null && getWorld() instanceof ServerWorld && !getWorld().isRemote && !ItemStack.areItemStacksEqual(itemstack, getStackInSlot(slot))) {
      InspirationsNetwork.sendToClients((ServerWorld)getWorld(), this.pos, new InventorySlotSyncPacket(itemstack, slot, pos));
    }
    super.setInventorySlotContents(slot, itemstack);

    if (world != null) {
      // update for rendering
      if (world.isRemote) {
        ModelDataManager.requestModelDataRefresh(this);
      }

      // if we have redstone books and either the old stack xor the new one is a book, update
      if (oldStack.getItem() == InspirationsBuilding.redstoneBook ^ itemstack.getItem() == InspirationsBuilding.redstoneBook) {
        world.updateComparatorOutputLevel(pos, this.getBlockState().getBlock());
      }
    }

    // clear bonus to recalculate it
    enchantBonus = Float.NaN;
  }

  /*
   * Book logic
   */

  public boolean interact(PlayerEntity player, Hand hand, int bookClicked) {
    // if it contains a book, take the book out
    if (isStackInSlot(bookClicked)) {
      if (world != null && !world.isRemote) {
        ItemHandlerHelper.giveItemToPlayer(player, getStackInSlot(bookClicked), player.inventory.currentItem);
        setInventorySlotContents(bookClicked, ItemStack.EMPTY);
      }
      return true;
    }

    // try adding book
    ItemStack stack = player.getHeldItem(hand);
    if (InspirationsRegistry.isBook(stack)) {
      if (world != null && !world.isRemote) {
        setInventorySlotContents(bookClicked, stack.split(1));
      }
      return true;
    }

    return false;
  }


  /*
   * GUI
   */

  @Nullable
  @Override
  public Container createMenu(int winId, PlayerInventory playerInv, PlayerEntity player) {
    return new BookshelfContainer(winId, playerInv, this);
  }

  /*
   * Extra logic
   */

  public int getComparatorPower() {
    for (int i = 0; i < MAX_BOOKS; i++) {
      if (getStackInSlot(i).getItem() == InspirationsBuilding.redstoneBook) {
        // last gives 15, first gives 0
        return i;
      }
    }
    return 0;
  }

  public float getEnchantPower() {
    // if we have a cached value, use that
    if (!Float.isNaN(enchantBonus)) {
      return enchantBonus;
    }
    // simple sum of all books with the power of a full shelf
    float books = 0;
    for (int i = 0; i < MAX_BOOKS; i++) {
      if (isStackInSlot(i)) {
        float power = InspirationsRegistry.getBookEnchantingPower(getStackInSlot(i));
        if (power >= 0) {
          books += power;
        }
      }
    }

    // divide by 14 since that is the number of books in a shelf
    enchantBonus = books / MAX_BOOKS;
    return enchantBonus;
  }

  /*
   * Rendering
   */
  @Override
  public IModelData getModelData() {
    // pack books into integer
    int books = 0;
    for (int i = 0; i < MAX_BOOKS; i++) {
      if (isStackInSlot(i)) {
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

  @Override
  public void read(BlockState blockState, CompoundNBT tags) {
    // temporary workaround to resize the inventory
    if (tags.contains("InventorySize")) {
      tags.putInt("InventorySize", MAX_BOOKS);
    }
    super.read(blockState, tags);
  }

  @Override
  public CompoundNBT getUpdateTag() {
    // book already in regular write, add to synced
    CompoundNBT nbt = super.getUpdateTag();
    writeInventoryToNBT(nbt);
    return nbt;
  }
}
