package knightminer.inspirations.cauldrons.interaction.stew;

import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import knightminer.inspirations.cauldrons.block.entity.SuspiciousStewCauldronBlockEntity;
import knightminer.inspirations.cauldrons.interaction.FillCauldronInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Logic to pour stew into an empty cauldron */
public class SuspiciousStewIntoEmptyCauldronInteraction extends FillCauldronInteraction {
	public static final SuspiciousStewIntoEmptyCauldronInteraction INSTANCE = new SuspiciousStewIntoEmptyCauldronInteraction();
	private SuspiciousStewIntoEmptyCauldronInteraction() {
		super(InspirationsCaudrons.suspiciousStewCauldron, 1, Items.BOWL, SoundEvents.BOTTLE_EMPTY);
	}

	@Override
	protected void afterSetBlock(BlockState oldState, Level level, BlockPos pos, ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag != null) {
			ListTag effects = tag.getList(SuspiciousStewItem.EFFECTS_TAG, Tag.TAG_COMPOUND);
			if (!effects.isEmpty()) {
				SuspiciousStewCauldronBlockEntity cauldron = InspirationsCaudrons.suspiciousStewCauldronEntity.getBlockEntity(level, pos);
				if (cauldron != null) {
					cauldron.setEffects(effects);
				}
			}
		}
	}
}
