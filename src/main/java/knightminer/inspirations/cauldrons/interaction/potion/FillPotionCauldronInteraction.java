package knightminer.inspirations.cauldrons.interaction.potion;

import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import knightminer.inspirations.cauldrons.block.entity.PotionCauldronBlockEntity;
import knightminer.inspirations.cauldrons.interaction.AbstractDecreaseLayerCauldronInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
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
		PotionCauldronBlockEntity cauldron = InspirationsCaudrons.potionCauldronEntity.getBlockEntity(level, pos);
		if (cauldron != null) {
			return PotionUtils.setPotion(new ItemStack(potionItem), cauldron.getPotion());
		}
		return ItemStack.EMPTY;
	}
}
