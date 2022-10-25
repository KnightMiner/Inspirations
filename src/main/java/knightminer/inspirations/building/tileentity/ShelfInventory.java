package knightminer.inspirations.building.tileentity;

import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class ShelfInventory implements IItemHandler, IItemHandlerModifiable, INBTSerializable<ListNBT> {
	/** Max size of the shelf */
	public static final int MAX_ITEMS = 16;

	/** Tile entity holding this inventory */
	private final ShelfTileEntity parent;
	/** List of items in this inventory */
	private final NonNullList<ItemStack> stacks = NonNullList.withSize(MAX_ITEMS, ItemStack.EMPTY);

	public ShelfInventory(ShelfTileEntity parent) {
		this.parent = parent;
	}

	/** Called when the slot content change */
	private void onSlotChanged(int slot, ItemStack oldStack) {
		parent.onSlotChanged(slot, oldStack, stacks.get(slot));
	}

	/* Basic info */

	@Override
	public int getSlots() {
		return MAX_ITEMS;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return true;
	}


	/* Helpers */

	/** Checks if the slot is valid */
	private boolean isSlotValid(int slot) {
		return slot >= 0 && slot < MAX_ITEMS;
	}

	/**
	 * Checks if the slot can hold this item in general, ignoring current contents
	 * Considers neighbor slots, unlike {@link #isItemValid(int, ItemStack)}
	 */
	public boolean canHoldItem(int slot, ItemStack stack) {
		// invalid index, bad
		if (!isSlotValid(slot) || stack.isEmpty()) {
			return false;
		}
		// cannot have a item in the stack before the current
		if (slot != 0 && slot != 8) {
			ItemStack previous = stacks.get(slot - 1);
			if (!previous.isEmpty() && !InspirationsRegistry.isBook(previous)) {
				return false;
			}
		}
		// books are always valid
		if (InspirationsRegistry.isBook(stack)) {
			return true;
		}
		// if last item in the shelf is only valid for books
		if (slot == 7 || slot == 15) {
			return false; // TODO: consider checking neighbor
		}
		// must have no stack in the next slot
		return stacks.get(slot + 1).isEmpty();
	}

	/** Checks if the given slot can accept this item */
	public boolean canInsertItem(int slot, ItemStack stack) {
		return canHoldItem(slot, stack) && stacks.get(slot).isEmpty();
	}


	/* Read write */

	@Override
	public ItemStack getStackInSlot(int slot) {
		return isSlotValid(slot) ? stacks.get(slot) : ItemStack.EMPTY;
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		if (stack.isEmpty() || canHoldItem(slot, stack)) {
			ItemStack oldStack = stacks.get(slot);
			this.stacks.set(slot, stack);
			onSlotChanged(slot, oldStack);
		}
	}


	/* Insert extract */

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		// ensure the item can currently be added
		if (!canInsertItem(slot, stack)) {
			return stack;
		}
		// decrease input by 1, update slot contents
		ItemStack result = ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - 1);
		if (!simulate) {
			setStackInSlot(slot, ItemHandlerHelper.copyStackWithSize(stack, 1));
		}
		return result;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount <= 0 || !isSlotValid(slot)) {
			return ItemStack.EMPTY;
		}
		// if nothing, done
		ItemStack current = stacks.get(slot);
		if (current.isEmpty()) {
			return ItemStack.EMPTY;
		}
		// make a copy of the item to return, update if needed
		ItemStack result = ItemHandlerHelper.copyStackWithSize(current, 1);
		if (!simulate) {
			setStackInSlot(slot, ItemStack.EMPTY);
		}
		return result;
	}


	/* NBT */
	private static final String SLOT_TAG = "Slot";

	@Override
	public ListNBT serializeNBT() {
		ListNBT list = new ListNBT();
		for (int i = 0; i < MAX_ITEMS; i++) {
			ItemStack stack = stacks.get(i);
			if (!stack.isEmpty()) {
				CompoundNBT itemTag = new CompoundNBT();
				itemTag.putByte(SLOT_TAG, (byte)i);
				stack.save(itemTag);
				list.add(itemTag);
			}
		}
		return list;
	}

	@Override
	public void deserializeNBT(ListNBT list) {
		for (int i = 0; i < list.size(); i++) {
			CompoundNBT itemNBT = list.getCompound(i);
			int slot = itemNBT.getByte(SLOT_TAG) & 0xFF;
			if (isSlotValid(slot)) {
				stacks.set(slot, ItemStack.of(itemNBT));
			}
		}
	}
}
