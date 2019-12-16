package knightminer.inspirations.building.item;

import knightminer.inspirations.common.item.TextureBlockItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;

public class BookshelfItem extends TextureBlockItem {
	public BookshelfItem(Block block) {
		super(block, new Item.Properties().group(ItemGroup.DECORATIONS), ItemTags.WOODEN_SLABS);
	}

	@Override
	public int getBurnTime(ItemStack itemStack) {
		return 300;
	}
}
