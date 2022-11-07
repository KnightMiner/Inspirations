package knightminer.inspirations.cauldrons.interaction.dye;

import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import knightminer.inspirations.cauldrons.block.entity.DyeCauldronBlockEntity;
import knightminer.inspirations.cauldrons.interaction.AbstractDecreaseLayerCauldronInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

/** Logic to dye an item using a cauldron */
public class DyeItemCauldronInteraction extends AbstractDecreaseLayerCauldronInteraction {
	private final Function<DyeColor,ItemLike> mapper;
	private final boolean copyNBT;
	public DyeItemCauldronInteraction(Function<DyeColor,ItemLike> mapper, boolean copyNBT) {
		super(LayeredCauldronBlock.LEVEL, true, SoundEvents.GENERIC_SPLASH);
		this.mapper = mapper;
		this.copyNBT = copyNBT;
	}

	public DyeItemCauldronInteraction(Function<DyeColor,ItemLike> mapper) {
		this(mapper, false);
	}

	@Override
	protected ItemStack getResult(BlockState state, Level level, BlockPos pos, ItemStack stack) {
		DyeCauldronBlockEntity cauldron = InspirationsCaudrons.dyeCauldronEntity.getBlockEntity(level, pos);
		if (cauldron != null) {
			DyeColor dye = cauldron.getDye();
			if (dye != null) {
				ItemStack result = new ItemStack(mapper.apply(dye));
				if (copyNBT) {
					CompoundTag nbt = stack.getTag();
					if (nbt != null) {
						result.setTag(nbt.copy());
					}
				}
				return result;
			}
		}
		return ItemStack.EMPTY;
	}
}
