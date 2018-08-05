package knightminer.inspirations.tools.client;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BarometerGetter implements IItemPropertyGetter {
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

		// if negative position, just 0
		if(entity.posY < 0) {
			return 0;
		}

		double height = entity.posY;
		int worldHeight = world.getHeight();
		if(height > worldHeight) {
			return 1;
		}
		return (float)Math.sqrt(height/worldHeight);
	}
}
