package knightminer.inspirations.tools.enchantment;

import knightminer.inspirations.common.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.LootBonusEnchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;

public class EnchantmentAxeLooting extends LootBonusEnchantment {
  public EnchantmentAxeLooting(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType... slots) {
    super(rarityIn, typeIn, slots);
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) {
    return (Config.axeEnchantmentTable.get() && stack.getItem() instanceof AxeItem) || super.canApplyAtEnchantingTable(stack);
  }

  @Override
  public boolean canApply(ItemStack stack) {
    // fallback in case axes cannot be enchanted at the table, but can receive from books
    return stack.getItem() instanceof AxeItem || super.canApplyAtEnchantingTable(stack);
  }

  @Override
  public boolean canApplyTogether(Enchantment ench) {
    // boost mob drops or block drops
    return super.canApplyTogether(ench) && ench != Enchantments.FORTUNE;
  }
}
