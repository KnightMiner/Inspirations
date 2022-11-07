package knightminer.inspirations.cauldrons.interaction.stew;

import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import knightminer.inspirations.cauldrons.block.FourLayerCauldronBlock;
import knightminer.inspirations.cauldrons.block.entity.SuspiciousStewCauldronBlockEntity;
import knightminer.inspirations.cauldrons.interaction.AbstractDecreaseLayerCauldronInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Fills a bowl with stew */
public class DecreaseSuspiciousStewCauldronInteraction extends AbstractDecreaseLayerCauldronInteraction {
	public static final DecreaseSuspiciousStewCauldronInteraction INSTANCE = new DecreaseSuspiciousStewCauldronInteraction();
	private DecreaseSuspiciousStewCauldronInteraction() {
		super(FourLayerCauldronBlock.LEVEL, true, SoundEvents.BOTTLE_FILL);
	}

	@Override
	protected ItemStack getResult(BlockState state, Level level, BlockPos pos, ItemStack stack) {
		SuspiciousStewCauldronBlockEntity cauldron = InspirationsCaudrons.suspiciousStewCauldronEntity.getBlockEntity(level, pos);
		if (cauldron != null) {
			ItemStack result = new ItemStack(Items.SUSPICIOUS_STEW);
			result.getOrCreateTag().put(SuspiciousStewItem.EFFECTS_TAG, cauldron.getEffects().copy());
			return result;
		}
		return ItemStack.EMPTY;
	}
}
