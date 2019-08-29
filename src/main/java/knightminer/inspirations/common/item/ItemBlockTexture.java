package knightminer.inspirations.common.item;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class ItemBlockTexture extends HidableBlockItem {

	private final Tag<Block> texTag;

	public ItemBlockTexture(Block block, BlockItem.Properties props, Tag<Block> texTag) {
		super(block, props);
		this.texTag = texTag;
	}

	@Override
	public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
		if (shouldAddtoItemGroup(group) && isInGroup(group)) {
			for(Block texture: texTag.getAllElements()) {
				items.add(TextureBlockUtil.createTexturedStack(getBlock(), texture));
			}
		}
	}

	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		if(!stack.hasTag()) {
			return;
		}

		ItemStack texture = TextureBlockUtil.getStackTexture(stack);
		if(!texture.isEmpty()) {
			tooltip.add(texture.getDisplayName());
		}
	}
}
