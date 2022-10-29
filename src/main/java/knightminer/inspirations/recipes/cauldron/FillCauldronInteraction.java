package knightminer.inspirations.recipes.cauldron;

import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.recipes.block.FourLayerCauldronBlock;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

/** Replaces the cauldron with a filled cauldron using the given container */
public record FillCauldronInteraction(BlockState newState, ItemLike container, SoundEvent sound) implements CauldronInteraction {
	public FillCauldronInteraction(FourLayerCauldronBlock block, int level, ItemLike container, SoundEvent sound) {
		this(block.defaultBlockState().setValue(FourLayerCauldronBlock.LEVEL, level), container, sound);
	}

	public FillCauldronInteraction(FourLayerCauldronBlock block, int level, ItemLike container) {
		this(block, level, container, SoundEvents.BUCKET_EMPTY);
	}

	public FillCauldronInteraction(FourLayerCauldronBlock block) {
		this(block, 4, Items.BUCKET);
	}

	@Override
	public InteractionResult interact(BlockState oldState, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack filledStack) {
		if (!level.isClientSide) {
			player.awardStat(Stats.ITEM_USED.get(filledStack.getItem()));
			// if no result, just empty the hand
			if (container == Items.AIR) {
				MiscUtil.shrinkHeldItem(player, hand, filledStack, 1);
			} else {
				player.setItemInHand(hand, ItemUtils.createFilledResult(filledStack, player, new ItemStack(container)));
			}
			player.awardStat(Stats.FILL_CAULDRON);
			level.setBlockAndUpdate(pos, newState);
			level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}
}
