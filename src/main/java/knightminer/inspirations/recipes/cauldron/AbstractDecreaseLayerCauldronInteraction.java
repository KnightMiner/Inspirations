package knightminer.inspirations.recipes.cauldron;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;

/** Shared logic for recipes that decrease the cauldron level */
public abstract class AbstractDecreaseLayerCauldronInteraction implements CauldronInteraction {
	private final IntegerProperty prop;
	private final boolean consumeInput;
	private final SoundEvent sound;

	protected AbstractDecreaseLayerCauldronInteraction(IntegerProperty prop, boolean consumeInput, SoundEvent sound) {
		this.prop = prop;
		this.consumeInput = consumeInput;
		this.sound = sound;
	}

	/** Gets the result for the given recipe */
	protected abstract ItemStack getResult(BlockState state, Level level, BlockPos pos, ItemStack stack);

	@Override
	public InteractionResult interact(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
		ItemStack result = getResult(state, level, pos, stack);
		if (result.isEmpty()) {
			return InteractionResult.PASS;
		}

		if (!level.isClientSide) {
			player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
			if (stack.isEmpty()) {
				player.setItemInHand(hand, result);
			} else if (consumeInput) {
				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, result));
			} else {
				MiscUtil.givePlayerItem(player, result);
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
