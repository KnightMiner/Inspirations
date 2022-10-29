package knightminer.inspirations.recipes.cauldron;

import knightminer.inspirations.library.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;

/** Decreases the cauldron by one layer, returning the given result */
public record DecreaseLayerCauldronInteraction(ItemStack result, IntegerProperty prop, boolean consumeInput, SoundEvent sound) implements CauldronInteraction {
	public DecreaseLayerCauldronInteraction(ItemLike result, IntegerProperty prop, SoundEvent sound) {
		this(new ItemStack(result), prop, true, sound);
	}

	public DecreaseLayerCauldronInteraction(ItemLike result, IntegerProperty prop) {
		this(result, prop, SoundEvents.BOTTLE_FILL);
	}

	@Override
	public InteractionResult interact(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
		if (!level.isClientSide) {
			player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
			if (stack.isEmpty()) {
				player.setItemInHand(hand, result.copy());
			} else if (consumeInput) {
				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, result.copy()));
			} else {
				MiscUtil.givePlayerItem(player, result.copy());
			}
			player.awardStat(Stats.USE_CAULDRON);
			int oldLevel = state.getValue(prop);
			if (oldLevel == 1) {
				level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
			} else {
				level.setBlockAndUpdate(pos, state.setValue(prop, oldLevel - 1));
			}
			level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}
}
