package knightminer.inspirations.tools.client;

import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

/**
 * Clone of vanilla wobble logic, since its all private
 */
public class Angle {
	private double rotation;
	private double rotateAmount;
	private long lastUpdateTime;

	public double getRotation() {
		return rotation;
	}

	/**
	 * Checks if the compass should update
	 * @param gameTime  Time
	 * @return  True if the compass should update
	 */
	public boolean shouldUpdate(long gameTime) {
		return this.lastUpdateTime != gameTime;
	}

	/**
	 * Wobbles the angle
	 * @param gameTime      New game time
	 * @param newRotation   Update rotation
	 */
	public void wobble(long gameTime, double newRotation) {
		this.lastUpdateTime = gameTime;
		double diff = newRotation - this.rotation;
		diff = Mth.positiveModulo(diff + 0.5D, 1.0D) - 0.5D;
		this.rotateAmount += diff * 0.1D;
		this.rotateAmount *= 0.8D;
		this.rotation = Mth.positiveModulo(this.rotation + this.rotateAmount, 1.0D);
	}

	/**
	 * Gets the angle for an item frame
	 */
	public static double getFrameRotation(ItemFrame frame) {
		Direction direction = frame.getDirection();
		int dir = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getStep() : 0;
		return Mth.wrapDegrees(180 + direction.get2DDataValue() * 90 + frame.getRotation() * 45 + dir);
	}
}
