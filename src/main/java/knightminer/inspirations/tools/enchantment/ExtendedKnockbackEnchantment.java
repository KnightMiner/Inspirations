package knightminer.inspirations.tools.enchantment;

import knightminer.inspirations.common.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.KnockbackEnchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Map;

import net.minecraft.enchantment.Enchantment.Rarity;

public class ExtendedKnockbackEnchantment extends KnockbackEnchantment {
  public ExtendedKnockbackEnchantment(Rarity rarityIn, EquipmentSlotType... slots) {
    super(rarityIn, slots);
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) {
    Item item = stack.getItem();
    return (Config.moreShieldEnchantments.get() && stack.isShield(null))
           || (Config.axeEnchantmentTable.get() && Config.axeWeaponEnchants.get() && item instanceof AxeItem)
           || super.canApplyAtEnchantingTable(stack);
  }

  @Override
  public boolean canEnchant(ItemStack stack) {
    // fallback in case axes cannot be enchanted at the table, but can receive from books
    return (Config.axeWeaponEnchants.get() && stack.getItem() instanceof AxeItem) || super.canEnchant(stack);
  }

  @Override
  public Map<EquipmentSlotType,ItemStack> getSlotItems(LivingEntity entity) {
    // shields in hand should not give knockback, just on hit
    Map<EquipmentSlotType,ItemStack> items = super.getSlotItems(entity);
    for (EquipmentSlotType slot : EquipmentSlotType.values()) {
      if (items.containsKey(slot) && items.get(slot).isShield(entity)) {
        items.put(slot, ItemStack.EMPTY);
      }
    }
    return items;
  }

  @Override
  public boolean checkCompatibility(Enchantment ench) {
    // no efficiency and knockback
    return super.checkCompatibility(ench) && ench != Enchantments.BLOCK_EFFICIENCY;
  }
}
