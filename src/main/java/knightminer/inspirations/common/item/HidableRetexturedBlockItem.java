package knightminer.inspirations.common.item;

import knightminer.inspirations.common.Config;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.item.RetexturedBlockItem;

import javax.annotation.Nullable;
import java.util.List;

public class HidableRetexturedBlockItem extends HidableBlockItem {
  private final TagKey<Item> textureTag;

  public HidableRetexturedBlockItem(Block block, TagKey<Item> textureTag, BlockItem.Properties props) {
    super(block, props);
    this.textureTag = textureTag;
  }

  @Override
  public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
    if (shouldAddtoItemGroup(group) && allowdedIn(group)) {
      RetexturedBlockItem.addTagVariants(this.getBlock(), textureTag, items, Config.showAllVariants.get());
    }
  }

  @OnlyIn(Dist.CLIENT)
  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    RetexturedBlockItem.addTooltip(stack, tooltip);
    super.appendHoverText(stack, worldIn, tooltip, flagIn);
  }
}
