package knightminer.inspirations.recipes.client;

import net.minecraft.client.particle.BubbleParticle;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class BoilingParticle extends BubbleParticle {

  public BoilingParticle(ClientWorld world, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed) {
    super(world, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed);
    this.alpha = 0.5f;
    this.hasPhysics = false;
  }

  @Override
  public void tick() {
    this.xo = this.x;
    this.yo = this.y;
    this.zo = this.z;
    this.move(this.xd, this.yd, this.zd);
    this.xd *= 0.8500000238418579D;
    this.yd *= 0.8500000238418579D;
    this.zd *= 0.8500000238418579D;

    if (this.lifetime-- <= 0) {
      this.remove();
    }
  }

  @Override
  public void move(double x, double y, double z) {
    this.setBoundingBox(this.getBoundingBox().move(x, y, z));
    this.x += x;
    this.y += y;
    this.z += z;
  }

  public static class Factory implements IParticleFactory<BasicParticleType> {
    private final IAnimatedSprite spriteSet;
    public Factory(IAnimatedSprite spriteSet) {
      this.spriteSet = spriteSet;
    }

    @Override
    public Particle createParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      BoilingParticle bubble = new BoilingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
      bubble.pickSprite(this.spriteSet);
      return bubble;
    }
  }
}
