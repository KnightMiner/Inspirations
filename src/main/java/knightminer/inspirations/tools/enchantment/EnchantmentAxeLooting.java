package knightminer.inspirations.tools.enchantment;

import knightminer.inspirations.common.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLootBonus;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;

public class EnchantmentAxeLooting extends EnchantmentLootBonus {
  public EnchantmentAxeLooting(Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot... slots) {
    super(rarityIn, typeIn, slots);
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) {
    return (Config.axeEnchantmentTable && stack.getItem() instanceof ItemAxe) || super.canApplyAtEnchantingTable(stack);
  }

  @Override
  public boolean canApply(ItemStack stack) {
    // fallback in case axes cannot be enchanted at the table, but can receive from books
    return stack.getItem() instanceof ItemAxe || super.canApplyAtEnchantingTable(stack);
  }

  @Override
  public boolean canApplyTogether(Enchantment ench) {
    // boost mob drops or block drops
    return super.canApplyTogether(ench) && ench != Enchantments.FORTUNE;
  }
}
