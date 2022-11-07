package knightminer.inspirations.cauldrons.interaction.stew;

import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import knightminer.inspirations.cauldrons.block.entity.SuspiciousStewCauldronBlockEntity;
import knightminer.inspirations.cauldrons.interaction.AbstractModifyCauldronInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import static knightminer.inspirations.cauldrons.block.FourLayerCauldronBlock.LEVEL;

/** Pours a suspicious stew into a cauldron, mixing the effects */
public class MixSuspiciousStewCauldronInteraction extends AbstractModifyCauldronInteraction {
	public static final MixSuspiciousStewCauldronInteraction INSTANCE = new MixSuspiciousStewCauldronInteraction();

	private MixSuspiciousStewCauldronInteraction() {
		super(Items.BOWL, SoundEvents.BOTTLE_EMPTY, Stats.FILL_CAULDRON);
	}

	@Nullable
	@Override
	protected BlockState getNewState(BlockState oldState, Level level, BlockPos pos, ItemStack filledStack) {
		int soupLevel = oldState.getValue(LEVEL);
		if (soupLevel < 4) {
			return oldState.setValue(LEVEL, soupLevel + 1);
		}
		return null;
	}

	@Override
	protected void afterSetBlock(BlockState oldState, Level level, BlockPos pos, ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag != null) {
			ListTag effects = tag.getList(SuspiciousStewItem.EFFECTS_TAG, Tag.TAG_COMPOUND);
			if (!effects.isEmpty()) {
				SuspiciousStewCauldronBlockEntity cauldron = InspirationsCaudrons.suspiciousStewCauldronEntity.getBlockEntity(level, pos);
				if (cauldron != null) {
					cauldron.mergeEffects(oldState.getValue(LEVEL), effects, 1);
				}
			}
		}
	}
}
