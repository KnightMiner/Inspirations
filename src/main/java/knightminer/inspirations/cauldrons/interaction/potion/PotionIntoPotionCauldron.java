package knightminer.inspirations.cauldrons.interaction.potion;

import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import knightminer.inspirations.cauldrons.block.entity.PotionCauldronBlockEntity;
import knightminer.inspirations.cauldrons.interaction.AbstractModifyCauldronInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import static net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL;

/** Logic to dump a potion into an empty cauldron */
public class PotionIntoPotionCauldron extends AbstractModifyCauldronInteraction {
	public PotionIntoPotionCauldron(ItemLike container) {
		super(container, SoundEvents.BOTTLE_EMPTY, Stats.FILL_CAULDRON);
	}

	@Nullable
	@Override
	protected BlockState getNewState(BlockState oldState, Level level, BlockPos pos, ItemStack filledStack) {
		int potionLevel = oldState.getValue(LEVEL);
		if (potionLevel < 4) {
			PotionCauldronBlockEntity cauldron = InspirationsCaudrons.potionCauldronEntity.getBlockEntity(level, pos);
			if (cauldron != null && cauldron.getPotion() == PotionUtils.getPotion(filledStack)) {
				return oldState.setValue(LEVEL, potionLevel + 1);
			}
		}
		return null;
	}
}
