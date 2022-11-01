package knightminer.inspirations.recipes.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Random;

public class MilkBottleItem extends Item {
	private static final Random RANDOM = new Random();
	private static final int DRINK_DURATION = 32;
	public MilkBottleItem(Properties props) {
		super(props);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
		if (living instanceof ServerPlayer player) {
			CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);
			player.awardStat(Stats.ITEM_USED.get(this));
		}

		// remove random effect
		if (!level.isClientSide) {
			ItemStack milkBucket = new ItemStack(Items.MILK_BUCKET);
			List<MobEffect> effects = living.getActiveEffects()
																			.stream()
																			.filter(effect -> effect.isCurativeItem(milkBucket))
																			.map(MobEffectInstance::getEffect)
																			.toList();
			if (!effects.isEmpty()) {
				living.removeEffect(effects.get(RANDOM.nextInt(effects.size())));
			}
		}

		// shrink the stack and return the container
		ItemStack container = stack.getContainerItem().copy();
		if (living instanceof Player player) {
			return ItemUtils.createFilledResult(stack, player, container);
		} else {
			stack.shrink(1);
			if (stack.isEmpty()) {
				return container;
			} else {
				living.spawnAtLocation(container);
				return stack;
			}
		}
	}

	@Override
	public int getUseDuration(ItemStack pStack) {
		return DRINK_DURATION;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack pStack) {
		return UseAnim.DRINK;
	}

	@Override
	public SoundEvent getDrinkingSound() {
		return SoundEvents.GENERIC_DRINK;
	}

	@Override
	public SoundEvent getEatingSound() {
		return SoundEvents.GENERIC_DRINK;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
		return ItemUtils.startUsingInstantly(pLevel, pPlayer, pHand);
	}
}
