package knightminer.inspirations.tools.enchantment;

import knightminer.inspirations.common.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.FireAspectEnchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Map;

public class EnchantmentExtendedFire extends FireAspectEnchantment {
	public EnchantmentExtendedFire(Rarity rarity, EquipmentSlotType... slots) {
		super(rarity, slots);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		Item item = stack.getItem();
		return (Config.moreShieldEnchantments.get() && item.isShield(stack, null))
				|| (Config.axeEnchantmentTable.get() && Config.axeWeaponEnchants.get() && item instanceof AxeItem)
				|| super.canApplyAtEnchantingTable(stack);
	}

	@Override
	public boolean canApply(ItemStack stack) {
		// fallback in case axes cannot be enchanted at the table, but can receive from books
		return (Config.axeWeaponEnchants.get() && stack.getItem() instanceof AxeItem) || super.canApply(stack);
	}

	@Nonnull
	@Override
	public Map<EquipmentSlotType, ItemStack> getEntityEquipment(@Nonnull LivingEntity entity) {
		// shields in hand should not give knockback, just on hit
		Map<EquipmentSlotType, ItemStack> items = super.getEntityEquipment(entity);
		for (EquipmentSlotType slot : EquipmentSlotType.values()) {
			if (items.containsKey(slot) && items.get(slot).isShield(entity)) {
				items.put(slot, ItemStack.EMPTY);
			}
		}
		return items;
	}

	@Override
	public boolean canApplyTogether(Enchantment ench) {
		// thorns or fire, and fire or efficiency
		return super.canApplyTogether(ench) && ench != Enchantments.THORNS && ench != Enchantments.EFFICIENCY;
	}
}
