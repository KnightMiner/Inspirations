package knightminer.inspirations.cauldrons.interaction.dye;

import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import knightminer.inspirations.cauldrons.block.entity.DyeCauldronBlockEntity;
import knightminer.inspirations.cauldrons.interaction.FillCauldronInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/** Pours a dyed bottle into an empty cauldron */
public class DyedBottleIntoEmptyCauldronInteraction extends FillCauldronInteraction {
	@Nullable
	private final Integer color;
	public DyedBottleIntoEmptyCauldronInteraction(@Nullable Integer color) {
		super(InspirationsCaudrons.dyeCauldron.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 1), Items.GLASS_BOTTLE, SoundEvents.BOTTLE_EMPTY);
		this.color = color;
	}

	@Override
	protected void afterSetBlock(BlockState oldState, Level level, BlockPos pos, ItemStack stack) {
		DyeCauldronBlockEntity cauldron = InspirationsCaudrons.dyeCauldronEntity.getBlockEntity(level, pos);
		if (cauldron != null) {
			cauldron.setColor(this.color != null ? this.color : MiscUtil.getColor(stack));
		}
	}
}
