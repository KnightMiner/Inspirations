package knightminer.inspirations.cauldrons.interaction;

import knightminer.inspirations.library.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;

/** Shared logic for an interaction that consumes an item to change the cauldron */
public abstract class AbstractModifyCauldronInteraction implements CauldronInteraction {
	private final ItemLike container;
	private final SoundEvent sound;
	private final ResourceLocation stat;
	protected AbstractModifyCauldronInteraction(ItemLike container, SoundEvent sound, ResourceLocation stat) {
		this.container = container;
		this.sound = sound;
		this.stat = stat;
	}

	/** Called after the state is set to set TE props */
	protected void afterSetBlock(BlockState oldState, Level level, BlockPos pos, ItemStack stack) {}

	/** Gets the new block state based on the old block state, or null if unable to change */
	@Nullable
	protected abstract BlockState getNewState(BlockState oldState, Level level, BlockPos pos, ItemStack filledStack);

	@Override
	public InteractionResult interact(BlockState oldState, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack filledStack) {
		BlockState newState = getNewState(oldState, level, pos, filledStack);
		if (newState == null) {
			return InteractionResult.PASS;
		}
		if (!level.isClientSide) {
			if (newState != oldState) {
				level.setBlockAndUpdate(pos, newState);
			}
			afterSetBlock(oldState, level, pos, filledStack);
			player.awardStat(stat);
			player.awardStat(Stats.ITEM_USED.get(filledStack.getItem()));

			// if no result, just empty the hand
			if (container == Items.AIR) {
				MiscUtil.shrinkHeldItem(player, hand, filledStack, 1);
			} else {
				player.setItemInHand(hand, ItemUtils.createFilledResult(filledStack, player, new ItemStack(container)));
			}
			level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}
}
