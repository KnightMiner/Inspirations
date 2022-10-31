package knightminer.inspirations.recipes.cauldron;

import knightminer.inspirations.library.InspirationsTags;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/** Transforms a cauldron into another cauldron */
public record TransformCauldronInteraction(boolean requireFire, int needed, IntegerProperty oldProp, FourLayerCauldronBlock block, SoundEvent sound) implements CauldronInteraction {
	public TransformCauldronInteraction(boolean requireFire, int needed, IntegerProperty oldProp, FourLayerCauldronBlock block) {
		this(requireFire, needed, oldProp, block, SoundEvents.BREWING_STAND_BREW);
	}

	/** Scales the amount needed */
	public static int scaleAmountNeeded(int needed) {
		// charge for a 4 layer cauldron as if it were 3 layer, that is 75% of cost, ceiling
		return (needed + 1) * 3 / 4;
	}

	@Override
	public InteractionResult interact(BlockState state, Level level, BlockPos pos, Player player, InteractionHand pHand, ItemStack stack) {
		if (requireFire && !level.getBlockState(pos.below()).is(InspirationsTags.Blocks.CAULDRON_FIRE)) {
			return InteractionResult.PASS;
		}
		if (!level.isClientSide) {
			int contentLevel = state.getValue(oldProp);
			int needed = this.needed * contentLevel;
			// for a 3 layer cauldron, treat 3 levels as 4 levels
			if (oldProp == LayeredCauldronBlock.LEVEL) {
				if (contentLevel == 3) {
					contentLevel = 4;
				}
			} else {
				needed = scaleAmountNeeded(needed);
			}
			if (stack.getCount() >= needed) {
				player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
				MiscUtil.shrinkHeldItem(player, pHand, stack, needed);
				player.awardStat(Stats.USE_CAULDRON);
				level.setBlockAndUpdate(pos, block.defaultBlockState().setValue(FourLayerCauldronBlock.LEVEL, contentLevel));
				level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
			}
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}
}
