package knightminer.inspirations.cauldrons.interaction;

import knightminer.inspirations.cauldrons.block.FourLayerCauldronBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/** Replaces the cauldron with a filled cauldron using the given container */
public class FillCauldronInteraction extends AbstractModifyCauldronInteraction {
	private final BlockState newState;
	public FillCauldronInteraction(BlockState newState, ItemLike container, SoundEvent sound) {
		super(container, sound, Stats.FILL_CAULDRON);
		this.newState = newState;
	}

	public FillCauldronInteraction(FourLayerCauldronBlock block, int level, ItemLike container, SoundEvent sound) {
		this(block.defaultBlockState().setValue(FourLayerCauldronBlock.LEVEL, level), container, sound);
	}

	public FillCauldronInteraction(FourLayerCauldronBlock block, int level, ItemLike container) {
		this(block, level, container, SoundEvents.BUCKET_EMPTY);
	}

	public FillCauldronInteraction(FourLayerCauldronBlock block) {
		this(block, 4, Items.BUCKET);
	}

	@Nullable
	@Override
	protected BlockState getNewState(BlockState oldState, Level level, BlockPos pos, ItemStack filledStack) {
		return newState;
	}
}
