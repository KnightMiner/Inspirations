package knightminer.inspirations.recipes.cauldron.potion;

import knightminer.inspirations.recipes.cauldron.AbstractModifyCauldronInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/** Fills a water cauldron with a water potion item */
public class WaterBottleIntoWaterInteraction extends AbstractModifyCauldronInteraction {
	public WaterBottleIntoWaterInteraction(ItemLike container) {
		super(container, SoundEvents.BOTTLE_EMPTY, Stats.FILL_CAULDRON);
	}

	@Nullable
	@Override
	protected BlockState getNewState(BlockState state, Level level, BlockPos pos, ItemStack filledStack) {
		int waterLevel = state.getValue(LayeredCauldronBlock.LEVEL);
		if (waterLevel < 3 && PotionUtils.getPotion(filledStack) == Potions.WATER) {
			return state.setValue(LayeredCauldronBlock.LEVEL, waterLevel + 1);
		}
		return null;
	}
}
