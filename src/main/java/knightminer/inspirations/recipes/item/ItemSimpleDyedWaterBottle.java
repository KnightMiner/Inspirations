package knightminer.inspirations.recipes.item;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.item.HidableItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.items.ItemHandlerHelper;

public class ItemSimpleDyedWaterBottle extends HidableItem {
	private DyeColor color;

	public ItemSimpleDyedWaterBottle(DyeColor color) {
		super(new Item.Properties()
				.group(ItemGroup.MATERIALS)
				.maxStackSize(16)
				.containerItem(Items.GLASS_BOTTLE),
                Config::enableCauldronDyeing
		);
		this.color = color;
	}

	public DyeColor getColor() {
		return color;
	}

	/** Dye sheep on right click with a bottle */
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
		if (target instanceof SheepEntity) {
			SheepEntity sheep = (SheepEntity)target;
			if (!sheep.getSheared() && sheep.getFleeceColor() != color) {
				sheep.setFleeceColor(color);
				player.playSound(SoundEvents.ITEM_BOTTLE_EMPTY, 1.0F, 1.0F);

				// give back bottle;
				ItemStack bottle = new ItemStack(getContainerItem());
				if (stack.getCount() == 1) {
					player.setHeldItem(hand, bottle);
				} else {
					stack.shrink(1);
					ItemHandlerHelper.giveItemToPlayer(player, bottle);
				}
			}
			return true;
		}
		return false;
	}
}
