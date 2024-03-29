package knightminer.inspirations.cauldrons.interaction.stew;

import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import knightminer.inspirations.cauldrons.block.entity.SuspiciousStewCauldronBlockEntity;
import knightminer.inspirations.cauldrons.interaction.TransformCauldronInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;

import static knightminer.inspirations.cauldrons.block.BoilingFourLayerCauldronBlock.isBoiling;
import static knightminer.inspirations.cauldrons.block.FourLayerCauldronBlock.LEVEL;

/** Cauldron interaction for brewing in a cauldron */
public enum SuspiciousStewingCauldronInteraction implements CauldronInteraction {
	INSTANCE;

	@Override
	public InteractionResult interact(BlockState oldState, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
		int soupLevel = oldState.getValue(LEVEL);
		int itemsNeeded = TransformCauldronInteraction.scaleAmountNeeded(soupLevel);

		// find the flower
		if (stack.getCount() >= itemsNeeded && isBoiling(level, pos)
				&& stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof FlowerBlock flower) {

			if (!level.isClientSide) {
				// set the block and effect
				level.setBlockAndUpdate(pos, InspirationsCaudrons.suspiciousStewCauldron.defaultBlockState().setValue(LEVEL, oldState.getValue(LEVEL)));
				SuspiciousStewCauldronBlockEntity cauldron = InspirationsCaudrons.suspiciousStewCauldronEntity.getBlockEntity(level, pos);
				if (cauldron != null) {
					cauldron.addEffect(flower.getSuspiciousStewEffect(), flower.getEffectDuration());
				}

				// consume items, update stats
				MiscUtil.shrinkHeldItem(player, hand, stack, itemsNeeded);
				player.awardStat(Stats.USE_CAULDRON);
				level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		return InteractionResult.PASS;
	}
}
