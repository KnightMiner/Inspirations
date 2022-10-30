package knightminer.inspirations.recipes.cauldron.dye;

import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.recipes.block.entity.DyeCauldronBlockEntity;
import knightminer.inspirations.recipes.cauldron.AbstractDecreaseLayerCauldronInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Logic to fill a glass bottle with dyed water */
public class FillDyedBottleCauldronInteraction extends AbstractDecreaseLayerCauldronInteraction {
	public static final FillDyedBottleCauldronInteraction INSTANCE = new FillDyedBottleCauldronInteraction();
	private FillDyedBottleCauldronInteraction() {
		super(LayeredCauldronBlock.LEVEL, true, SoundEvents.BOTTLE_FILL);
	}

	@Override
	protected ItemStack getResult(BlockState state, Level level, BlockPos pos, ItemStack stack) {
		BlockEntity be = level.getBlockEntity(pos);
		if (be != null && be.getType() == InspirationsRecipes.dyeCauldronEntity) {
			DyeCauldronBlockEntity cauldron = (DyeCauldronBlockEntity) be;
			DyeColor dye = cauldron.getDye();
			if (dye != null) {
				return new ItemStack(InspirationsRecipes.simpleDyedWaterBottle.get(dye));
			} else {
				return MiscUtil.setColor(new ItemStack(InspirationsRecipes.mixedDyedWaterBottle), cauldron.getColor());
			}
		}
		return ItemStack.EMPTY;
	}
}
