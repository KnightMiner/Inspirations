package knightminer.inspirations.tools.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.ThornsEnchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.ItemStack;

public class EnchantmentShieldThorns extends ThornsEnchantment {
  public EnchantmentShieldThorns(Rarity rarityIn, EquipmentSlotType... slots) {
    super(rarityIn, slots);
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) {
    return stack.getItem() instanceof ShieldItem || super.canApplyAtEnchantingTable(stack);
  }

  @Override
  public boolean canApplyTogether(Enchantment ench) {
    // thorns or fire, never needed this choice before
    return super.canApplyTogether(ench) && ench != Enchantments.FIRE_ASPECT;
  }
}
