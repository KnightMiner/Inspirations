package knightminer.inspirations.tweaks.item;

import knightminer.inspirations.common.item.HidableBlockItem;
import net.minecraft.block.Block;

public class SeedItem extends HidableBlockItem {

	public SeedItem(Block block, Properties props) {
		super(block, props);
	}

	@Override
	public String getTranslationKey() {
		return getDefaultTranslationKey();
	}
}
