package knightminer.inspirations.tools.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class NorthCompassPropertyGetter implements ItemPropertyFunction {
  private final Angle angle = new Angle();

  @Override
  public float call(ItemStack stack, @Nullable ClientLevel clientWorld, @Nullable LivingEntity entity, int seed) {
    ItemFrame frame = stack.getFrame();
    if (frame != null) {
      Direction facing = frame.getDirection();
      if (facing == Direction.DOWN) {
        return frame.getRotation() / 8f;
      } else if (facing == Direction.UP) {
        // Flip 180 degrees.
        return Mth.positiveModulo(0.5f + frame.getRotation() / 8f, 1f);
      }
      return Mth.positiveModulo(facing.get2DDataValue() / 4f + 0.5f + frame.getRotation() / 8f, 1);
    }

    if (entity == null) {
      return 0;
    }
    Level world = clientWorld;
    if (world == null) {
      world = entity.level;
    }

    double angle = Mth.positiveModulo(entity.getYRot() / 360, 1);
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
