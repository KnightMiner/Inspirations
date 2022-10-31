package knightminer.inspirations.recipes.cauldron.dye;

import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.recipes.block.entity.DyeCauldronBlockEntity;
import knightminer.inspirations.recipes.cauldron.IncreaseLayerCauldronInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/** Increases the amount of dye in a dyed cauldron, mixing the color if different */
public class DyedBottleIntoDyeCauldronInteraction extends IncreaseLayerCauldronInteraction {
	@Nullable
	private final Integer color;
	protected DyedBottleIntoDyeCauldronInteraction(ItemLike bottle, @Nullable Integer color) {
		super(bottle, LayeredCauldronBlock.LEVEL, 3);
		this.color = color;
	}

	public DyedBottleIntoDyeCauldronInteraction(@Nullable Integer color) {
		this(Items.GLASS_BOTTLE, color);
	}

	@Override
	protected void afterSetBlock(BlockState state, Level level, BlockPos pos, ItemStack stack) {
		DyeCauldronBlockEntity cauldron = InspirationsRecipes.dyeCauldronEntity.getBlockEntity(level, pos);
		if (cauldron != null) {
			int newColor = this.color != null ? this.color : MiscUtil.getColor(stack);
			cauldron.setColor(MiscUtil.addColors(newColor, 1, cauldron.getColor(), state.getValue(this.prop)));
		}
	}
}
