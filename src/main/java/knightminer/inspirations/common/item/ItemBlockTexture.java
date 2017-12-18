package knightminer.inspirations.common.item;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import knightminer.inspirations.library.util.RecipeUtil;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import slimeknights.mantle.item.ItemBlockMeta;

public class ItemBlockTexture extends ItemBlockMeta {
	public ItemBlockTexture(Block block) {
		super(block);
	}

	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		if(!stack.hasTagCompound()) {
			return;
		}

		ItemStack texture = RecipeUtil.getStackTexture(stack);
		if(!texture.isEmpty()) {
			tooltip.add(texture.getDisplayName());
		}
	}
}
