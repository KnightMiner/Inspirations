package knightminer.inspirations.tools.enchantment;

import knightminer.inspirations.common.Config;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LootBonusEnchantment;

public class AxeLootBonusEnchantment extends LootBonusEnchantment {
  public AxeLootBonusEnchantment(Rarity rarityIn, EnchantmentCategory typeIn, EquipmentSlot... slots) {
    super(rarityIn, typeIn, slots);
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) {
    return (Config.axeEnchantmentTable.getAsBoolean() && stack.getItem() instanceof AxeItem) || super.canApplyAtEnchantingTable(stack);
  }

  @Override
  public boolean canEnchant(ItemStack stack) {
    // fallback in case axes cannot be enchanted at the table, but can receive from books
    return stack.getItem() instanceof AxeItem || super.canApplyAtEnchantingTable(stack);
  }

  @Override
  public boolean checkCompatibility(Enchantment ench) {
    // boost mob drops or block drops
    return super.checkCompatibility(ench) && ench != Enchantments.BLOCK_FORTUNE;
  }
}
