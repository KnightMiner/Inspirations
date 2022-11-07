package knightminer.inspirations.cauldrons.interaction.dye;

import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import knightminer.inspirations.cauldrons.block.entity.DyeCauldronBlockEntity;
import knightminer.inspirations.cauldrons.interaction.AbstractDecreaseLayerCauldronInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Logic to fill a glass bottle with dyed water */
public class FillDyedBottleCauldronInteraction extends AbstractDecreaseLayerCauldronInteraction {
	public static final FillDyedBottleCauldronInteraction INSTANCE = new FillDyedBottleCauldronInteraction();
	private FillDyedBottleCauldronInteraction() {
		super(LayeredCauldronBlock.LEVEL, true, SoundEvents.BOTTLE_FILL);
	}

	@Override
	protected ItemStack getResult(BlockState state, Level level, BlockPos pos, ItemStack stack) {
		DyeCauldronBlockEntity cauldron = InspirationsCaudrons.dyeCauldronEntity.getBlockEntity(level, pos);
		if (cauldron != null) {
			DyeColor dye = cauldron.getDye();
			if (dye != null) {
				return new ItemStack(InspirationsCaudrons.simpleDyedWaterBottle.get(dye));
			} else {
				return MiscUtil.setColor(new ItemStack(InspirationsCaudrons.mixedDyedWaterBottle), cauldron.getColor());
			}
		}
		return ItemStack.EMPTY;
	}
}
