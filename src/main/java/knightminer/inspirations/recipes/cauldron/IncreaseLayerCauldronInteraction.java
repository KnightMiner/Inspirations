package knightminer.inspirations.recipes.cauldron;

import knightminer.inspirations.recipes.block.FourLayerCauldronBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

/** Fills the cauldron, increasing the layers by one */
public record IncreaseLayerCauldronInteraction(ItemLike container) implements CauldronInteraction {
	@Override
	public InteractionResult interact(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
		if (state.getValue(FourLayerCauldronBlock.LEVEL) != 4) {
			if (!level.isClientSide) {
				player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(container)));
				player.awardStat(Stats.USE_CAULDRON);
				level.setBlockAndUpdate(pos, state.cycle(FourLayerCauldronBlock.LEVEL));
				level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		return InteractionResult.PASS;
	}
}
