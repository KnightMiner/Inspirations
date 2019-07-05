package knightminer.inspirations.tools.client;

import knightminer.inspirations.Inspirations;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class PhotometerGetter implements IItemPropertyGetter {
	@Override
	@SideOnly(Side.CLIENT)
	public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entityIn) {
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
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)entity;
			if (player.getHeldItemMainhand() == stack || player.getHeldItemOffhand() == stack) {
				RayTraceResult trace = Minecraft.getMinecraft().objectMouseOver;
				if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK) {
					pos = trace.getBlockPos();
					if (world.isBlockFullCube(pos)) {
						pos = pos.offset(trace.sideHit);
					}
				}
			}
		}
		// if any part failed, just use the entity position
		if (pos == null) {
			pos = new BlockPos(entity.posX, entity.posY, entity.posZ);
		}

		// only use block light, skylight is not too useful
		return world.getLightFromNeighborsFor(EnumSkyBlock.BLOCK, pos);
	}
}
