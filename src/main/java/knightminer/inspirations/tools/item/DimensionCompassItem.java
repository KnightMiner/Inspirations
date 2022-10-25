package knightminer.inspirations.tools.item;

import knightminer.inspirations.Inspirations;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraft.item.Item.Properties;

/* Yes, the interface says armor, but in mojmappings its not armor, its just dyeable */
public class DimensionCompassItem extends CompassItem implements IDyeableArmorItem {
	public DimensionCompassItem(Properties builder) {
		super(builder);
	}

	@Override
	public String getDescriptionId(ItemStack stack) {
		return isLodestoneCompass(stack) ? "item.inspirations.lodestone_dimension_compass" : this.getDescriptionId();
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		// basically a copy of the vanilla compass, but does not hardcode to compass item
		BlockPos blockpos = context.getClickedPos();
		World world = context.getLevel();
		if (!world.getBlockState(blockpos).is(Blocks.LODESTONE)) {
			return ActionResultType.PASS;
		}

		world.playSound(null, blockpos, SoundEvents.LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);
		PlayerEntity player = context.getPlayer();
		if (player == null) {
			return ActionResultType.PASS;
		}
		ItemStack stack = context.getItemInHand();
		boolean singleItem = !player.abilities.instabuild && stack.getCount() == 1;
		if (singleItem) {
			write(world.dimension(), blockpos, stack.getOrCreateTag());
		} else {
			ItemStack copy = new ItemStack(this, 1);
			CompoundNBT nbt = stack.getTag();
			if (nbt == null) {
				nbt = new CompoundNBT();
			} else {
				nbt = nbt.copy();
			}
			copy.setTag(nbt);
			if (!player.abilities.instabuild) {
				stack.shrink(1);
			}
			write(world.dimension(), blockpos, nbt);
			if (!player.inventory.add(copy)) {
				player.drop(copy, false);
			}
		}

		return ActionResultType.sidedSuccess(world.isClientSide);
	}

	/**
	 * Writes the dimension and position to the compass
	 * @param dimension  Dimension
	 * @param pos        Position
	 * @param nbt        NBT
	 */
	private static void write(RegistryKey<World> dimension, BlockPos pos, CompoundNBT nbt) {
		nbt.put("LodestonePos", NBTUtil.writeBlockPos(pos));
		World.RESOURCE_KEY_CODEC.encodeStart(NBTDynamicOps.INSTANCE, dimension)
							 .resultOrPartial(Inspirations.log::error)
							 .ifPresent(key -> nbt.put("LodestoneDimension", key));
		nbt.putBoolean("LodestoneTracked", true);
	}
}
