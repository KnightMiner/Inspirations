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

/* Yes, the interface says armor, but in mojmappings its not armor, its just dyeable */
public class DimensionCompassItem extends CompassItem implements IDyeableArmorItem {
	public DimensionCompassItem(Properties builder) {
		super(builder);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return func_234670_d_(stack) ? "item.inspirations.lodestone_dimension_compass" : this.getTranslationKey();
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		// basically a copy of the vanilla compass, but does not hardcode to compass item
		BlockPos blockpos = context.getPos();
		World world = context.getWorld();
		if (!world.getBlockState(blockpos).isIn(Blocks.LODESTONE)) {
			return ActionResultType.PASS;
		}

		world.playSound(null, blockpos, SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);
		PlayerEntity player = context.getPlayer();
		if (player == null) {
			return ActionResultType.PASS;
		}
		ItemStack stack = context.getItem();
		boolean singleItem = !player.abilities.isCreativeMode && stack.getCount() == 1;
		if (singleItem) {
			write(world.getDimensionKey(), blockpos, stack.getOrCreateTag());
		} else {
			ItemStack copy = new ItemStack(this, 1);
			CompoundNBT nbt = stack.getTag();
			if (nbt == null) {
				nbt = new CompoundNBT();
			} else {
				nbt = nbt.copy();
			}
			copy.setTag(nbt);
			if (!player.abilities.isCreativeMode) {
				stack.shrink(1);
			}
			write(world.getDimensionKey(), blockpos, nbt);
			if (!player.inventory.addItemStackToInventory(copy)) {
				player.dropItem(copy, false);
			}
		}

		return ActionResultType.func_233537_a_(world.isRemote);
	}

	/**
	 * Writes the dimension and position to the compass
	 * @param dimension  Dimension
	 * @param pos        Position
	 * @param nbt        NBT
	 */
	private static void write(RegistryKey<World> dimension, BlockPos pos, CompoundNBT nbt) {
		nbt.put("LodestonePos", NBTUtil.writeBlockPos(pos));
		World.CODEC.encodeStart(NBTDynamicOps.INSTANCE, dimension)
							 .resultOrPartial(Inspirations.log::error)
							 .ifPresent(key -> nbt.put("LodestoneDimension", key));
		nbt.putBoolean("LodestoneTracked", true);
	}
}
