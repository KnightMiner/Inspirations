package knightminer.inspirations.tools.client;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BarometerPropertyGetter implements IItemPropertyGetter {
  @Override
  public float call(ItemStack stack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity entityIn) {
    Entity entity = entityIn != null ? entityIn : stack.getItemFrame();
    if (entity == null) {
      return 0;
    }
    World world = clientWorld;
    if (world == null) {
      if (entity.world == null) {
        return 0;
      }
      world = entity.world;
    }

    // if negative position, just 0
    double height = entity.getPosY();
    if (height < 0) {
      return 0;
    }

    int worldHeight = world.getHeight();
    if (height > worldHeight) {
      return 1;
    }
    return (float)Math.sqrt(height / worldHeight);
  }
}
