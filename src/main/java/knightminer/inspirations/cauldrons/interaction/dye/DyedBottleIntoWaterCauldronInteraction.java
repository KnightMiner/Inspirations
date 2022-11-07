package knightminer.inspirations.cauldrons.interaction.dye;

import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import knightminer.inspirations.cauldrons.block.entity.DyeCauldronBlockEntity;
import knightminer.inspirations.cauldrons.interaction.AbstractModifyCauldronInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import static net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL;

/** Adds dye to a water cauldron */
public class DyedBottleIntoWaterCauldronInteraction extends AbstractModifyCauldronInteraction {
	@Nullable
	private final Integer color;
	public DyedBottleIntoWaterCauldronInteraction(@Nullable Integer color) {
		super(Items.GLASS_BOTTLE, SoundEvents.BOTTLE_EMPTY, Stats.FILL_CAULDRON);
		this.color = color;
	}

	@Nullable
	@Override
	protected BlockState getNewState(BlockState oldState, Level level, BlockPos pos, ItemStack filledStack) {
		int contentLevel = oldState.getValue(LEVEL);
		if (contentLevel == 3) {
			return null;
		}
		return  InspirationsCaudrons.dyeCauldron.defaultBlockState().setValue(LEVEL, contentLevel + 1);
	}

	@Override
	protected void afterSetBlock(BlockState oldState, Level level, BlockPos pos, ItemStack stack) {
		DyeCauldronBlockEntity cauldron = InspirationsCaudrons.dyeCauldronEntity.getBlockEntity(level, pos);
		if (cauldron != null) {
			int newColor = this.color != null ? this.color : MiscUtil.getColor(stack);
			cauldron.setColor(MiscUtil.addColors(newColor, 1, 0x808080, oldState.getValue(LEVEL)));
		}
	}
}
