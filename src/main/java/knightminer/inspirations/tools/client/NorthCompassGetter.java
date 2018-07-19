package knightminer.inspirations.tools.client;

import javax.annotation.Nullable;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NorthCompassGetter implements IItemPropertyGetter {
	double rotation;
	double rota;
	long lastUpdateTick;

	@Override
	@SideOnly(Side.CLIENT)
	public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
		if(stack.isOnItemFrame()) {
			EntityItemFrame frame = stack.getItemFrame();
			EnumFacing facing = frame.getHorizontalFacing();
			// TODO: quark support
			if(facing == null || facing.getAxis() == Axis.Y) {
				return 0;
			}
			return MathHelper.positiveModulo(facing.getHorizontalIndex()/4f + 0.5f + frame.getRotation()/8f, 1);
		}

		if(entity == null) {
			return 0;
		}
		if(world == null) {
			if(entity.world == null) {
				return 0;
			}
			world = entity.world;
		}

		double angle = MathHelper.positiveModulo(entity.rotationYaw/360, 1);
		return (float) (entity instanceof EntityPlayerSP ? wobble(world, angle) : angle);
	}

	@SideOnly(Side.CLIENT)
	private double wobble(World world, double angle) {
		if(world.getTotalWorldTime() != lastUpdateTick) {
			lastUpdateTick = world.getTotalWorldTime();
			double d0 = angle - rotation;
			d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
			rota += d0 * 0.1D;
			rota *= 0.9D;
			rotation = MathHelper.positiveModulo(rotation + rota, 1.0D);
		}

		return rotation;
	}
}
