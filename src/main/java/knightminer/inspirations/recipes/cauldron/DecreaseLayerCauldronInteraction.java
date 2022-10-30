package knightminer.inspirations.recipes.cauldron;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/** Decreases the cauldron by one layer, returning the given result */
public class DecreaseLayerCauldronInteraction extends AbstractDecreaseLayerCauldronInteraction {
	private final ItemStack result;
	public DecreaseLayerCauldronInteraction(ItemStack result, IntegerProperty prop, boolean consumeInput, SoundEvent sound) {
		super(prop, consumeInput, sound);
		this.result = result;
	}

	public DecreaseLayerCauldronInteraction(ItemLike result, IntegerProperty prop, SoundEvent sound) {
		this(new ItemStack(result), prop, true, sound);
	}

	public DecreaseLayerCauldronInteraction(ItemLike result, IntegerProperty prop) {
		this(result, prop, SoundEvents.BOTTLE_FILL);
	}

	@Override
	protected ItemStack getResult(BlockState state, Level level, BlockPos pos, ItemStack stack) {
		return result.copy();
	}
}
