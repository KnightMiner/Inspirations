package knightminer.inspirations.tools.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentThorns;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;

public class EnchantmentShieldThorns extends EnchantmentThorns {
  public EnchantmentShieldThorns(Rarity rarityIn, EntityEquipmentSlot... slots) {
    super(rarityIn, slots);
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) {
    return stack.getItem() instanceof ItemShield || super.canApplyAtEnchantingTable(stack);
  }

  @Override
  public boolean canApplyTogether(Enchantment ench) {
    // thorns or fire, never needed this choice before
    return super.canApplyTogether(ench) && ench != Enchantments.FIRE_ASPECT;
  }
}
