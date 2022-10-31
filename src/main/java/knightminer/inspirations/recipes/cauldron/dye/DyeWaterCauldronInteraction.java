package knightminer.inspirations.recipes.cauldron.dye;

import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.recipes.block.entity.DyeCauldronBlockEntity;
import knightminer.inspirations.recipes.cauldron.AbstractModifyCauldronInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import static net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL;

/** Dyes a water cauldron into a dyed cauldron */
public class DyeWaterCauldronInteraction extends AbstractModifyCauldronInteraction {
	private final int color;

	public DyeWaterCauldronInteraction(int color) {
		super(Items.AIR, SoundEvents.GENERIC_SPLASH, Stats.USE_CAULDRON);
		this.color = color;
	}

	public DyeWaterCauldronInteraction(DyeColor dye) {
		this(MiscUtil.getColor(dye));
	}

	@Nullable
	@Override
	protected BlockState getNewState(BlockState oldState, Level level, BlockPos pos, ItemStack filledStack) {
		return InspirationsRecipes.dyeCauldron.defaultBlockState().setValue(LEVEL, oldState.getValue(LEVEL));
	}

	@Override
	protected void afterSetBlock(BlockState oldState, Level level, BlockPos pos, ItemStack stack) {
		DyeCauldronBlockEntity cauldron = InspirationsRecipes.dyeCauldronEntity.getBlockEntity(level, pos);
		if (cauldron != null) {
			cauldron.setColor(this.color);
		}
	}
}
