package knightminer.inspirations.tools.enchantment;

import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;

import java.util.List;

public class EnchantmentShieldProtection extends EnchantmentProtection {
  public EnchantmentShieldProtection(Rarity rarityIn, Type protectionTypeIn, EntityEquipmentSlot... slots) {
    super(rarityIn, protectionTypeIn, slots);
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) {
    return stack.getItem() instanceof ItemShield || super.canApplyAtEnchantingTable(stack);
  }

  @Override
  public List<ItemStack> getEntityEquipment(EntityLivingBase entity) {
    // only include the shield if blocking
    List<ItemStack> list = super.getEntityEquipment(entity);
    if (entity.isActiveItemStackBlocking()) {
      list.add(entity.getActiveItemStack());
    }
    return list;
  }
}
