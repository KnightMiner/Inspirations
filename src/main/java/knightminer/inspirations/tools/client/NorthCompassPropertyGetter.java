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

import javax.annotation.Nullable;

public class NorthCompassPropertyGetter implements IItemPropertyGetter {
  private final Angle angle = new Angle();

  @Override
  public float call(ItemStack stack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity entity) {
    ItemFrameEntity frame = stack.getFrame();
    if (frame != null) {
      Direction facing = frame.getDirection();
      if (facing == Direction.DOWN) {
        return frame.getRotation() / 8f;
      } else if (facing == Direction.UP) {
        // Flip 180 degrees.
        return MathHelper.positiveModulo(0.5f + frame.getRotation() / 8f, 1f);
      }
      return MathHelper.positiveModulo(facing.get2DDataValue() / 4f + 0.5f + frame.getRotation() / 8f, 1);
    }

    if (entity == null) {
      return 0;
    }
    World world = clientWorld;
    if (world == null) {
      if (entity.level == null) {
        return 0;
      }
      world = entity.level;
    }

    double angle = MathHelper.positiveModulo(entity.yRot / 360, 1);
    if (entity == Minecraft.getInstance().player) {
      long gameTime = world.getGameTime();
      if (this.angle.shouldUpdate(gameTime)) {
        this.angle.wobble(gameTime, angle);
      }
      return (float)this.angle.getRotation();
    }
    return (float)angle;
  }
}
