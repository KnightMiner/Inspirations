package knightminer.inspirations.tools.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class PhotometerPropertyGetter implements IItemPropertyGetter {
  @Override
  public float call(ItemStack stack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity entityIn) {
    Entity entity = entityIn != null ? entityIn : stack.getFrame();
    World world = clientWorld;
    if (entity == null) {
      return 0;
    }
    if (world == null) {
      if (entity.level == null) {
        return 0;
      }
      world = entity.level;
    }

    // if currently holding the item, use the block the player is looking at
    BlockPos pos = null;
    if (entity == Minecraft.getInstance().player) {
      PlayerEntity player = Minecraft.getInstance().player;
      if (player.getMainHandItem() == stack || player.getOffhandItem() == stack) {
        RayTraceResult trace = Minecraft.getInstance().hitResult;
        if (trace != null && trace.getType() == RayTraceResult.Type.BLOCK) {
          pos = ((BlockRayTraceResult)trace).getBlockPos();
          if (world.getBlockState(pos).isSolidRender(world, pos)) {
            pos = pos.relative(((BlockRayTraceResult)trace).getDirection());
          }
        }
      }
    }
    // if any part failed, just use the entity position
    if (pos == null) {
      pos = entity.blockPosition();
    }

    // only use block light, skylight is not too useful
    return world.getBrightness(LightType.BLOCK, pos);
  }
}
