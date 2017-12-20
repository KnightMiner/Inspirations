package knightminer.inspirations.building.item;

import javax.annotation.Nonnull;

import knightminer.inspirations.building.entity.EntityModArrow;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemChargedArrow extends ItemArrow {
	@Nonnull
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		// eventually may have more types here
		return super.getUnlocalizedName(stack) + "." + "charged";
	}

	@Override
	public EntityArrow createArrow(World world, ItemStack stack, EntityLivingBase shooter) {
		return new EntityModArrow(world, shooter);
	}

	@Override
	public boolean isInfinite(ItemStack stack, ItemStack bow, net.minecraft.entity.player.EntityPlayer player) {
		return false;
	}
}
