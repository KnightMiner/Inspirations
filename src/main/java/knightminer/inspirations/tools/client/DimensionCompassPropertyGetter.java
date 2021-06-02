package knightminer.inspirations.tools.client;

import knightminer.inspirations.tools.capability.DimensionCompass;
import knightminer.inspirations.tools.capability.IDimensionCompass;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Optional;

public class DimensionCompassPropertyGetter implements IItemPropertyGetter {
	/* Last rotation for a target position */
	private final Angle positionWobble = new Angle();
	/** Last rotation for random position */
	private final Angle randomRotation = new Angle();

	@Override
	public float call(ItemStack stack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity living) {
		Entity entity = living != null ? living : stack.getAttachedEntity();
		if (entity == null) {
			return 0.0F;
		}

		// ensure we have a world
		World world = clientWorld;
		if (world == null) {
			if (entity.world == null) {
				return 0.0f;
			}
			world = entity.world;
		}

		// start by selecting our target position
		BlockPos pos = CompassItem.func_234670_d_(stack)
									 ? getLodestonePosition(world, stack.getOrCreateTag())
									 : getDimensionEntered(entity);
		long time = world.getGameTime();
		Vector3d entityPos = entity.getPositionVec();

		// if the position is valid and we are not exactly on the position
		if (pos != null && entityPos.squareDistanceTo(pos.getX() + 0.5D, entityPos.getY(), pos.getZ() + 0.5D) >= 9.999999747378752E-6D) {
			boolean isPlayer = living instanceof PlayerEntity && ((PlayerEntity)living).isUser();

			// angle is based on holder
			double holderAngle = 0.0D;
			if (isPlayer) {
				holderAngle = living.rotationYaw;
			} else if (entity instanceof ItemFrameEntity) {
				holderAngle = Angle.getFrameRotation((ItemFrameEntity)entity);
			} else if (entity instanceof ItemEntity) {
				holderAngle = (180.0F - ((ItemEntity)entity).getItemHover(0.5F) / 6.2831855F * 360.0F);
			} else if (living != null) {
				holderAngle = living.renderYawOffset;
			}

			// wobble it a little
			holderAngle = MathHelper.positiveModulo(holderAngle / 360.0D, 1.0D);
			double exactRotation = getAngleToPosition(Vector3d.copyCentered(pos), entity) / 6.2831854820251465D;
			double wobbleRotation;
			if (isPlayer) {
				if (this.positionWobble.shouldUpdate(time)) {
					this.positionWobble.wobble(time, 0.5D - (holderAngle - 0.25D));
				}
				wobbleRotation = exactRotation + this.positionWobble.getRotation();
			} else {
				wobbleRotation = 0.5D - (holderAngle - 0.25D - exactRotation);
			}

			return MathHelper.positiveModulo((float)wobbleRotation, 1.0F);
		} else {
			// spin randomly
			if (this.randomRotation.shouldUpdate(time)) {
				this.randomRotation.wobble(time, Math.random());
			}

			double wobbleRotation = this.randomRotation.getRotation() + (stack.hashCode() / 2.14748365E9F);
			return MathHelper.positiveModulo((float)wobbleRotation, 1.0F);
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
	private static BlockPos getLodestonePosition(World world, CompoundNBT nbt) {
		boolean hasPos = nbt.contains("LodestonePos");
		boolean hasDim = nbt.contains("LodestoneDimension");
		if (hasPos && hasDim) {
			Optional<RegistryKey<World>> optional = CompassItem.func_234667_a_(nbt);
			if (optional.isPresent()) {
				RegistryKey<World> storedDimension = optional.get();
				RegistryKey<World> currentDimension = world.getDimensionKey();
				BlockPos pos = NBTUtil.readBlockPos(nbt.getCompound("LodestonePos"));

				if (storedDimension != currentDimension) {
					// from nether coords
					if (storedDimension == World.THE_NETHER) {
						return new BlockPos(pos.getX() * 8, pos.getY(), pos.getZ() * 8);
					}
					// to nether coords
					if (currentDimension == World.THE_NETHER) {
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
	private static double getAngleToPosition(Vector3d target, Entity entity) {
		return Math.atan2(target.getZ() - entity.getPosZ(), target.getX() - entity.getPosX());
	}
}
