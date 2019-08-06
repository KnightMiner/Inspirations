package knightminer.inspirations.tools.client;

import knightminer.inspirations.tools.item.ItemWaypointCompass;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.WeakHashMap;

public class WaypointCompassGetter implements IItemPropertyGetter {

	@Override
	@OnlyIn(Dist.CLIENT)
	public float call(@Nonnull ItemStack stack, @Nullable World world, @Nullable LivingEntity entityIn) {
		if (entityIn == null && !stack.isOnItemFrame()) {
			return 0.0F;
		}
		boolean isHeld = entityIn != null;
		Entity entity = isHeld ? entityIn : stack.getItemFrame();

		if (world == null || entity == null) {
			if(entity == null || entity.world == null) {
				return 0.0F;
			}
			world = entity.world;
		}

		double angle = getAngle(stack, world, entity, isHeld);
		if (entity == Minecraft.getInstance().player) {
			angle = this.wobble(stack, world, angle);
		}

		return MathHelper.positiveModulo((float)angle, 1.0F);
	}


	/* Math functions */

	@OnlyIn(Dist.CLIENT)
	private double getAngle(ItemStack stack, @Nonnull World world, @Nonnull Entity entity, boolean isHeld) {
		DimensionType dimension = ItemWaypointCompass.getDimensionType(stack);
		if (dimension != null) {
			BlockPos pos = ItemWaypointCompass.getPos(stack, dimension, world.getDimension().getType());
			if (pos != null) {
				double entityAngle = isHeld ? (double)entity.rotationYaw : this.getFrameRotation((ItemFrameEntity) entity);
				entityAngle = MathHelper.positiveModulo(entityAngle / 360.0D, 1.0D);
				double posAngle = this.posToAngle(pos, entity) / (Math.PI * 2D);
				return 0.5D - (entityAngle - 0.25D - posAngle);
			}
		}
		return Math.random();
	}

	@OnlyIn(Dist.CLIENT)
	private double getFrameRotation(ItemFrameEntity frame) {
		return MathHelper.wrapDegrees(180 + frame.getHorizontalFacing().getHorizontalIndex() * 90);
	}

	@OnlyIn(Dist.CLIENT)
	private double posToAngle(BlockPos pos, Entity entity) {
		return Math.atan2((double)pos.getZ() + 0.5 - entity.posZ, (double)pos.getX() + 0.5 - entity.posX);
	}


	/* Wobble logic */
	private Map<ItemStack,Wobble> wobbleMap = new WeakHashMap<>();

	@OnlyIn(Dist.CLIENT)
	private double wobble(ItemStack stack, World world, double angle) {
		Wobble wobble = wobbleMap.computeIfAbsent(stack, (s)->new Wobble());
		if (world.getGameTime() != wobble.lastUpdateTick) {
			wobble.lastUpdateTick = world.getGameTime();
			double newAngle = angle - wobble.rotation;
			newAngle = MathHelper.positiveModulo(newAngle + 0.5D, 1.0D) - 0.5D;
			wobble.rota += newAngle * 0.1D;
			wobble.rota *= 0.8D;
			wobble.rotation = MathHelper.positiveModulo(wobble.rotation + wobble.rota, 1.0D);
		}

		return wobble.rotation;
	}

	private static class Wobble {
		double rotation;
		double rota;
		long lastUpdateTick;
	}
}
