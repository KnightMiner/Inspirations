package knightminer.inspirations.cauldrons.interaction.potion;

import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import knightminer.inspirations.cauldrons.block.entity.PotionCauldronBlockEntity;
import knightminer.inspirations.cauldrons.interaction.AbstractDecreaseLayerCauldronInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Tips a stack of 16 arrows in the cauldron */
public class TipArrowCauldronInteraction extends AbstractDecreaseLayerCauldronInteraction {
	public static final TipArrowCauldronInteraction INSTANCE = new TipArrowCauldronInteraction();
	private TipArrowCauldronInteraction() {
		super(LayeredCauldronBlock.LEVEL, true, SoundEvents.GENERIC_SPLASH);
	}

	@Override
	protected ItemStack getResult(BlockState state, Level level, BlockPos pos, ItemStack stack) {
		PotionCauldronBlockEntity cauldron = InspirationsCaudrons.potionCauldronEntity.getBlockEntity(level, pos);
		if (cauldron != null) {
			int resultCount = Math.min(stack.getCount(), 16);
			stack.shrink(resultCount - 1);
			return PotionUtils.setPotion(new ItemStack(Items.TIPPED_ARROW, resultCount), cauldron.getPotion());
		}
		return ItemStack.EMPTY;
	}
}
