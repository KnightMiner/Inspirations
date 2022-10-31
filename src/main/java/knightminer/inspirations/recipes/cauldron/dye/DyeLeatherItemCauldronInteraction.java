package knightminer.inspirations.recipes.cauldron.dye;

import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.recipes.block.entity.DyeCauldronBlockEntity;
import knightminer.inspirations.recipes.cauldron.AbstractDecreaseLayerCauldronInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemHandlerHelper;

/** Logic to dye a leather item using the dye color */
public class DyeLeatherItemCauldronInteraction extends AbstractDecreaseLayerCauldronInteraction {
	public static final DyeLeatherItemCauldronInteraction INSTANCE = new DyeLeatherItemCauldronInteraction();
	private DyeLeatherItemCauldronInteraction() {
		super(LayeredCauldronBlock.LEVEL, true, SoundEvents.GENERIC_SPLASH);
	}

	@Override
	protected ItemStack getResult(BlockState state, Level level, BlockPos pos, ItemStack stack) {
		if (stack.getItem() instanceof DyeableLeatherItem dyeable) {
			DyeCauldronBlockEntity cauldron = InspirationsRecipes.dyeCauldronEntity.getBlockEntity(level, pos);
			if (cauldron != null) {
				ItemStack result = ItemHandlerHelper.copyStackWithSize(stack, 1);
				dyeable.setColor(result, cauldron.getColor());
				return result;
			}
		}
		return ItemStack.EMPTY;
	}
}
