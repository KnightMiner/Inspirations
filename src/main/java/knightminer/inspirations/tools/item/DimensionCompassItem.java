package knightminer.inspirations.tools.item;

import knightminer.inspirations.Inspirations;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/* Yes, the interface says armor, but in mojmappings its not armor, its just dyeable */
public class DimensionCompassItem extends CompassItem implements DyeableLeatherItem {
	public DimensionCompassItem(Properties builder) {
		super(builder);
	}

	@Override
	public String getDescriptionId(ItemStack stack) {
		return isLodestoneCompass(stack) ? "item.inspirations.lodestone_dimension_compass" : this.getDescriptionId();
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		// basically a copy of the vanilla compass, but does not hardcode to compass item
		BlockPos blockpos = context.getClickedPos();
		Level world = context.getLevel();
		if (!world.getBlockState(blockpos).is(Blocks.LODESTONE)) {
			return InteractionResult.PASS;
		}

		world.playSound(null, blockpos, SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
		Player player = context.getPlayer();
		if (player == null) {
			return InteractionResult.PASS;
		}
		ItemStack stack = context.getItemInHand();
		boolean singleItem = !player.getAbilities().instabuild && stack.getCount() == 1;
		if (singleItem) {
			write(world.dimension(), blockpos, stack.getOrCreateTag());
		} else {
			ItemStack copy = new ItemStack(this, 1);
			CompoundTag nbt = stack.getTag();
			if (nbt == null) {
				nbt = new CompoundTag();
			} else {
				nbt = nbt.copy();
			}
			copy.setTag(nbt);
			if (!player.getAbilities().instabuild) {
				stack.shrink(1);
			}
			write(world.dimension(), blockpos, nbt);
			if (!player.getInventory().add(copy)) {
				player.drop(copy, false);
			}
		}

		return InteractionResult.sidedSuccess(world.isClientSide);
	}

	/**
	 * Writes the dimension and position to the compass
	 * @param dimension  Dimension
	 * @param pos        Position
	 * @param nbt        NBT
	 */
	private static void write(ResourceKey<Level> dimension, BlockPos pos, CompoundTag nbt) {
		nbt.put("LodestonePos", NbtUtils.writeBlockPos(pos));
		Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, dimension)
							 .resultOrPartial(Inspirations.log::error)
							 .ifPresent(key -> nbt.put("LodestoneDimension", key));
		nbt.putBoolean("LodestoneTracked", true);
	}
}
