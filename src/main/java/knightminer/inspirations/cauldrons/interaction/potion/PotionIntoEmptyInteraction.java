package knightminer.inspirations.cauldrons.interaction.potion;

import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import knightminer.inspirations.cauldrons.block.entity.PotionCauldronBlockEntity;
import knightminer.inspirations.cauldrons.interaction.AbstractModifyCauldronInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/** Logic to dump a potion into an empty cauldron */
public class PotionIntoEmptyInteraction extends AbstractModifyCauldronInteraction {
	public PotionIntoEmptyInteraction(ItemLike container) {
		super(container, SoundEvents.BOTTLE_FILL, Stats.FILL_CAULDRON);
	}

	@Nullable
	@Override
	protected BlockState getNewState(BlockState oldState, Level level, BlockPos pos, ItemStack filledStack) {
		Block block = PotionUtils.getPotion(filledStack) == Potions.WATER ? Blocks.WATER_CAULDRON : InspirationsCaudrons.potionCauldron;
		return block.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 1);
	}

	@Override
	protected void afterSetBlock(BlockState oldState, Level level, BlockPos pos, ItemStack stack) {
		Potion potion = PotionUtils.getPotion(stack);
		if (potion != Potions.WATER) {
			PotionCauldronBlockEntity cauldron = InspirationsCaudrons.potionCauldronEntity.getBlockEntity(level, pos);
			if (cauldron != null) {
				cauldron.setPotion(potion);
			}
		}
	}
}
