package knightminer.inspirations.tools.enchantment;

import knightminer.inspirations.common.Config;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.KnockbackEnchantment;
import net.minecraftforge.common.ToolActions;

import java.util.Map;

public class ExtendedKnockbackEnchantment extends KnockbackEnchantment {
  public ExtendedKnockbackEnchantment(Rarity rarityIn, EquipmentSlot... slots) {
    super(rarityIn, slots);
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) {
    Item item = stack.getItem();
    return (Config.moreShieldEnchantments.get() && stack.canPerformAction(ToolActions.SHIELD_BLOCK))
           || (Config.axeEnchantmentTable.get() && Config.axeWeaponEnchants.get() && item instanceof AxeItem)
           || super.canApplyAtEnchantingTable(stack);
  }

  @Override
  public boolean canEnchant(ItemStack stack) {
    // fallback in case axes cannot be enchanted at the table, but can receive from books
    return (Config.axeWeaponEnchants.get() && stack.getItem() instanceof AxeItem) || super.canEnchant(stack);
  }

  @Override
  public Map<EquipmentSlot,ItemStack> getSlotItems(LivingEntity entity) {
    // shields in hand should not give knockback, just on hit
    Map<EquipmentSlot,ItemStack> items = super.getSlotItems(entity);
    for (EquipmentSlot slot : EquipmentSlot.values()) {
      if (items.containsKey(slot) && items.get(slot).canPerformAction(ToolActions.SHIELD_BLOCK)) {
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
