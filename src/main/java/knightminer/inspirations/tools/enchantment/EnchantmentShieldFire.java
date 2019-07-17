package knightminer.inspirations.tools.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentFireAspect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;

import java.util.List;

public class EnchantmentShieldFire extends EnchantmentFireAspect {
  public EnchantmentShieldFire(Rarity rarityIn, EntityEquipmentSlot... slots) {
    super(rarityIn, slots);
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) {
    return stack.getItem() instanceof ItemShield || super.canApplyAtEnchantingTable(stack);
  }

  @Override
  public List<ItemStack> getEntityEquipment(EntityLivingBase entity) {
    // shields in hand should not give fire, just on hit
    List<ItemStack> items = super.getEntityEquipment(entity);
    items.removeIf((stack) -> stack.getItem() instanceof ItemShield);
    return items;
  }

  @Override
  public boolean canApplyTogether(Enchantment ench) {
    // thorns or fire, never needed this choice before
    return super.canApplyTogether(ench) && ench != Enchantments.THORNS;
  }
}
