package knightminer.inspirations.tools.client;

import knightminer.inspirations.tools.capability.DimensionCompass;
import knightminer.inspirations.tools.capability.IDimensionCompass;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;

public class DimensionCompassPropertyGetter implements ItemPropertyFunction {
	/* Last rotation for a target position */
	private final Angle positionWobble = new Angle();
	/** Last rotation for random position */
	private final Angle randomRotation = new Angle();

	@Override
	public float call(ItemStack stack, @Nullable ClientLevel clientWorld, @Nullable LivingEntity living, int seed) {
		Entity entity = living != null ? living : stack.getEntityRepresentation();
		if (entity == null) {
			return 0.0F;
		}

		// ensure we have a world
		Level world = clientWorld;
		if (world == null) {
			world = entity.level;
		}

		// start by selecting our target position
		BlockPos pos = CompassItem.isLodestoneCompass(stack)
									 ? getLodestonePosition(world, stack.getOrCreateTag())
									 : getDimensionEntered(entity);
		long time = world.getGameTime();
		Vec3 entityPos = entity.position();

		// if the position is valid and we are not exactly on the position
		if (pos != null && entityPos.distanceToSqr(pos.getX() + 0.5D, entityPos.y(), pos.getZ() + 0.5D) >= 9.999999747378752E-6D) {
			boolean isPlayer = living instanceof Player && ((Player)living).isLocalPlayer();

			// angle is based on holder
			double holderAngle = 0.0D;
			if (isPlayer) {
				holderAngle = living.getYRot();
			} else if (entity instanceof ItemFrame) {
				holderAngle = Angle.getFrameRotation((ItemFrame)entity);
			} else if (entity instanceof ItemEntity) {
				holderAngle = (180.0F - ((ItemEntity)entity).getSpin(0.5F) / 6.2831855F * 360.0F);
			} else if (living != null) {
				holderAngle = living.yBodyRot;
			}

			// wobble it a little
			holderAngle = Mth.positiveModulo(holderAngle / 360.0D, 1.0D);
			double exactRotation = getAngleToPosition(Vec3.atCenterOf(pos), entity) / 6.2831854820251465D;
			double wobbleRotation;
			if (isPlayer) {
				if (this.positionWobble.shouldUpdate(time)) {
					this.positionWobble.wobble(time, 0.5D - (holderAngle - 0.25D));
				}
				wobbleRotation = exactRotation + this.positionWobble.getRotation();
			} else {
				wobbleRotation = 0.5D - (holderAngle - 0.25D - exactRotation);
			}

			return Mth.positiveModulo((float)wobbleRotation, 1.0F);
		} else {
			// spin randomly
			if (this.randomRotation.shouldUpdate(time)) {
				this.randomRotation.wobble(time, Math.random());
			}

			double wobbleRotation = this.randomRotation.getRotation() + (stack.hashCode() / 2.14748365E9F);
			return Mth.positiveModulo((float)wobbleRotation, 1.0F);
		}
	}

	/**
	 * Gets the position the entity entered the dimension
	 * @param entity  Entity
	 * @return  Position, or null if never entered
	 */
	@Nullable
	private static BlockPos getDimensionEntered(Entity entity) {
		return entity.getCapability(DimensionCompass.CAPABILITY).resolve().map(IDimensionCompass::getEnteredPosition).orElse(null);
	}

	/**
	 * Gets the position of the lodestone
	 * @param world  Lodestone
	 * @param nbt    Stack NBT
	 * @return  Lodestone position
	 */
	@Nullable
	private static BlockPos getLodestonePosition(Level world, CompoundTag nbt) {
		boolean hasPos = nbt.contains("LodestonePos");
		boolean hasDim = nbt.contains("LodestoneDimension");
		if (hasPos && hasDim) {
			Optional<ResourceKey<Level>> optional = CompassItem.getLodestoneDimension(nbt);
			if (optional.isPresent()) {
				ResourceKey<Level> storedDimension = optional.get();
				ResourceKey<Level> currentDimension = world.dimension();
				BlockPos pos = NbtUtils.readBlockPos(nbt.getCompound("LodestonePos"));

				if (storedDimension != currentDimension) {
					// from nether coords
					if (storedDimension == Level.NETHER) {
						return new BlockPos(pos.getX() * 8, pos.getY(), pos.getZ() * 8);
					}
					// to nether coords
					if (currentDimension == Level.NETHER) {
						return new BlockPos(Math.round(pos.getX() / 8f), pos.getY(), Math.round(pos.getZ() / 8f));
					}
				}
				return pos;
			}
		}

		return null;
	}

	/**
	 * Gets the angle from the position to the entity
	 */
	private static double getAngleToPosition(Vec3 target, Entity entity) {
		return Math.atan2(target.z() - entity.getZ(), target.x() - entity.getX());
	}
}
