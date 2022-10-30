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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;

/** Fills the cauldron, increasing the layers by one */
public class IncreaseLayerCauldronInteraction implements CauldronInteraction {
	private final ItemLike container;
	protected final IntegerProperty prop;
	private final int max;

	public IncreaseLayerCauldronInteraction(ItemLike container, IntegerProperty prop, int max) {
		this.container = container;
		this.prop = prop;
		this.max = max;
	}

	public static IncreaseLayerCauldronInteraction fourLevel(ItemLike container) {
		return new IncreaseLayerCauldronInteraction(container, FourLayerCauldronBlock.LEVEL, 4);
	}

	/** Called after the block is set */
	protected void afterSetBlock(BlockState state, Level level, BlockPos pos, ItemStack stack) {}

	@Override
	public InteractionResult interact(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
		if (state.getValue(prop) != max) {
			if (!level.isClientSide) {
				level.setBlockAndUpdate(pos, state.cycle(prop));
				afterSetBlock(state, level, pos, stack);
				player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(container)));
				player.awardStat(Stats.USE_CAULDRON);
				level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		return InteractionResult.PASS;
	}
}
