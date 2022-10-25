package knightminer.inspirations.tools.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ThornsEnchantment;
import net.minecraftforge.common.ToolActions;

public class ShieldThornsEnchantment extends ThornsEnchantment {
  public ShieldThornsEnchantment(Rarity rarityIn, EquipmentSlot... slots) {
    super(rarityIn, slots);
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) {
    return stack.canPerformAction(ToolActions.SHIELD_BLOCK) || super.canApplyAtEnchantingTable(stack);
  }

  @Override
  public boolean checkCompatibility(Enchantment ench) {
    // thorns or fire, never needed this choice before
    return super.checkCompatibility(ench) && ench != Enchantments.FIRE_ASPECT;
  }
}
