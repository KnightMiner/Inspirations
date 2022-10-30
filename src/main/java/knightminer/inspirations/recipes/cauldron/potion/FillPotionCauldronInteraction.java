package knightminer.inspirations.recipes.cauldron.potion;

import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.recipes.block.entity.PotionCauldronBlockEntity;
import knightminer.inspirations.recipes.cauldron.AbstractDecreaseLayerCauldronInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Fills a potion from the potion cauldron */
public class FillPotionCauldronInteraction extends AbstractDecreaseLayerCauldronInteraction {
	private final Item potionItem;
	public FillPotionCauldronInteraction(Item potionItem) {
		super(LayeredCauldronBlock.LEVEL, true, SoundEvents.BOTTLE_FILL);
		this.potionItem = potionItem;
	}

	@Override
	protected ItemStack getResult(BlockState state, Level level, BlockPos pos, ItemStack stack) {
		BlockEntity be = level.getBlockEntity(pos);
		if (be != null && be.getType() == InspirationsRecipes.potionCauldronEntity) {
			return PotionUtils.setPotion(new ItemStack(potionItem), ((PotionCauldronBlockEntity) be).getPotion());
		}
		return ItemStack.EMPTY;
	}
}
