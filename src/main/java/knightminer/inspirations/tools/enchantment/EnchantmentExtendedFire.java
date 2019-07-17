package knightminer.inspirations.tools.enchantment;

import knightminer.inspirations.common.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentFireAspect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;

import java.util.List;

public class EnchantmentExtendedFire extends EnchantmentFireAspect {
  public EnchantmentExtendedFire(Rarity rarityIn, EntityEquipmentSlot... slots) {
    super(rarityIn, slots);
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) {
    Item item = stack.getItem();
    return (Config.moreShieldEnchantments && item instanceof ItemShield)
           || (Config.axeEnchantmentTable && Config.axeWeaponEnchants && item instanceof ItemAxe)
           || super.canApplyAtEnchantingTable(stack);
  }

  @Override
  public boolean canApply(ItemStack stack) {
    // fallback in case axes cannot be enchanted at the table, but can receive from books
    return (Config.axeWeaponEnchants && stack.getItem() instanceof ItemAxe) || super.canApply(stack);
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
    // thorns or fire, and fire or efficiency
    return super.canApplyTogether(ench) && ench != Enchantments.THORNS && ench != Enchantments.EFFICIENCY;
  }
}
