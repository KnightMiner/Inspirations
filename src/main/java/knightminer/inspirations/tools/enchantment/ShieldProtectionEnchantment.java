package knightminer.inspirations.tools.enchantment;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraftforge.common.ToolActions;

import java.util.Map;

public class ShieldProtectionEnchantment extends ProtectionEnchantment {
  public ShieldProtectionEnchantment(Rarity rarityIn, Type protectionTypeIn, EquipmentSlot... slots) {
    super(rarityIn, protectionTypeIn, slots);
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) {
    return stack.canPerformAction(ToolActions.SHIELD_BLOCK) || super.canApplyAtEnchantingTable(stack);
  }

  @Override
  public Map<EquipmentSlot,ItemStack> getSlotItems(LivingEntity entity) {
    // only include the shield if blocking
    Map<EquipmentSlot,ItemStack> items = super.getSlotItems(entity);
    if (entity.isBlocking()) {
      items.put(
          entity.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND,
          entity.getUseItem()
               );
    }
    return items;
  }
}
