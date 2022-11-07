package knightminer.inspirations.cauldrons.interaction.dye;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/** Logic to dump a water bottle into a dye cauldron, dilutes the color */
public class WaterBottleIntoDyeCauldronInteraction extends DyedBottleIntoDyeCauldronInteraction {
	public WaterBottleIntoDyeCauldronInteraction(ItemLike bottle, @Nullable Integer color) {
		super(bottle, color);
	}

	@Nullable
	@Override
	protected BlockState getNewState(BlockState state, Level level, BlockPos pos, ItemStack filledStack) {
		if (PotionUtils.getPotion(filledStack) == Potions.WATER) {
			return super.getNewState(state, level, pos, filledStack);
		}
		return null;
	}
}
