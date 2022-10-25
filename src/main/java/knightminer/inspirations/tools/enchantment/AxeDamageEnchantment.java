package knightminer.inspirations.tools.enchantment;

import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.enchantment.Enchantment.Rarity;

public class AxeDamageEnchantment extends DamageEnchantment {
  public AxeDamageEnchantment(Rarity rarityIn, int damageTypeIn, EquipmentSlot... slots) {
    super(rarityIn, damageTypeIn, slots);
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) {
    return stack.getItem() instanceof AxeItem || super.canApplyAtEnchantingTable(stack);
  }
}
