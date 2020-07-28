package knightminer.inspirations.recipes.client;

import net.minecraft.client.particle.BubbleParticle;
import net.minecraft.client.world.ClientWorld;

public class BoilingParticle extends BubbleParticle {

	public BoilingParticle(ClientWorld world, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed) {
		super(world, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed);
		this.particleAlpha = 0.5f;
		this.canCollide = false;
	}

	@Override
	public void tick() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.move(this.motionX, this.motionY, this.motionZ);
		this.motionX *= 0.8500000238418579D;
		this.motionY *= 0.8500000238418579D;
		this.motionZ *= 0.8500000238418579D;

		if (this.maxAge-- <= 0) {
			this.setExpired();
		}
	}

	@Override
	public void move(double x, double y, double z) {
		this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
		this.posX += x;
		this.posY += y;
		this.posZ += z;
	}
}
