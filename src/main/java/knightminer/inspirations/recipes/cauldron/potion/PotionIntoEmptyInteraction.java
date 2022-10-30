package knightminer.inspirations.recipes.cauldron.potion;

import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.recipes.block.entity.PotionCauldronBlockEntity;
import knightminer.inspirations.recipes.cauldron.AbstractModifyCauldronInteraction;
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
import net.minecraft.world.level.block.entity.BlockEntity;
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
		Block block = PotionUtils.getPotion(filledStack) == Potions.WATER ? Blocks.WATER_CAULDRON : InspirationsRecipes.potionCauldron;
		return block.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 1);
	}

	@Override
	protected void afterSetBlock(BlockState oldState, Level level, BlockPos pos, ItemStack stack) {
		Potion potion = PotionUtils.getPotion(stack);
		if (potion != Potions.WATER) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be != null && be.getType() == InspirationsRecipes.potionCauldronEntity) {
				((PotionCauldronBlockEntity)be).setPotion(potion);
			}
		}
	}
}
