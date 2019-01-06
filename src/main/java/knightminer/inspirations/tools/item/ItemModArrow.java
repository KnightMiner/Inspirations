package knightminer.inspirations.tools.item;

import java.util.Locale;
import java.util.function.BooleanSupplier;

import javax.annotation.Nonnull;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.tools.entity.EntityModArrow;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class ItemModArrow extends ItemArrow {
	@Nonnull
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		// eventually may have more types here
		return super.getUnlocalizedName(stack) + "." + ArrowType.fromMeta(stack.getMetadata()).getName();
	}

	@Override
	public EntityArrow createArrow(World world, ItemStack stack, EntityLivingBase shooter) {
		return new EntityModArrow(world, shooter, stack.getMetadata());
	}

	@Override
	public boolean isInfinite(ItemStack stack, ItemStack bow, net.minecraft.entity.player.EntityPlayer player) {
		return false;
	}

	/**
	 * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
	 */
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			for(ArrowType type : ArrowType.values()) {
				if(type.isEnabled()) {
					items.add(new ItemStack(this, 1, type.getMeta()));
				}
			}
		}
	}

	public static enum ArrowType {
		CHARGED(() -> Config.enableChargedArrow);

		private int meta;
		private BooleanSupplier enabled;
		ArrowType(BooleanSupplier enabled) {
			this.meta = ordinal();
			this.enabled = enabled;
		}

		public boolean isEnabled() {
			return enabled.getAsBoolean();
		}

		public int getMeta() {
			return meta;
		}

		public String getName() {
			return name().toLowerCase(Locale.US);
		}

		public static ArrowType fromMeta(int meta) {
			if(meta >= values().length) {
				meta = 0;
			}
			return values()[meta];
		}
	}
}
