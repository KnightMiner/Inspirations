package knightminer.inspirations.tools.enchantment;

import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.Map;

public class ShieldProtectionEnchantment extends ProtectionEnchantment {
  public ShieldProtectionEnchantment(Rarity rarityIn, Type protectionTypeIn, EquipmentSlotType... slots) {
    super(rarityIn, protectionTypeIn, slots);
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) {
    return stack.isShield(null) || super.canApplyAtEnchantingTable(stack);
  }

  @Override
  public Map<EquipmentSlotType,ItemStack> getEntityEquipment(LivingEntity entity) {
    // only include the shield if blocking
    Map<EquipmentSlotType,ItemStack> items = super.getEntityEquipment(entity);
    if (entity.isActiveItemStackBlocking()) {
      items.put(
          entity.getActiveHand() == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND,
          entity.getActiveItemStack()
               );
    }
    return items;
  }
}
