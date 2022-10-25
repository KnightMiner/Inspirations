package knightminer.inspirations.common.item;

import knightminer.inspirations.common.Config;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.item.RetexturedBlockItem;

import javax.annotation.Nullable;
import java.util.List;

public class HidableRetexturedBlockItem extends HidableBlockItem {
  private final ITag<Item> textureTag;

  public HidableRetexturedBlockItem(Block block, ITag<Item> textureTag, BlockItem.Properties props) {
    super(block, props);
    this.textureTag = textureTag;
  }

  @Override
  public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
    if (shouldAddtoItemGroup(group) && allowdedIn(group)) {
      RetexturedBlockItem.addTagVariants(this.getBlock(), textureTag, items, Config.showAllVariants.get());
    }
  }

  @OnlyIn(Dist.CLIENT)
  @Override
  public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    RetexturedBlockItem.addTooltip(stack, tooltip);
    super.appendHoverText(stack, worldIn, tooltip, flagIn);
  }
}
