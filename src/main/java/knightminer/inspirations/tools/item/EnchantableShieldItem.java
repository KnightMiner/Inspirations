package knightminer.inspirations.tools.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;

public class EnchantableShieldItem extends ShieldItem {
  public EnchantableShieldItem(Item.Properties props) {
    super(props);
  }

  @Override
  public int getItemEnchantability(ItemStack stack) {
    // small boost to enchantability if it has a banner
    return stack.getChildTag("BlockEntityTag") != null ? 16 : 12;
  }
}
