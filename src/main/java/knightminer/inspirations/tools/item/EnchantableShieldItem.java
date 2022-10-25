package knightminer.inspirations.tools.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

public class EnchantableShieldItem extends ShieldItem {
  public EnchantableShieldItem(Item.Properties props) {
    super(props);
  }

  @Override
  public int getItemEnchantability(ItemStack stack) {
    // small boost to enchantability if it has a banner
    return stack.getTagElement("BlockEntityTag") != null ? 16 : 12;
  }
}
