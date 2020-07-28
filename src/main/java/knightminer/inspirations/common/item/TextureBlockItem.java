package knightminer.inspirations.common.item;

import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TextureBlockItem extends HidableBlockItem {

	private final ITag<Item> texTag;

	public TextureBlockItem(Block block, BlockItem.Properties props, ITag<Item> texTag) {
		super(block, props);
		this.texTag = texTag;
	}

	@Override
	public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
		if (shouldAddtoItemGroup(group) && isInGroup(group)) {
			TextureBlockUtil.addBlocksFromTag(this.getBlock(), texTag, items);
		}
	}

	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		if(!stack.hasTag()) {
			return;
		}

		Block block = TextureBlockUtil.getTextureBlock(stack);
		if(block != Blocks.AIR) {
			tooltip.add(block.getTranslatedName());
		}
	}
}
