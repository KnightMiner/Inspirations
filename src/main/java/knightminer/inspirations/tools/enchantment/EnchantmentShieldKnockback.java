package knightminer.inspirations.tools.enchantment;

import net.minecraft.enchantment.EnchantmentKnockback;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;

import java.util.List;

public class EnchantmentShieldKnockback extends EnchantmentKnockback {
  public EnchantmentShieldKnockback(Rarity rarityIn, EntityEquipmentSlot... slots) {
    super(rarityIn, slots);
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) {
    return stack.getItem() instanceof ItemShield || super.canApplyAtEnchantingTable(stack);
  }

  @Override
  public List<ItemStack> getEntityEquipment(EntityLivingBase entity) {
    // shields in hand should not give knockback, just on hit
    List<ItemStack> items = super.getEntityEquipment(entity);
    items.removeIf((stack) -> stack.getItem() instanceof ItemShield);
    return items;
  }
}
