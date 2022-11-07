package knightminer.inspirations.cauldrons.interaction;

import knightminer.inspirations.library.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

/** Empties the cauldron and returns a filled stack */
public record EmptyCauldronInteraction(ItemLike filled, boolean consumeInput, SoundEvent fillSound) implements CauldronInteraction {
	public EmptyCauldronInteraction(ItemLike filled, SoundEvent fillSound) {
		this(filled, true, fillSound);
	}

	@Override
	public InteractionResult interact(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack emptyStack) {
		if (!(state.getBlock() instanceof AbstractCauldronBlock cauldron) || cauldron.isFull(state)) {
			if (!level.isClientSide) {
				if (emptyStack.isEmpty()) {
					player.setItemInHand(hand, new ItemStack(filled));
				} else if (consumeInput) {
					player.awardStat(Stats.ITEM_USED.get(emptyStack.getItem()));
					player.setItemInHand(hand, ItemUtils.createFilledResult(emptyStack, player, new ItemStack(filled)));
				} else {
					MiscUtil.givePlayerItem(player, new ItemStack(filled));
				}
				player.awardStat(Stats.USE_CAULDRON);
				level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
				level.playSound(null, pos, fillSound, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		return InteractionResult.PASS;
	}
}
