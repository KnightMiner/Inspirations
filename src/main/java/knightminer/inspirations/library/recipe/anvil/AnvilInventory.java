package knightminer.inspirations.library.recipe.anvil;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Dummy inventory, used to pass along the relevant state when an interaction occurs.
 */
public class AnvilInventory implements IInventory {
	private final List<ItemStack> items;
	private final BlockState state;
	public boolean[] used;

	public AnvilInventory(List<ItemStack> items, BlockState state) {
		this.items = items;
		this.state = state;
		this.used = new boolean[items.size()];
	}

	public BlockState getState() {
		return state;
	}

	public List<ItemStack> getItems() {
		return items;
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int index) {
		return items.get(index);
	}

	@Override
	public int getSizeInventory() {
		return items.size();
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Nonnull
	@Override
	public ItemStack decrStackSize(int index, int count) {
		return ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStackHelper.getAndRemove(items, index);
	}

	@Override
	public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
		items.set(index, stack);
	}

	@Override
	public void markDirty() {

	}

	@Override
	public boolean isUsableByPlayer(@Nonnull PlayerEntity player) {
		return false;
	}

	@Override
	public void clear() {

	}
}
