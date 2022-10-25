package knightminer.inspirations.tools.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class BarometerPropertyGetter implements ItemPropertyFunction {
  @Override
  public float call(ItemStack stack, @Nullable ClientLevel clientWorld, @Nullable LivingEntity entityIn, int seed) {
    Entity entity = entityIn != null ? entityIn : stack.getFrame();
    if (entity == null) {
      return 0;
    }
    Level world = clientWorld;
    if (world == null) {
      world = entity.level;
    }

    // if negative position, just 0
    double height = entity.getY();
    if (height < 0) {
      return 0;
    }

    int worldHeight = world.getMaxBuildHeight();
    if (height > worldHeight) {
      return 1;
    }
    return (float)Math.sqrt(height / worldHeight);
  }
}
