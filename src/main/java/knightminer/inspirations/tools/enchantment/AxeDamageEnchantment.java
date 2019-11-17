package knightminer.inspirations.tools.enchantment;

import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;

public class AxeDamageEnchantment extends DamageEnchantment {
  public AxeDamageEnchantment(Rarity rarityIn, int damageTypeIn, EquipmentSlotType... slots) {
    super(rarityIn, damageTypeIn, slots);
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) {
    return stack.getItem() instanceof AxeItem || super.canApplyAtEnchantingTable(stack);
  }
}
