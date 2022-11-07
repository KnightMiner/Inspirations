package knightminer.inspirations.cauldrons.interaction;

import knightminer.inspirations.cauldrons.block.FourLayerCauldronBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import javax.annotation.Nullable;

/** Fills the cauldron, increasing the layers by one */
public class IncreaseLayerCauldronInteraction extends AbstractModifyCauldronInteraction {
	protected final IntegerProperty prop;
	private final int max;

	public IncreaseLayerCauldronInteraction(ItemLike container, IntegerProperty prop, int max) {
		super(container, SoundEvents.BOTTLE_EMPTY, Stats.FILL_CAULDRON);
		this.prop = prop;
		this.max = max;
	}

	public static IncreaseLayerCauldronInteraction fourLevel(ItemLike container) {
		return new IncreaseLayerCauldronInteraction(container, FourLayerCauldronBlock.LEVEL, 4);
	}

	@Nullable
	@Override
	protected BlockState getNewState(BlockState state, Level level, BlockPos pos, ItemStack filledStack) {
		if (state.getValue(prop) != max) {
			return state.cycle(prop);
		}
		return null;
	}
}
