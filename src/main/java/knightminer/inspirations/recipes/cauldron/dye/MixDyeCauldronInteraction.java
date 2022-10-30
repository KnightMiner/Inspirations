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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/** Dye the dyed water in a cauldron to get a mixed color */
public class MixDyeCauldronInteraction extends AbstractModifyCauldronInteraction {
	private final int color;

	public MixDyeCauldronInteraction(int color) {
		super(Items.AIR, SoundEvents.GENERIC_SPLASH, Stats.USE_CAULDRON);
		this.color = color;
	}

	public MixDyeCauldronInteraction(DyeColor dye) {
		this(MiscUtil.getColor(dye));
	}

	@Nullable
	@Override
	protected BlockState getNewState(BlockState oldState, Level level, BlockPos pos, ItemStack filledStack) {
		return oldState;
	}

	@Override
	protected void afterSetBlock(BlockState oldState, Level level, BlockPos pos, ItemStack stack) {
		BlockEntity be = level.getBlockEntity(pos);
		if (be != null && be.getType() == InspirationsRecipes.dyeCauldronEntity) {
			DyeCauldronBlockEntity cauldron = (DyeCauldronBlockEntity)be;
			cauldron.setColor(MiscUtil.addColors(this.color, 1, cauldron.getColor(), 1));
		}
	}
}
