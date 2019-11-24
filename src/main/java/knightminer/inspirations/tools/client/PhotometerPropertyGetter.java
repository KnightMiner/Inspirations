package knightminer.inspirations.tools.client;

import net.minecraft.client.Minecraft;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class PhotometerPropertyGetter implements IItemPropertyGetter {

	@OnlyIn(Dist.CLIENT)
	@Override
	public float call(ItemStack stack, @Nullable World world, @Nullable LivingEntity entityIn) {
		Entity entity = entityIn != null ? entityIn : stack.getItemFrame();
		if(entity == null) {
			return 0;
		}
		if(world == null) {
			if(entity.world == null) {
				return 0;
			}
			world = entity.world;
		}

		// if currently holding the item, use the block the player is looking at
		BlockPos pos = null;
		if (entity == Minecraft.getInstance().player) {
			PlayerEntity player = Minecraft.getInstance().player;
			if (player.getHeldItemMainhand() == stack || player.getHeldItemOffhand() == stack) {
				RayTraceResult trace = Minecraft.getInstance().objectMouseOver;
				if (trace != null && trace.getType() == RayTraceResult.Type.BLOCK) {
					pos = ((BlockRayTraceResult)trace).getPos();
					if (world.getBlockState(pos).isOpaqueCube(world, pos)) {
						pos = pos.offset(((BlockRayTraceResult) trace).getFace());
					}
				}
			}
		}
		// if any part failed, just use the entity position
		if (pos == null) {
			pos = new BlockPos(entity);
		}

		// only use block light, skylight is not too useful
		return world.getLightFor(LightType.BLOCK, pos);
	}
}
