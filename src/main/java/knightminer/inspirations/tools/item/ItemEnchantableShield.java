package knightminer.inspirations.tools.item;

import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;

public class ItemEnchantableShield extends ItemShield {
  public ItemEnchantableShield() {
    super();
    this.setUnlocalizedName("shield");
  }

  @Override
  public int getItemEnchantability(ItemStack stack) {
    // small boost to enchantability if it has a banner
    return stack.getSubCompound("BlockEntityTag") != null ? 16 : 12;
  }
}
