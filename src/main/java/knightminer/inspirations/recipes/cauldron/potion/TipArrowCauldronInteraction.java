package knightminer.inspirations.recipes.cauldron.potion;

import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.recipes.block.entity.PotionCauldronBlockEntity;
import knightminer.inspirations.recipes.cauldron.AbstractDecreaseLayerCauldronInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Tips a stack of 16 arrows in the cauldron */
public class TipArrowCauldronInteraction extends AbstractDecreaseLayerCauldronInteraction {
	public static final TipArrowCauldronInteraction INSTANCE = new TipArrowCauldronInteraction();
	private TipArrowCauldronInteraction() {
		super(LayeredCauldronBlock.LEVEL, true, SoundEvents.GENERIC_SPLASH);
	}

	@Override
	protected ItemStack getResult(BlockState state, Level level, BlockPos pos, ItemStack stack) {
		BlockEntity be = level.getBlockEntity(pos);
		if (be != null && be.getType() == InspirationsRecipes.potionCauldronEntity) {
			int resultCount = Math.min(stack.getCount(), 16);
			stack.shrink(resultCount - 1);
			return PotionUtils.setPotion(new ItemStack(Items.TIPPED_ARROW, resultCount), ((PotionCauldronBlockEntity) be).getPotion());
		}
		return ItemStack.EMPTY;
	}
}
