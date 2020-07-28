package knightminer.inspirations.tools.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class NorthCompassPropertyGetter implements IItemPropertyGetter {
	double rotation;
	double rota;
	long lastUpdateTick;

	@Override
	@OnlyIn(Dist.CLIENT)
	public float call(ItemStack stack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity entity) {
		ItemFrameEntity frame = stack.getItemFrame();
		if(frame != null) {
			Direction facing = frame.getHorizontalFacing();
			if(facing == Direction.DOWN) {
				return frame.getRotation()/8f;
			} else if (facing == Direction.UP) {
				// Flip 180 degrees.
				return MathHelper.positiveModulo(0.5f + frame.getRotation()/8f, 1f);
			}
			return MathHelper.positiveModulo(facing.getHorizontalIndex()/4f + 0.5f + frame.getRotation()/8f, 1);
		}

		if(entity == null) {
			return 0;
		}
		World world = clientWorld;
		if(world == null) {
			if(entity.world == null) {
				return 0;
			}
			world = entity.world;
		}

		double angle = MathHelper.positiveModulo(entity.rotationYaw/360, 1);
		return (float) (entity == Minecraft.getInstance().player ? wobble(world, angle) : angle);
	}

	@OnlyIn(Dist.CLIENT)
	private double wobble(World world, double angle) {
		if(world.getGameTime() != lastUpdateTick) {
			lastUpdateTick = world.getGameTime();
			double d0 = angle - rotation;
			d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
			rota += d0 * 0.1D;
			rota *= 0.9D;
			rotation = MathHelper.positiveModulo(rotation + rota, 1.0D);
		}

		return rotation;
	}
}
