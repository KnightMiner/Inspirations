package knightminer.inspirations.library;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemMetaKey {
	private Item item;
	private int meta;

	public ItemMetaKey(ItemStack stack) {
		this.item = stack.getItem();
		this.meta = stack.getMetadata();
	}

	public ItemMetaKey(Item item, int meta) {
		this.item = item;
		this.meta = meta;
	}

	public Item getItem() {
		return item;
	}

	public int getMeta() {
		return meta;
	}

	public ItemStack makeItemStack() {
		return new ItemStack(item, 1, meta);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}

		if(this.getClass() != other.getClass()) {
			return false;
		}

		ItemMetaKey otherKey = (ItemMetaKey) other;
		return otherKey.item == this.item && otherKey.meta == this.meta;
	}

	@Override
	public int hashCode() {
		int result = item != null ? item.hashCode() : 0;
		result = 31 * result + meta;
		return result;
	}
}
